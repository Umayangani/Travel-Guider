import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ProfileDropdown.css";

export default function ProfileDropdown() {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const user = {
    name: "John Doe",
    avatar: "https://i.pravatar.cc/100?img=3",
  };

  return (
    <div className="profile-dropdown-container">
      <div className="profile-dropdown-trigger" onClick={() => setOpen((v) => !v)}>
        <img src={user.avatar} alt="avatar" className="profile-dropdown-avatar" />
        <span className="profile-dropdown-name">{user.name}</span>
      </div>
      {open && (
        <div className="profile-dropdown-menu">
          <button onClick={() => { setOpen(false); navigate("/profile"); }}>
            Profile
          </button>
          <button onClick={() => { setOpen(false); navigate("/profile"); }}>
            Edit Details
          </button>
          <button onClick={() => { setOpen(false); navigate("/profile"); }}>
            Change Password
          </button>
          <button onClick={() => { setOpen(false); /* handle logout */ }}>
            Logout
          </button>
        </div>
      )}
    </div>
  );
}
