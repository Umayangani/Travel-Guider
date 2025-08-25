import React, { useState } from "react";
import "./AddPlace.css"; // Reuse existing CSS

const Settings = () => {
  const [profile, setProfile] = useState({
    name: "",
    email: "",
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: "",
    darkMode: false,
    emailNotifications: true,
  });

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setProfile((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // You can send separate requests to update profile, password, and preferences
    alert("✅ Settings saved (implement API calls as needed).");
  };

  return (
    <div className="add-place-container">
      <h2>Settings</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <h3>Profile Information</h3>
          <label>Name:</label>
          <input
            name="name"
            value={profile.name}
            onChange={handleInputChange}
          />

          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={profile.email}
            onChange={handleInputChange}
          />
        </div>

        <div className="form-section">
          <h3>Change Password</h3>
          <label>Current Password:</label>
          <input
            type="password"
            name="currentPassword"
            value={profile.currentPassword}
            onChange={handleInputChange}
          />

          <label>New Password:</label>
          <input
            type="password"
            name="newPassword"
            value={profile.newPassword}
            onChange={handleInputChange}
          />

          <label>Confirm New Password:</label>
          <input
            type="password"
            name="confirmNewPassword"
            value={profile.confirmNewPassword}
            onChange={handleInputChange}
          />
        </div>

        <div className="form-section">
          <h3>Preferences</h3>
          <label>
            <input
              type="checkbox"
              name="darkMode"
              checked={profile.darkMode}
              onChange={handleInputChange}
            />
            Enable Dark Mode
          </label>

          <label>
            <input
              type="checkbox"
              name="emailNotifications"
              checked={profile.emailNotifications}
              onChange={handleInputChange}
            />
            Receive Email Notifications
          </label>
        </div>

        <button type="submit">Save Settings</button>
      </form>
    </div>
  );
};

export default Settings;
