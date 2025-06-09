import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../AuthContext";
import { useNotification } from "./NotificationContext";
import Notifications from "./Notifications";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./Navbar.css";

const Navbar: React.FC = () => {
  const { isAuthenticated, logout, username } = useAuth();
  const { unreadCount } = useNotification();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const toggleProfileDropdown = () => {
    setIsDropdownOpen((prev) => !prev);
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen((prev) => !prev);
  };

  const closeMobileMenu = () => {
    setIsMobileMenuOpen(false);
  };

  const handleProfile = () => {
    navigate("/profile");
    setIsDropdownOpen(false);
    closeMobileMenu();
  };

  const handleLogout = () => {
    logout();
    navigate("/");
    setIsDropdownOpen(false);
    closeMobileMenu();
  };

  const handleMobileNavClick = () => {
    closeMobileMenu();
  };

  return (
    <>
      <div className="nav-container">
        <nav className="navbar">
          {/* Left - Project Name */}
          <div className="navbar-left">
            <Link to="/" className="home-link">
              <i className="bi bi-house-door-fill"></i> NeighBuzz
            </Link>
          </div>

          {/* Center - Search bar and links (Desktop only) */}
          <div className="navbar-center">
            <div className="nav-links">
              <Link
                to="/news"
                className={`nav-link ${location.pathname === '/news' ? 'active' : ''}`}
                style={{ fontSize: "21px", fontWeight: "bold" }}
              >
                News
              </Link>
              <Link
                to="/events"
                className={`nav-link ${location.pathname === '/events' ? 'active' : ''}`}
                style={{ fontSize: "21px", fontWeight: "bold" }}
              >
                Events
              </Link>
              <Link
                to="/forums"
                className={`nav-link ${location.pathname === '/forums' ? 'active' : ''}`}
                style={{ fontSize: "21px", fontWeight: "bold" }}
              >
                Forum
              </Link>
              <Link
                to="/survey"
                className={`nav-link ${location.pathname === '/survey' ? 'active' : ''}`}
                style={{ fontSize: "21px", fontWeight: "bold" }}
              >
                Survey
              </Link>
            </div>
          </div>

          {/* Right - Notifications, Profile, and Burger Menu */}
          <div className="navbar-right">
            {/* Notification Bell Button - Always visible */}
            <button
              onClick={() => setIsNotificationsOpen(true)}
              className="notification-bell"
              aria-label="Open notifications"
            >
              🔔
              {unreadCount > 0 && (
                <span className="notification-count">
                  {unreadCount > 99 ? '99+' : unreadCount}
                </span>
              )}
            </button>

            {/* Username (Desktop only) */}
            {isAuthenticated && (
              <span className="username" style={{ color: "white" }}>
                {username}
              </span>
            )}

            {/* Profile Icon (Desktop only) */}
            <div className="profile-icon" onClick={toggleProfileDropdown}>
              <i className="bi bi-person-circle"></i>
            </div>

            {/* Burger Menu (Mobile only) */}
            <div className="burger-menu" onClick={toggleMobileMenu}>
              <div className="burger-line"></div>
              <div className="burger-line"></div>
              <div className="burger-line"></div>
            </div>

            {/* Profile Dropdown (Desktop only) */}
            {isDropdownOpen && (
              <div className="profile-dropdown">
                {isAuthenticated ? (
                  <>
                    <div className="dropdown-item" onClick={handleProfile}>
                      {username || "My Profile"}
                    </div>
                    <div className="dropdown-item logout" onClick={handleLogout}>
                      Logout
                    </div>
                  </>
                ) : (
                  <Link to="/login" className="dropdown-item" onClick={() => setIsDropdownOpen(false)}>
                    Login / Register
                  </Link>
                )}
              </div>
            )}
          </div>

          {/* Notifications panel drawer */}
          <Notifications
            isOpen={isNotificationsOpen}
            onClose={() => setIsNotificationsOpen(false)}
          />
        </nav>

        {/* Mobile Menu Overlay */}
        <div
          className={`mobile-menu-overlay ${isMobileMenuOpen ? 'open' : ''}`}
          onClick={closeMobileMenu}
        ></div>

        {/* Mobile Menu */}
        <div className={`mobile-menu ${isMobileMenuOpen ? 'open' : ''}`}>
          <div className="mobile-menu-header">
            <span className="mobile-menu-title">Menu</span>
            <button className="close-menu" onClick={closeMobileMenu}>
              ×
            </button>
          </div>

          <div className="mobile-nav-links">
            <Link
              to="/news"
              className={`mobile-nav-link ${location.pathname === '/news' ? 'active' : ''}`}
              onClick={handleMobileNavClick}
            >
              News
            </Link>
            <Link
              to="/events"
              className={`mobile-nav-link ${location.pathname === '/events' ? 'active' : ''}`}
              onClick={handleMobileNavClick}
            >
              Events
            </Link>
            <Link
              to="/forums"
              className={`mobile-nav-link ${location.pathname === '/forums' ? 'active' : ''}`}
              onClick={handleMobileNavClick}
            >
              Forum
            </Link>
            <Link
              to="/survey"
              className={`mobile-nav-link ${location.pathname === '/survey' ? 'active' : ''}`}
              onClick={handleMobileNavClick}
            >
              Survey
            </Link>

            {isAuthenticated && (
              <>
                <Link
                  to="/profile"
                  className={`mobile-nav-link ${location.pathname === '/profile' ? 'active' : ''}`}
                  onClick={handleMobileNavClick}
                >
                  Profile ({username})
                </Link>
                <div className="mobile-nav-link logout" onClick={handleLogout}>
                  Logout
                </div>
              </>
            )}

            {!isAuthenticated && (
              <Link
                to="/login"
                className="mobile-nav-link"
                onClick={handleMobileNavClick}
              >
                Login / Register
              </Link>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default Navbar;