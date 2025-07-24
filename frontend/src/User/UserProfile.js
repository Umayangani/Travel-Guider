import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./UserProfile.css";

export default function UserProfile() {
  const [user, setUser] = useState({
    name: "",
    email: "",
    dob: "",
    address: "",
  });
  const [editing, setEditing] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
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
    setSelectedFile(null);
    setPreviewUrl(null);
    // Reset any unsaved changes by refetching user data or resetting state
  };

  const handleFileSelect = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) { // 5MB limit
        alert('File size must be less than 5MB');
        return;
      }
      
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
      if (!allowedTypes.includes(file.type)) {
        alert('Please select a valid image file (JPEG, PNG, GIF)');
        return;
      }

      setSelectedFile(file);
      
      // Create preview URL
      const reader = new FileReader();
      reader.onload = (e) => {
        setPreviewUrl(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const uploadProfilePicture = async () => {
    if (!selectedFile) return null;

    const formData = new FormData();
    formData.append('avatar', selectedFile);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/user/upload-avatar', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Avatar upload response:', result);
        return result.avatarUrl;
      } else {
        const errorData = await response.json();
        console.error('Avatar upload error:', errorData);
        throw new Error('Failed to upload image');
      }
    } catch (error) {
      console.error('Error uploading avatar:', error);
      alert('Failed to upload profile picture');
      return null;
    }
  };

  const handleSave = async () => {
    try {
      let updatedUser = { ...user };

      // Upload avatar if a new file is selected
      if (selectedFile) {
        console.log('Uploading avatar...');
        const avatarUrl = await uploadProfilePicture();
        console.log('Received avatar URL:', avatarUrl);
        if (avatarUrl) {
          updatedUser.avatar = avatarUrl;
          // Update the user state immediately with new avatar
          setUser(prevUser => ({
            ...prevUser,
            avatar: avatarUrl
          }));
          console.log('Updated user state with new avatar:', avatarUrl);
        } else {
          return; // Stop if avatar upload failed
        }
      }

      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/user/profile', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedUser)
      });

      if (response.ok) {
        setUser(updatedUser);
        setEditing(false);
        setSelectedFile(null);
        setPreviewUrl(null);
        
        // Force refresh user data from backend to ensure avatar is updated
        try {
          const token = localStorage.getItem('token');
          const refreshResponse = await fetch('http://localhost:8080/api/user/profile', {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          
          if (refreshResponse.ok) {
            const refreshedUserData = await refreshResponse.json();
            setUser({
              name: refreshedUserData.name || "",
              email: refreshedUserData.email || "",
              dob: refreshedUserData.dob || "",
              address: refreshedUserData.address || "",
              avatar: refreshedUserData.avatar || "https://i.pravatar.cc/150?img=3"
            });
            console.log('User data refreshed with avatar:', refreshedUserData.avatar);
          }
        } catch (refreshError) {
          console.error('Failed to refresh user data:', refreshError);
        }
        
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
              <div className="avatar-container">
                <img 
                  key={user.avatar + Date.now()} // Force re-render when avatar changes
                  src={previewUrl || user.avatar || "https://i.pravatar.cc/150?img=3"} 
                  alt="Profile" 
                  className="profile-avatar-large" 
                  onError={(e) => {
                    console.log('Image failed to load:', e.target.src);
                    e.target.src = "https://i.pravatar.cc/150?img=3";
                  }}
                  onLoad={() => {
                    console.log('Image loaded successfully:', previewUrl || user.avatar);
                  }}
                />
                {editing && (
                  <div className="avatar-upload-overlay">
                    <input
                      type="file"
                      id="avatar-upload"
                      accept="image/*"
                      onChange={handleFileSelect}
                      className="avatar-upload-input"
                    />
                    <label htmlFor="avatar-upload" className="avatar-upload-btn">
                      📷 Change Photo
                    </label>
                  </div>
                )}
              </div>
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
                    <div className="form-group">
                      <label>Address</label>
                      <input
                        name="address"
                        value={user.address}
                        onChange={handleChange}
                        disabled={!editing}
                        className="profile-input"
                        placeholder="Enter your address"
                      />
                    </div>
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
