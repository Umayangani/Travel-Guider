import React from "react";
import "./FloatingIcon.css";

const FloatingIcon = ({ icon = "/icons/chat.png", alt = "Chat", onClick }) => (
  <div className="floating-icon" onClick={onClick}>
    <img src={icon} alt={alt} style={{ width: 48, height: 48 }} />
  </div>
);

export default FloatingIcon;
