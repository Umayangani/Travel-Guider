import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";

const Sidebar = ({ adminName = "Admin" }) => {
  const [openMenu, setOpenMenu] = useState("");

  const toggleMenu = (menuName) => {
    setOpenMenu(openMenu === menuName ? "" : menuName);
  };

  const Chevron = ({ isOpen }) => (
    <img
      src={isOpen ? "/icons/chevron-up.png" : "/icons/chevron-down.png"}
      alt=""
      className="chevron-icon"
    />
  );

  return (
    <div className="sidebar">
      <div className="sidebar-header">Hi, {adminName}</div>

      <div className="menu-item">
        <NavLink
          to="/"
          end
          className={({ isActive }) => (isActive ? "menu-title active" : "menu-title")}
        >
          <div className="menu-label">
            <img src="/icons/dashboard.png" alt="Dashboard" className="menu-icon" />
            Dashboard
          </div>
        </NavLink>
      </div>

      <div className="menu-item">
        <div onClick={() => toggleMenu("place")} className="menu-title">
          <div className="menu-label">
            <img src="/icons/location.svg" alt="Place" className="menu-icon" />
            Place
          </div>
          <Chevron isOpen={openMenu === "place"} />
        </div>
        {openMenu === "place" && (
          <div className="submenu">
            <NavLink
              to="/add-place"
              className={({ isActive }) =>
                isActive ? "submenu-item active" : "submenu-item"
              }
            >
              Add Place
            </NavLink>
            <NavLink
              to="/edit-place"
              className={({ isActive }) =>
                isActive ? "submenu-item active" : "submenu-item"
              }
            >
              Edit Place
            </NavLink>
          </div>
        )}
      </div>

      {/* Other menus */}
      <div className="menu-item">
        <div onClick={() => toggleMenu("train")} className="menu-title">
          <div className="menu-label">
            <img src="/icons/train.png" alt="Train" className="menu-icon" />
            Train
          </div>
          <Chevron isOpen={openMenu === "train"} />
        </div>
        {openMenu === "train" && (
            <div className="submenu">
            <NavLink to="/add-train" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
              Add Train
            </NavLink>
         <NavLink to="/edit-train" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
              Edit Train
        </NavLink>
         </div>
        )}
      </div>

      <div className="menu-item">
        <div onClick={() => toggleMenu("bus")} className="menu-title">
          <div className="menu-label">
            <img src="/icons/bus.png" alt="Bus" className="menu-icon" />
            Bus
          </div>
          <Chevron isOpen={openMenu === "bus"} />
        </div>
        {openMenu === "bus" && (
  <div className="submenu">
    <NavLink to="/add-bus" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
      Add Bus
    </NavLink>
    <NavLink to="/edit-bus" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
      Edit Bus
    </NavLink>
  </div>
)}
      </div>

      <div className="menu-item">
        <div onClick={() => toggleMenu("user")} className="menu-title">
          <div className="menu-label">
            <img src="/icons/users.png" alt="Users" className="menu-icon" />
            User Management
          </div>
          <Chevron isOpen={openMenu === "user"} />
        </div>
        {openMenu === "user" && (
  <div className="submenu">
    <NavLink to="/add-user" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
      Add User
    </NavLink>
    <NavLink to="/manage-users" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
      Manage Users
    </NavLink>
  </div>
)}
      </div>

      <div className="menu-item">
        <div onClick={() => toggleMenu("admin")} className="menu-title">
          <div className="menu-label">
            <img src="/icons/admin.png" alt="Admin" className="menu-icon" />
            Admin Management
          </div>
          <Chevron isOpen={openMenu === "admin"} />
        </div>
        {openMenu === "admin" && (
  <div className="submenu">
    <NavLink to="/add-admin" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
      Add Admin
    </NavLink>
    <NavLink to="/manage-admins" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
      Manage Admins
    </NavLink>
  </div>
)}
      </div>
      <div className="menu-item">
        <div className="menu-title">
         <div className="menu-label">
           <img src="/icons/settings.png" alt="Settings" className="menu-icon" />
           <NavLink to="/Settings" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>
             Settings
            </NavLink>
          </div>
        </div>
      </div>





      <div className="menu-item">
        <div className="menu-title">
          <div className="menu-label">
            <img src="/icons/logout.png" alt="Logout" className="menu-icon" />
            Logout
          </div>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
