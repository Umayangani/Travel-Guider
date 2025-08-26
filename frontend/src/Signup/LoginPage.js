import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import "./LoginPage.css";

function LoginPage({ onNavigate, onClose }) {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const handleRegisterClick = (e) => {
    e.preventDefault();
    onNavigate('signup');
  };

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const handleCloseClick = () => {
    onClose();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
  // Use a config for API base URL
  const { API_BASE_URL } = require("../api/config");
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });
      const data = await response.json();
      console.log('Backend response:', data); // Debug log
      if (!response.ok) {
        setError(data.message || JSON.stringify(data) || "Login failed");
        return;
      }
      
      // Store the token in localStorage
      if (data.token) {
        localStorage.setItem('token', data.token);
        console.log('Token stored:', data.token);
      }
      
      if (!data.role) {
        setError("No role returned from backend. Check backend response format.");
        return;
      }
      if (data.role === "admin") {
        onClose();
        navigate("/admin"); // Show sidebar + dashboard after admin login
      } else if (data.role === "user") {
        onClose();
        navigate("/user");
      } else {
        setError(`Unknown role: ${data.role}`);
      }
    } catch (err) {
      console.error('Login error:', err);
      setError("Network error. Please try again.");
    }
  };

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="login-container">
        <div className="login-card">
          <button className="close-btn" onClick={handleCloseClick}>
            ✕
          </button>
          <div className="form-section">
            <h2>Sign In</h2>
            <form onSubmit={handleSubmit}>
              <div className="input-group">
                <span className="icon">📧</span>
                <input
                  type="email"
                  placeholder="Your Email"
                  required
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                />
              </div>
              <div className="input-group">
                <span className="icon">🔒</span>
                <input
                  type="password"
                  placeholder="Password"
                  required
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                />
              </div>
              {error && <div className="error-message">{error}</div>}
              <div className="forgot-password">
                <a href="#">Forgot your password?</a>
              </div>
              <button type="submit" className="login-btn">Sign In</button>
            </form>
          </div>

          <div className="image-section">
            <img src="./media/login.jpg" alt="Desk with laptop" />
            <button className="switch-btn" onClick={handleRegisterClick}>
              Are you new to here?
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;