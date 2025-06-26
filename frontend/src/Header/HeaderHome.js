import "./HeaderHome.css";

function HeaderHome({ onNavigate }) {
  const handleUserClick = (e) => {
    e.preventDefault();
    onNavigate('signup');
  };

  const handleHomeClick = (e) => {
    e.preventDefault();
    onNavigate(null); // Close any open modal
  };

  return (
    <header className="headerHome">
      <div className="left">
        <img src="/logo.png" alt="Logo" className="logo" />
        <span className="site-name">Trevora</span>
      </div>

      <nav className="center">
        <a href="#" onClick={handleHomeClick}>Home</a>
        <a href="#">About</a>
      </nav>

      <div className="right">
        <a href="#" onClick={handleUserClick}>
          <img src="/icons/user.svg" alt="User Icon" className="user-icon" />
        </a>
      </div>
    </header>
  );
}

export default HeaderHome;
