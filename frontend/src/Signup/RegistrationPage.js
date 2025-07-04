import React from 'react';
import "./RegistrationPage.css";

function RegistrationPage({ onNavigate, onClose }) {
  const handleLoginClick = (e) => {
    e.preventDefault();
    onNavigate('login');
  };

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="registration-container">
        <div className="registration-card">
          <button className="close-btn" onClick={onClose}>✕</button>
          <div className="form-section">
            <h2>Sign Up</h2>
            <form>
              <div className="input-group">
                <span className="icon">👤</span>
                <input type="text" placeholder="Your Name" required />
              </div>
              <div className="input-group">
                <span className="icon">📧</span>
                <input type="email" placeholder="Your Email" required />
              </div>
              <div className="input-group">
                <span className="icon">🔒</span>
                <input type="password" placeholder="Password" required />
              </div>
               <div className="input-group">
                <span className="icon">🔒</span>
                <input type="password" placeholder="Re-enter Password" required />
              </div>
              <label className="checkbox-group">
                <input type="checkbox" required />
                I agree to the <a href="#">Terms of Service</a>
              </label>
              <button type="submit" className="register-btn">Register</button>
            </form>
          </div>
          <div className="image-section">
            <img src="/media/registration.jpg" alt="Register" />
            <button className="switch-btn" onClick={handleLoginClick}>
              Already a member?
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RegistrationPage;