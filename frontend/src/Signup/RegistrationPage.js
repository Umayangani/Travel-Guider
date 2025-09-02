import React, { useState } from 'react';
import "./RegistrationPage.css";

function RegistrationPage({ onNavigate, onClose }) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rePassword, setRePassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleLoginClick = (e) => {
    e.preventDefault();
    onNavigate('login');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (password !== rePassword) {
      setError("Passwords do not match");
      return;
    }
    try {
  // Use a config for API base URL
  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8090";
  const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password }),
      });
      const data = await response.json();
      if (!response.ok) {
        setError(data.message || "Registration failed");
      } else {
        setSuccess("Registration successful! You can now log in.");
        setName(""); setEmail(""); setPassword(""); setRePassword("");
      }
    } catch (err) {
      setError("Network error. Please try again.");
    }
  };

  return (
    <div className="modal-overlay">
      <div className="registration-container">
        <div className="registration-card">
          {/* Close button positioned at top-right */}
          <button className="close-btn" onClick={onClose}>✕</button>
          
          <div className="form-section">
            <h2>Create Your Account</h2>
            <p className="form-subtitle">Join us to start your travel journey</p>
            <form onSubmit={handleSubmit}>
              <div className="input-group">
                <span className="icon">👤</span>
                <input type="text" placeholder="Your Name" required value={name} onChange={e => setName(e.target.value)} />
              </div>
              <div className="input-group">
                <span className="icon">📧</span>
                <input type="email" placeholder="Your Email" required value={email} onChange={e => setEmail(e.target.value)} />
              </div>
              <div className="input-group">
                <span className="icon">🔒</span>
                <input type="password" placeholder="Password" required value={password} onChange={e => setPassword(e.target.value)} />
              </div>
               <div className="input-group">
                <span className="icon">🔒</span>
                <input type="password" placeholder="Re-enter Password" required value={rePassword} onChange={e => setRePassword(e.target.value)} />
              </div>
              {error && <div className="error-message">{error}</div>}
              {success && <div className="success-message">{success}</div>}
              <label className="checkbox-group">
                <input type="checkbox" required />
                I agree to the <button type="button" className="terms-link" onClick={() => {}}>Terms of Service</button>
              </label>
              <button type="submit" className="register-btn">Register</button>
            </form>
          </div>
          <div className="image-section">
            <div className="image-container">
              <img src="/media/registration.jpg" alt="Register" />
            </div>
            <div className="switch-section">
              <p>Already have an account?</p>
              <button className="switch-btn" onClick={handleLoginClick}>
                Sign In Here
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RegistrationPage;