import React, { useState, useEffect } from "react";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";

const Sidebar = () => {
  const [openMenu, setOpenMenu] = useState("");
  const [adminName, setAdminName] = useState("Admin");

  // Fetch admin data from database after login
  useEffect(() => {
    const fetchAdminData = async () => {
      try {
        const token = localStorage.getItem('token');
        console.log('=== Sidebar Admin Debug ===');
        console.log('Token found:', token ? 'Yes' : 'No');
        
        if (token) {
          const response = await fetch('http://localhost:8080/api/user/profile', {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          
          console.log('Sidebar Response status:', response.status);
          
          if (response.ok) {
            const userData = await response.json();
            console.log('Sidebar Admin data received:', userData);
            setAdminName(userData.name || "Admin");
          } else {
            console.log('Sidebar Response not ok:', response.status);
          }
        } else {
          console.log('Sidebar: No token found in localStorage');
        }
      } catch (error) {
        console.error('Sidebar: Error fetching admin data:', error);
      }
    };

    fetchAdminData();
  }, []);

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
          to="/admin"
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
              to="/admin/add-place"
              className={({ isActive }) =>
                isActive ? "submenu-item active" : "submenu-item"
              }
            >
              Add Place
            </NavLink>
            <NavLink
              to="/admin/edit-place"
              className={({ isActive }) =>
                isActive ? "submenu-item active" : "submenu-item"
              }
            >
              Edit Place
            </NavLink>
            <NavLink
              to="/admin/csv-import"
              className={({ isActive }) =>
                isActive ? "submenu-item active" : "submenu-item"
              }
            >
              Import CSV
            </NavLink>
          </div>
        )}
      </div>

      <div className="menu-item">
        <div onClick={() => toggleMenu("event")} className="menu-title">
          <div className="menu-label">
            <img src="/icons/event.png" alt="Event & Festival" className="menu-icon" />
            Event & Festival
          </div>
          <Chevron isOpen={openMenu === "event"} />
        </div>
        {openMenu === "event" && (
          <div className="submenu">
            <NavLink to="/admin/add-event" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Add Event</NavLink>
            <NavLink to="/admin/manage-event" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Manage Event</NavLink>
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
            <NavLink to="/admin/add-train" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Add Train</NavLink>
         <NavLink to="/admin/edit-train/" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Edit Train</NavLink>
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
    <NavLink to="/admin/add-bus" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Add Bus</NavLink>
    <NavLink to="/admin/edit-bus" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Edit Bus</NavLink>
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
    <NavLink to="/admin/add-user" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Add User</NavLink>
    <NavLink to="/admin/manage-users" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Manage Users</NavLink>
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
    <NavLink to="/admin/add-admin" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Add Admin</NavLink>
    <NavLink to="/admin/manage-admins" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Manage Admins</NavLink>
  </div>
)}
      </div>
      <div className="menu-item">
        <div className="menu-title">
         <div className="menu-label">
           <img src="/icons/settings.png" alt="Settings" className="menu-icon" />
           <NavLink to="/admin/settings" className={({ isActive }) => (isActive ? "submenu-item active" : "submenu-item")}>Settings</NavLink>
          </div>
        </div>
      </div>





      <div className="menu-item">
        <div className="menu-title" onClick={() => { window.location.href = "/"; }} style={{ cursor: 'pointer' }}>
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
