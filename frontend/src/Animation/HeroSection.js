import './HeroSection.css';


import React from 'react';

function HeroSection() {
  // State to track if hero is in view
  const [showButton, setShowButton] = React.useState(true);
  const heroRef = React.useRef(null);

  React.useEffect(() => {
    const handleScroll = () => {
      if (!heroRef.current) return;
      const rect = heroRef.current.getBoundingClientRect();
      // Show button only if hero section is visible at least 50px from top
      setShowButton(rect.bottom > 50 && rect.top < window.innerHeight - 50);
    };
    window.addEventListener('scroll', handleScroll);
    handleScroll();
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <section ref={heroRef} className="hero custom-hero" style={{ position: 'relative', minHeight: '100vh', background: 'linear-gradient(rgba(0,0,0,0.15),rgba(0,0,0,0.15)), #032a06', width: '100vw', overflow: 'hidden' }}>
      <div className="hero-bg-image" style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', zIndex: 0 }}>
        <video
          src="/media/animation.mp4"
          autoPlay
          loop
          muted
          className="hero-video"
          style={{ width: '100%', height: '100%', objectFit: 'cover', opacity: 0.7 }}
        />
      </div>
      <div className="hero-content" style={{ position: 'relative', zIndex: 2, textAlign: 'center', color: '#fff', paddingTop: '1rem', minHeight: '60vh', height: 'calc(100vh - 80px)', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        {/* You can add any overlay or branding here if needed */}
      </div>
      {showButton && (
        <div style={{ position: 'relative', left: 0, bottom: 0, width: '100vw', display: 'flex', justifyContent: 'center', zIndex: 20, pointerEvents: 'none' }}>
          <button className="get-started-btn" style={{ background: '#062E03', color: '#fff', fontWeight: 600, fontSize: '1.1rem', border: 'none', borderRadius: '7px', padding: '0.8rem 2.5rem', boxShadow: '0 2px 8px rgba(0,0,0,0.15)', cursor: 'pointer', margin: '2rem 0', pointerEvents: 'auto' }}>Get Started</button>
        </div>
      )}
    </section>
  );
}

export default HeroSection;
