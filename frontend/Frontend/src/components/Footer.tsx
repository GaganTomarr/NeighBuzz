import React from "react";
import { Link } from "react-router-dom";
import "./Footer.css";

const Footer: React.FC = () => {
  const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="footer">
      <div className="footer-background">
        <div className="footer-wave">
          <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
            <path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z"></path>
          </svg>
        </div>
        <div className="footer-particles">
          <div className="particle"></div>
          <div className="particle"></div>
          <div className="particle"></div>
          <div className="particle"></div>
          <div className="particle"></div>
          <div className="particle"></div>
        </div>
      </div>

      <div className="footer-container">
        {/* Main Footer Content */}
        <div className="footer-content">
          {/* Brand Section */}
          <div className="footer-section footer-brand">
            <div className="brand-logo">
              <div className="logo-icon">
                <i className="fas fa-home"></i>
              </div>
              <h3>NeighBuzz</h3>
            </div>
            <p className="brand-description">
              Connecting communities, one neighborhood at a time. Building stronger bonds 
              through shared experiences and local engagement.
            </p>
            <div className="social-links">
              <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" aria-label="Facebook" className="social-link facebook">
                <i className="fab fa-facebook-f"></i>
              </a>
              <a href="https://twitter.com" target="_blank" rel="noopener noreferrer" aria-label="Twitter" className="social-link twitter">
                <i className="fab fa-twitter"></i>
              </a>
              <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" aria-label="Instagram" className="social-link instagram">
                <i className="fab fa-instagram"></i>
              </a>
              <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer" aria-label="LinkedIn" className="social-link linkedin">
                <i className="fab fa-linkedin-in"></i>
              </a>
              <a href="https://youtube.com" target="_blank" rel="noopener noreferrer" aria-label="YouTube" className="social-link youtube">
                <i className="fab fa-youtube"></i>
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div className="footer-section">
            <h4 className="footer-title">
              <i className="fas fa-link"></i>
              Quick Links
            </h4>
            <ul className="footer-links">
              <li><Link to="/news"><i className="fas fa-newspaper"></i>Latest News</Link></li>
              <li><Link to="/events"><i className="fas fa-calendar-alt"></i>Community Events</Link></li>
              <li><Link to="/forums"><i className="fas fa-comments"></i>Discussion Forums</Link></li>
              <li><Link to="/survey"><i className="fas fa-poll"></i>Surveys & Polls</Link></li>
              <li><a href="#about" onClick={() => scrollToSection('about')}><i className="fas fa-info-circle"></i>About Us</a></li>
            </ul>
          </div>

          {/* Support */}
          <div className="footer-section">
            <h4 className="footer-title">
              <i className="fas fa-headset"></i>
              Support
            </h4>
            <ul className="footer-links">
              <li><a href="#faq" onClick={() => scrollToSection('faq')}><i className="fas fa-question-circle"></i>FAQ</a></li>
              <li><Link to="/help"><i className="fas fa-life-ring"></i>Help Center</Link></li>
              <li><Link to="/community-guidelines"><i className="fas fa-users"></i>Community Guidelines</Link></li>
              <li><Link to="/report"><i className="fas fa-flag"></i>Report Issue</Link></li>
              <li><a href="#contact" onClick={() => scrollToSection('contact')}><i className="fas fa-envelope"></i>Contact Support</a></li>
            </ul>
          </div>

          {/* Contact Information */}
          <div className="footer-section">
            <h4 className="footer-title">
              <i className="fas fa-map-marker-alt"></i>
              Contact Info
            </h4>
            <div className="contact-info">
              <div className="contact-item">
                <div className="contact-icon">
                  <i className="fas fa-envelope"></i>
                </div>
                <div className="contact-details">
                  <span>Email</span>
                  <a href="mailto:hello@neighbuzz.com">hello@neighbuzz.com</a>
                </div>
              </div>
              <div className="contact-item">
                <div className="contact-icon">
                  <i className="fas fa-phone"></i>
                </div>
                <div className="contact-details">
                  <span>Phone</span>
                  <a href="tel:+15551234567">+1 (555) 123-4567</a>
                </div>
              </div>
              <div className="contact-item">
                <div className="contact-icon">
                  <i className="fas fa-map-marker-alt"></i>
                </div>
                <div className="contact-details">
                  <span>Address</span>
                  <p>123 Community Street<br />Neighborhood City, MYSORE 12345</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Bottom */}
        <div className="footer-bottom">
          <div className="footer-bottom-content">
            <div className="copyright">
              <p>&copy; 2025 NeighBuzz. All rights reserved. Made with <i className="fas fa-heart"></i> for communities.</p>
            </div>
            <div className="footer-bottom-links">
              <button onClick={scrollToTop} className="back-to-top">
                <i className="fas fa-arrow-up"></i>
                <span>Back to Top</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
