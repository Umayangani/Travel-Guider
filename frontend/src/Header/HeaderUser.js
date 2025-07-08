import React, { useState, useEffect, useRef } from "react";
import ReactDOM from "react-dom";
import "./HeaderUser.css";

const images = [
  "/media/kandy.jpg",
  "/media/ninearch.jpg",
  "/media/sigiriya.jpg"
];

function HeaderUser({ userName = "User", onNavigate = () => {}, onLogout = () => {} }) {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [bgIndex, setBgIndex] = useState(0);
  const intervalRef = useRef();
  const dropdownRef = useRef();
  const greetingRef = useRef();

  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setBgIndex((prev) => (prev + 1) % images.length);
    }, 3000);
    return () => clearInterval(intervalRef.current);
  }, []);

  const toggleDropdown = () => setDropdownOpen((open) => !open);

  // Close dropdown on outside click
  useEffect(() => {
    if (!dropdownOpen) return;
    function handleClick(e) {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target) &&
        greetingRef.current &&
        !greetingRef.current.contains(e.target)
      ) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, [dropdownOpen]);

  return (
    <header
      className="headerUser"
      style={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        width: '100vw',
        maxWidth: '100vw',
        background: 'transparent',
        color: '#fff',
        zIndex: 100,
        borderBottom: 'none',
        boxShadow: 'none',
        overflowX: 'hidden'
      }}
    >
      <div className="headerUser-left" style={{ zIndex: 2 }}>
        <img src="/logo.png" alt="Logo" className="headerUser-logo" />
        <span className="headerUser-site" style={{ color: '#fff', textShadow: '0 2px 12px #000, 0 0 2px #000' }}>Trevora</span>
      </div>
      <div className="headerUser-right" style={{ zIndex: 2, position: 'relative' }}>
        <div className="headerUser-greeting" ref={greetingRef} onClick={toggleDropdown}>
          Welcome back, {userName && userName !== "User" ? userName : "User"}
          <img
            src="/icons/chevron-down.png"
            alt="Dropdown"
            className={`headerUser-chevron${dropdownOpen ? " open" : ""}`}
          />
        </div>
        {dropdownOpen && ReactDOM.createPortal(
          <div className="headerUser-dropdown" ref={dropdownRef} style={{position: 'absolute', top: '70px', right: '30px'}}>
            <button onClick={() => { setDropdownOpen(false); onNavigate("profile"); }}>Profile</button>
            <button onClick={() => { setDropdownOpen(false); onNavigate("ongoing-trips"); }}>Ongoing Trips</button>
            <button onClick={() => { setDropdownOpen(false); onNavigate("history"); }}>History</button>
            <button onClick={() => { setDropdownOpen(false); onNavigate("explore-history"); }}>Explore History</button>
            <button onClick={() => { setDropdownOpen(false); onLogout(); }}>Log out</button>
          </div>,
          document.body
        )}
      </div>
    </header>
  );
}

export default HeaderUser;
