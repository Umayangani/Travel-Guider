import "./HeaderHome.css";
import ProfileDropdown from "./ProfileDropdown";

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
    <header className="headerHome custom-header" style={{ background: '#032a06', color: '#fff', padding: '0.7rem 0', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
      <div className="left">
        <img src="/logo.png" alt="Logo" className="logo" />
        <span className="site-name" style={{ color: '#fff', fontWeight: 700, fontSize: '2rem', letterSpacing: '1px' }}>
          Trevora
        </span>
      </div>
      <nav className="center custom-nav">
        <a href="#" style={{ color: '#ffc107', fontWeight: 700 }} onClick={handleHomeClick}>Home</a>
        <a href="#">About</a>
      </nav>
      <div className="right custom-header-right">
        <a href="#" onClick={handleUserClick}>
          <img src="/icons/user.svg" alt="User Icon" className="user-icon" />
        </a>
        <ProfileDropdown />
      </div>
    </header>
  );
}

export default HeaderHome;
