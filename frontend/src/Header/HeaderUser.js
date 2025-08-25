import React, { useState, useEffect, useRef } from "react";
import ReactDOM from "react-dom";
import "./HeaderUser.css";

const images = [
  "/media/kandy.jpg",
  "/media/ninearch.jpg",
  "/media/sigiriya.jpg"
];

function HeaderUser({ onNavigate = () => {}, onLogout = () => {} }) {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [bgIndex, setBgIndex] = useState(0);
  const [userName, setUserName] = useState("User");
  const intervalRef = useRef();
  const dropdownRef = useRef();
  const greetingRef = useRef();

  // Cycle background images for immersive effect (optional, for future hero integration)
  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setBgIndex((prev) => (prev + 1) % images.length);
    }, 3000);
    return () => clearInterval(intervalRef.current);
  }, []);

  // Fetch user data from database after login
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem('token');
        console.log('=== HeaderUser Debug ===');
        console.log('Token found:', token ? 'Yes' : 'No');
        console.log('Token value:', token);
        
        if (token) {
          const response = await fetch('http://localhost:8080/api/user/profile', {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          
          console.log('Response status:', response.status);
          
          if (response.ok) {
            const userData = await response.json();
            console.log('User data received:', userData);
            console.log('Setting userName to:', userData.name);
            setUserName(userData.name || "User");
          } else {
            console.log('Response not ok:', response.status);
            const errorText = await response.text();
            console.log('Error response:', errorText);
          }
        } else {
          console.log('No token found in localStorage');
        }
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    };

    fetchUserData();
  }, []);

  const toggleDropdown = () => setDropdownOpen((open) => !open);

  const navigateTo = (route) => {
    if (route === "profile") {
      window.location.href = "/profile";
    } else if (route === "messages") {
      window.location.href = "/messages";
    } else {
      onNavigate(route);
    }
  };

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

  // Keyboard accessibility: close dropdown on Escape
  useEffect(() => {
    if (!dropdownOpen) return;
    function handleKey(e) {
      if (e.key === "Escape") setDropdownOpen(false);
    }
    document.addEventListener("keydown", handleKey);
    return () => document.removeEventListener("keydown", handleKey);
  }, [dropdownOpen]);

  // Responsive, modern header with overlay and dropdown portal
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
        overflowX: 'hidden',
        pointerEvents: 'none', // allow overlays to pass through except for content
      }}
    >
      <div className="headerUser-left" style={{ zIndex: 2, pointerEvents: 'auto' }}>
        <img src="/logo.png" alt="Logo" className="headerUser-logo" />
        <span className="headerUser-site" style={{ color: '#fff', textShadow: '0 2px 12px #000, 0 0 2px #000' }}>Trevora</span>
      </div>
      <div className="headerUser-right" style={{ zIndex: 2, position: 'relative', pointerEvents: 'auto' }}>
        <div
          className="headerUser-greeting"
          ref={greetingRef}
          onClick={toggleDropdown}
          tabIndex={0}
          aria-haspopup="true"
          aria-expanded={dropdownOpen}
          style={{ cursor: 'pointer', outline: 'none' }}
          onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') toggleDropdown(); }}
        >
          Welcome back, {userName}
          <img
            src="/icons/chevron-down.png"
            alt="Dropdown"
            className={`headerUser-chevron${dropdownOpen ? " open" : ""}`}
          />
        </div>
        {dropdownOpen && ReactDOM.createPortal(
          <div
            className="headerUser-dropdown"
            ref={dropdownRef}
            style={{
              position: 'absolute',
              top: '70px',
              right: '30px',
              minWidth: 180,
              background: 'rgba(0,0,0,0.85)',
              borderRadius: 12,
              boxShadow: '0 8px 32px rgba(0,0,0,0.25)',
              padding: '12px 0',
              display: 'flex',
              flexDirection: 'column',
              gap: 0,
              zIndex: 9999,
              pointerEvents: 'auto',
              border: '1px solid rgba(255,255,255,0.08)'
            }}
          >
            <button onClick={() => { setDropdownOpen(false); navigateTo("profile"); }}>Profile</button>
            <button onClick={() => { setDropdownOpen(false); navigateTo("ongoing-trips"); }}>Ongoing Trips</button>
            <button onClick={() => { setDropdownOpen(false); navigateTo("history"); }}>History</button>
            <button onClick={() => { setDropdownOpen(false); navigateTo("explore-history"); }}>Explore History</button>
            <button onClick={() => {
              setDropdownOpen(false);
              onLogout();
              window.location.href = "/";
            }}>Log out</button>
          </div>,
          document.body
        )}
      </div>
    </header>
  );
}

export default HeaderUser;
