import './Footer.css';

function Footer() {
  return (
    <footer className="footer">
      {/* Left */}
      <div className="footer-section">
        <h2 className="footer-brand">Trevora</h2>
        <p className="footer-description">
          Explore Sri Lanka like never before with smart, emotion-based travel plans tailored just for you.
        </p>
        <div className="footer-social">
          <a href="#"><img src="/icons/facebook.svg" alt="Facebook" /></a>
          <a href="#"><img src="/icons/instagram.svg" alt="Instagram" /></a>
          <a href="#"><img src="/icons/linkedin.svg" alt="LinkedIn" /></a>
        </div>
      </div>

      {/* Center */}
      <div className="footer-section">
        <h3>Quick Links</h3>
        <ul className="footer-links">
          <li><a href="#">Home</a></li>
          <li><a href="#">Destinations</a></li>
          <li><a href="#">About Us</a></li>
          <li><a href="#">Contact</a></li>
        </ul>
      </div>

      {/* Right */}
      <div className="footer-section">
        <h3>Subscribe to our Newsletter</h3>
        <form className="newsletter-form">
          <input type="email" placeholder="Your email" required />
          <button type="submit">Subscribe</button>
        </form>
      </div>

      {/* Bottom bar */}
      <div className="footer-bottom">
        <p>&copy; {new Date().getFullYear()} Travel Guider. All rights reserved.</p>
      </div>
    </footer>
  );
}

export default Footer;
