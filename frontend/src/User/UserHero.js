import React, { useState, useEffect, useRef } from "react";
import "./UserHero.css";

const images = [
  "/media/kandy.jpg",
  "/media/nineArch.jpg",
  "/media/sigiriya.jpeg"
];

function UserHero({ children }) {
  const [bgIndex, setBgIndex] = useState(0);
  const intervalRef = useRef();

  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setBgIndex((prev) => (prev + 1) % images.length);
    }, 5000);
    return () => clearInterval(intervalRef.current);
  }, []);

  return (
    <div
      className="user-hero"
      style={{
        width: '100vw',
        height: '100vh',
        minHeight: '400px',
        background: `linear-gradient(rgba(0,0,0,0.25),rgba(0,0,0,0.25)), url(${images[bgIndex]}) center center / cover no-repeat`,
        position: 'relative',
        overflow: 'hidden',
        transition: 'background-image 0.7s ease-in-out'
      }}
    >
      {/* Overlay content (centered) */}
      <div
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 2,
          pointerEvents: 'none',
        }}
      >
        {/* Only render children if provided */}
        {children && (
          <div style={{ pointerEvents: 'auto', width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            {children}
          </div>
        )}
      </div>
      {/* Spacer to reserve height */}
      <div style={{ height: '100vh', minHeight: '400px' }}></div>
    </div>
  );
}

export default UserHero;
