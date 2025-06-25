import { useState, useEffect, useRef } from "react";
import "./HeaderHome.css";

function HeaderHome() {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const toggleDropdown = () => setDropdownOpen((prev) => !prev);

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    }

    if (dropdownOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [dropdownOpen]);

  return (
    <header className="headerHome">
      <div className="left">
        <img src="logo.png" alt="Logo" className="logo" />
        <span className="site-name">Trevora</span>
      </div>

      <nav className="center">
        <a href="#">Home</a>
        <a href="#">About</a>
      </nav>

      <div className="right" ref={dropdownRef}>
        <button
          className="dropdown-toggle"
          onClick={toggleDropdown}
          aria-haspopup="true"
          aria-expanded={dropdownOpen}
        >
          <img
            src="/icons/user.svg"
            alt="User Icon"
            className="user-icon"
            aria-hidden="true"
          />
        </button>

        {dropdownOpen && (
          <div className="dropdown-menu">
            <a href="#">Login</a>
            <a href="#">Register</a>
          </div>
        )}
      </div>
    </header>
  );
}

export default HeaderHome;
