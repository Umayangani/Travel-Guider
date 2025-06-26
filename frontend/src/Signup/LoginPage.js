import React from 'react';
import "./LoginPage.css";

function LoginPage({ onNavigate, onClose }) {
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

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="login-container">
        <div className="login-card">
          <button className="close-btn" onClick={handleCloseClick}>
            ✕
          </button>
          <div className="form-section">
            <h2>Sign In</h2>
            <form>
              <div className="input-group">
                <span className="icon">📧</span>
                <input type="email" placeholder="Your Email" required />
              </div>
              <div className="input-group">
                <span className="icon">🔒</span>
                <input type="password" placeholder="Password" required />
              </div>
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