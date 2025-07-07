import React, { useState } from "react";
import "./HeaderUser.css";

function HeaderUser({ userName = "User", onNavigate = () => {}, onLogout = () => {} }) {
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const toggleDropdown = () => setDropdownOpen((open) => !open);

  return (
    <header className="headerUser">
      <div className="headerUser-left">
        <img src="/logo.png" alt="Logo" className="headerUser-logo" />
        <span className="headerUser-site">Trevora</span>
      </div>
      <div className="headerUser-right">
        <div className="headerUser-greeting" onClick={toggleDropdown}>
          Hi, {userName && userName !== "User" ? userName : "User"}
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
