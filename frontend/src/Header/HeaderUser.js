import React, { useState, useEffect, useRef } from "react";
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

  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setBgIndex((prev) => (prev + 1) % images.length);
    }, 3000);
    return () => clearInterval(intervalRef.current);
  }, []);

  const toggleDropdown = () => setDropdownOpen((open) => !open);

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
      <div className="headerUser-right" style={{ zIndex: 2 }}>
        <div className="headerUser-greeting" onClick={toggleDropdown}>
          Welcome back, {userName && userName !== "User" ? userName : "User"}
          <img
            src="/icons/chevron-down.png"
            alt="Dropdown"
            className={`headerUser-chevron${dropdownOpen ? " open" : ""}`}
          />
        </div>
        {dropdownOpen && (
          <div className="headerUser-dropdown">
            <button onClick={() => { onNavigate("profile"); setDropdownOpen(false); }}>Profile</button>
            <button onClick={() => { onNavigate("history"); setDropdownOpen(false); }}>History</button>
            <button onClick={() => { onLogout(); setDropdownOpen(false); }}>Log out</button>
          </div>
        )}
      </div>
    </header>
  );
}

export default HeaderUser;
