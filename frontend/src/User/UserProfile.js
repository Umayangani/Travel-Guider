import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./UserProfile.css";

export default function UserProfile() {
  const [user, setUser] = useState({
    name: "",
    email: "",
    phone: "",
    dob: "",
    address: "",
    avatar: "https://i.pravatar.cc/150?img=3",
  });
  const [editing, setEditing] = useState(false);
  const [passwords, setPasswords] = useState({
    current: "",
    new: "",
    confirm: ""
  });
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false
  });
  const [activeTab, setActiveTab] = useState("profile");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Fetch user data from backend
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          const response = await fetch('http://localhost:8080/api/user/profile', {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          
          if (response.ok) {
            const userData = await response.json();
            setUser({
              name: userData.name || "",
              email: userData.email || "",
              phone: userData.phone || "",
              dob: userData.dob || "",
              address: userData.address || "",
              avatar: userData.avatar || "https://i.pravatar.cc/150?img=3"
            });
          }
        }
      } catch (error) {
        console.error('Error fetching user data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  const handleEdit = () => setEditing(true);
  const handleCancel = () => {
    setEditing(false);
    // Reset form if needed
  };

  const handleSave = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/user/profile', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      });

      if (response.ok) {
        setEditing(false);
        alert('Profile updated successfully!');
      } else {
        alert('Failed to update profile');
      }
    } catch (error) {
      console.error('Error updating profile:', error);
      alert('Error updating profile');
    }
  };

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handlePasswordChange = (field, value) => {
    setPasswords({ ...passwords, [field]: value });
  };

  const togglePasswordVisibility = (field) => {
    setShowPasswords({ ...showPasswords, [field]: !showPasswords[field] });
  };

  const handlePasswordUpdate = async () => {
    if (passwords.new !== passwords.confirm) {
      alert('New passwords do not match');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/user/change-password', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          currentPassword: passwords.current,
          newPassword: passwords.new
        })
      });

      if (response.ok) {
        setPasswords({ current: "", new: "", confirm: "" });
        alert('Password updated successfully!');
      } else {
        alert('Failed to update password');
      }
    } catch (error) {
      console.error('Error updating password:', error);
      alert('Error updating password');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  if (loading) {
    return <div className="profile-loading">Loading...</div>;
  }

  return (
    <div className="profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <button className="profile-back-btn" onClick={() => navigate('/user')}>
            ← Back
          </button>
          <h1>My Profile</h1>
        </div>

        <div className="profile-content">
          <div className="profile-sidebar">
            <div className="profile-avatar-section">
              <img src={user.avatar} alt="Profile" className="profile-avatar-large" />
              <h3>{user.name || "User"}</h3>
              <p>{user.email}</p>
            </div>
            
            <nav className="profile-nav">
              <button 
                className={`profile-nav-item ${activeTab === 'profile' ? 'active' : ''}`}
                onClick={() => setActiveTab('profile')}
              >
                📝 Personal Info
              </button>
              <button 
                className={`profile-nav-item ${activeTab === 'security' ? 'active' : ''}`}
                onClick={() => setActiveTab('security')}
              >
                🔒 Security
              </button>
              <button 
                className={`profile-nav-item ${activeTab === 'preferences' ? 'active' : ''}`}
                onClick={() => setActiveTab('preferences')}
              >
                ⚙️ Preferences
              </button>
            </nav>
          </div>

          <div className="profile-main">
            {activeTab === 'profile' && (
              <div className="profile-section">
                <div className="section-header">
                  <h2>Personal Information</h2>
                  {!editing ? (
                    <button className="profile-edit-btn" onClick={handleEdit}>
                      Edit Profile
                    </button>
                  ) : (
                    <div className="edit-actions">
                      <button className="profile-save-btn" onClick={handleSave}>
                        Save Changes
                      </button>
                      <button className="profile-cancel-btn" onClick={handleCancel}>
                        Cancel
                      </button>
                    </div>
                  )}
                </div>

                <div className="profile-form">
                  <div className="form-row">
                    <div className="form-group">
                      <label>Full Name</label>
                      <input
                        name="name"
                        value={user.name}
                        onChange={handleChange}
                        disabled={!editing}
                        className="profile-input"
                        placeholder="Enter your full name"
                      />
                    </div>
                    <div className="form-group">
                      <label>Email Address</label>
                      <input
                        name="email"
                        type="email"
                        value={user.email}
                        onChange={handleChange}
                        disabled={!editing}
                        className="profile-input"
                        placeholder="Enter your email"
                      />
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label>Phone Number</label>
                      <input
                        name="phone"
                        value={user.phone}
                        onChange={handleChange}
                        disabled={!editing}
                        className="profile-input"
                        placeholder="Enter your phone number"
                      />
                    </div>
                    <div className="form-group">
                      <label>Date of Birth</label>
                      <input
                        name="dob"
                        type="date"
                        value={user.dob}
                        onChange={handleChange}
                        disabled={!editing}
                        className="profile-input"
                      />
                    </div>
                  </div>

                  <div className="form-group">
                    <label>Address</label>
                    <textarea
                      name="address"
                      value={user.address}
                      onChange={handleChange}
                      disabled={!editing}
                      className="profile-textarea"
                      placeholder="Enter your address"
                      rows="3"
                    />
                  </div>
                </div>
              </div>
            )}

            {activeTab === 'security' && (
              <div className="profile-section">
                <div className="section-header">
                  <h2>Security Settings</h2>
                </div>

                <div className="security-section">
                  <h3>Change Password</h3>
                  <div className="password-form">
                    <div className="form-group">
                      <label>Current Password</label>
                      <div className="password-input-group">
                        <input
                          type={showPasswords.current ? "text" : "password"}
                          value={passwords.current}
                          onChange={(e) => handlePasswordChange('current', e.target.value)}
                          className="profile-input"
                          placeholder="Enter current password"
                        />
                        <button 
                          type="button"
                          onClick={() => togglePasswordVisibility('current')}
                          className="password-toggle"
                        >
                          {showPasswords.current ? '👁️' : '👁️‍🗨️'}
                        </button>
                      </div>
                    </div>

                    <div className="form-group">
                      <label>New Password</label>
                      <div className="password-input-group">
                        <input
                          type={showPasswords.new ? "text" : "password"}
                          value={passwords.new}
                          onChange={(e) => handlePasswordChange('new', e.target.value)}
                          className="profile-input"
                          placeholder="Enter new password"
                        />
                        <button 
                          type="button"
                          onClick={() => togglePasswordVisibility('new')}
                          className="password-toggle"
                        >
                          {showPasswords.new ? '👁️' : '👁️‍🗨️'}
                        </button>
                      </div>
                    </div>

                    <div className="form-group">
                      <label>Confirm New Password</label>
                      <div className="password-input-group">
                        <input
                          type={showPasswords.confirm ? "text" : "password"}
                          value={passwords.confirm}
                          onChange={(e) => handlePasswordChange('confirm', e.target.value)}
                          className="profile-input"
                          placeholder="Confirm new password"
                        />
                        <button 
                          type="button"
                          onClick={() => togglePasswordVisibility('confirm')}
                          className="password-toggle"
                        >
                          {showPasswords.confirm ? '👁️' : '👁️‍🗨️'}
                        </button>
                      </div>
                    </div>

                    <button className="profile-save-btn" onClick={handlePasswordUpdate}>
                      Update Password
                    </button>
                  </div>
                </div>
              </div>
            )}

            {activeTab === 'preferences' && (
              <div className="profile-section">
                <div className="section-header">
                  <h2>Account Preferences</h2>
                </div>

                <div className="preferences-section">
                  <div className="preference-item">
                    <h4>Email Notifications</h4>
                    <p>Receive updates about your bookings and travel recommendations</p>
                    <label className="toggle-switch">
                      <input type="checkbox" defaultChecked />
                      <span className="toggle-slider"></span>
                    </label>
                  </div>

                  <div className="preference-item">
                    <h4>SMS Notifications</h4>
                    <p>Get important alerts via text message</p>
                    <label className="toggle-switch">
                      <input type="checkbox" />
                      <span className="toggle-slider"></span>
                    </label>
                  </div>

                  <div className="preference-item">
                    <h4>Marketing Communications</h4>
                    <p>Receive promotional offers and travel deals</p>
                    <label className="toggle-switch">
                      <input type="checkbox" defaultChecked />
                      <span className="toggle-slider"></span>
                    </label>
                  </div>

                  <div className="danger-zone">
                    <h3>Danger Zone</h3>
                    <button className="profile-logout-btn" onClick={handleLogout}>
                      Logout from Account
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
