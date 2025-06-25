import './HeaderHome.css';

function HeaderHome() {
  return (
    <header className="headerHome">
      <div className="left">
        <img src="logo.png" alt="Logo" className="logo" />
        <span className="site-name">Travel Guider</span>
      </div>
      <nav className="right">
        <a href="">Home</a>
        <a href="">About</a>
        <a href="">Register</a>
        <a href="">Login</a>
      </nav>
    </header>
  );
}

export default HeaderHome;
