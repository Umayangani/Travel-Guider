import React, { useState } from "react";
import "./UserProfile.css";

const initialUser = {
  name: "John Doe",
  email: "john@example.com",
  avatar: "https://i.pravatar.cc/100?img=3",
};

export default function UserProfile() {
  const [user, setUser] = useState(initialUser);
  const [editing, setEditing] = useState(false);
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleEdit = () => setEditing(true);
  const handleSave = () => setEditing(false);
  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };
  const handlePasswordChange = (e) => setPassword(e.target.value);

  return (
    <div className="profile-container">
      <div className="profile-header">
        <img src={user.avatar} alt="avatar" className="profile-avatar" />
        <div>
          <h2>{user.name}</h2>
          <p>{user.email}</p>
        </div>
        <button className="profile-edit-btn" onClick={handleEdit}>
          Edit
        </button>
      </div>
      {editing && (
        <div className="profile-edit-section">
          <label>
            Name
            <input
              name="name"
              value={user.name}
              onChange={handleChange}
              className="profile-input"
            />
          </label>
          <label>
            Email
            <input
              name="email"
              value={user.email}
              onChange={handleChange}
              className="profile-input"
            />
          </label>
          <button className="profile-save-btn" onClick={handleSave}>
            Save
          </button>
        </div>
      )}
      <div className="profile-password-section">
        <h3>Change Password</h3>
        <input
          type={showPassword ? "text" : "password"}
          value={password}
          onChange={handlePasswordChange}
          className="profile-input profile-password-input"
          placeholder="New password"
          style={{ flex: 1, minWidth: 0 }}
        />
        <button
          className="profile-show-btn"
          onClick={() => setShowPassword((v) => !v)}
        >
          {showPassword ? "Hide" : "Show"}
        </button>
        <button className="profile-save-btn" style={{ marginLeft: 12 }}>
          Update Password
        </button>
      </div>
      <div className="profile-actions">
        <button className="profile-logout-btn">Logout</button>
      </div>
    </div>
  );
}
