import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import Footer from "./Footer";
import "./Home.css";

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [activeQuestion, setActiveQuestion] = useState<number | null>(null);

  const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const toggleQuestion = (index: number) => {
    setActiveQuestion(activeQuestion === index ? null : index);
  };

  const faqData = [
    {
      question: "How do I join my local community on NeighBuzz?",
      answer: "Simply sign up with your email address and verify your location. We'll automatically connect you with your local community based on your address. You can also search for specific neighborhoods or communities to join."
    },
    {
      question: "Is NeighBuzz free to use?",
      answer: "Yes! NeighBuzz is completely free for all community members. Our goal is to strengthen neighborhoods, not profit from them. All features including news, forums, events, and surveys are available at no cost."
    },
    {
      question: "How do I report inappropriate content?",
      answer: "Each post and comment has a report button (flag icon). Click it to flag inappropriate content, and our moderation team will review it within 24 hours. We take community safety seriously and respond quickly to reports."
    },
    {
      question: "Can I organize events through NeighBuzz?",
      answer: "Absolutely! Use our Events section to create, promote, and manage community events. You can set dates, locations, descriptions, and even track RSVPs. It's a great way to bring neighbors together for various activities."
    },
    {
      question: "How do I change my notification preferences?",
      answer: "Go to your Profile settings and click on 'Notifications.' You can customize what types of updates you receive (news, events, forum replies, surveys) and choose between email, push, or in-app notifications."
    },
    {
      question: "What types of surveys can I participate in?",
      answer: "Our surveys cover various community topics like local development, safety concerns, event preferences, and neighborhood improvements. Community leaders and verified organizations can create surveys to gather resident feedback."
    },
    {
      question: "How do I verify my account?",
      answer: "Account verification involves confirming your email address and optionally your physical address. Verified accounts have access to additional features and are marked with a verification badge to build trust in the community."
    }
  ];

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-background">
          <div className="floating-elements">
            <div className="floating-element"></div>
            <div className="floating-element"></div>
            <div className="floating-element"></div>
            <div className="floating-element"></div>
          </div>
        </div>
        <div className="hero-overlay">
          <div className="hero-content">
            <div className="hero-badge">
              <i className="fas fa-star"></i>
              <span>Welcome to the Future of Community</span>
            </div>
            <h1 className="hero-title">
              Connect with Your
              <span className="gradient-text"> Neighborhood</span>
            </h1>
            <p className="hero-subtitle">
              Discover local news, join vibrant discussions, attend exciting events,
              and make your voice heard through community surveys. Building stronger
              neighborhoods, one connection at a time.
            </p>

            {!isAuthenticated ? (
              <div className="hero-buttons">
                <button
                  className="btn btn-primary"
                  onClick={() => navigate("/login")}
                >
                  <i className="fas fa-sign-in-alt"></i>
                  <span>Get Started</span>
                </button>
                <button
                  className="btn btn-secondary"
                  onClick={() => navigate("/register")}
                >
                  <i className="fas fa-user-plus"></i>
                  <span>Join Community</span>
                </button>
              </div>
            ) : (
              <div className="hero-buttons">
                <button
                  className="btn btn-primary"
                  onClick={() => navigate("/news")}
                >
                  <i className="fas fa-newspaper"></i>
                  <span>Explore News</span>
                </button>
                <button
                  className="btn btn-secondary"
                  onClick={() => navigate("/forums")}
                >
                  <i className="fas fa-comments"></i>
                  <span>Join Forums</span>
                </button>
              </div>
            )}

            <div className="hero-stats">
              <div className="stat-item">
                <div className="stat-number">10K+</div>
                <div className="stat-label">Active Members</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">500+</div>
                <div className="stat-label">Communities</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">1K+</div>
                <div className="stat-label">Events Monthly</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Rest of the sections remain the same... */}
      {/* About Section */}
      <section id="about" className="about-section">
        <div className="section-container">
          <div className="section-header">
            <h2 className="section-title">About NeighBuzz</h2>
            <p className="section-subtitle">
              We believe that strong neighborhoods are built on connection, communication,
              and collaboration. Our platform brings together everything you need to stay
              engaged with your local community.
            </p>
          </div>

          <div className="about-content">
            <div className="about-text">
              <div className="text-block">
                <h3>Our Mission</h3>
                <p>
                  NeighBuzz is your digital gateway to community engagement. We're dedicated
                  to fostering meaningful connections between neighbors and creating vibrant,
                  well-informed communities where everyone has a voice.
                </p>
              </div>
              <div className="text-block">
                <h3>Why Choose NeighBuzz?</h3>
                <p>
                  Our platform combines the essential tools you need to stay informed,
                  engaged, and connected with what matters most in your local area. From
                  breaking news to community events, we've got you covered.
                </p>
              </div>
            </div>

            <div className="features-grid">
              <div className="feature-card" onClick={() => navigate("/news")}>
                <div className="feature-icon">
                  <i className="fas fa-newspaper"></i>
                </div>
                <h3>Local News</h3>
                <p>Stay updated with real-time local news, announcements, and important community updates</p>
                <div className="feature-link">
                  <span>Learn More</span>
                  <i className="fas fa-arrow-right"></i>
                </div>
              </div>

              <div className="feature-card" onClick={() => navigate("/events")}>
                <div className="feature-icon">
                  <i className="fas fa-calendar-alt"></i>
                </div>
                <h3>Community Events</h3>
                <p>Discover, create, and participate in local events, meetups, and community gatherings</p>
                <div className="feature-link">
                  <span>Learn More</span>
                  <i className="fas fa-arrow-right"></i>
                </div>
              </div>

              <div className="feature-card" onClick={() => navigate("/forums")}>
                <div className="feature-icon">
                  <i className="fas fa-comments"></i>
                </div>
                <h3>Discussion Forums</h3>
                <p>Engage in meaningful conversations, share ideas, and connect with your neighbors</p>
                <div className="feature-link">
                  <span>Learn More</span>
                  <i className="fas fa-arrow-right"></i>
                </div>
              </div>

              <div className="feature-card" onClick={() => navigate("/survey")}>
                <div className="feature-icon">
                  <i className="fas fa-poll"></i>
                </div>
                <h3>Community Surveys</h3>
                <p>Share your opinions on local issues and help shape your community's future decisions</p>
                <div className="feature-link">
                  <span>Learn More</span>
                  <i className="fas fa-arrow-right"></i>
                </div>
              </div>
            </div>

          </div>
        </div>
      </section>

      {/* Contact Section */}
      <section id="contact" className="contact-section">
        <div className="section-container">
          <div className="section-header">
            <h2 className="section-title">Contact Us</h2>
            <p className="section-subtitle">
              Have questions, suggestions, or need support? Our team is here to help
              you make the most of your NeighBuzz experience.
            </p>
          </div>

          <div className="contact-content">
            <div className="contact-cards">
              <div className="contact-card">
                <div className="contact-icon">
                  <i className="fas fa-envelope"></i>
                </div>
                <h3>Email Support</h3>
                <p>Get help with your account, technical issues, or general questions</p>
                <a href="mailto:hello@neighbuzz.com" className="contact-link">
                  hello@neighbuzz.com
                </a>
              </div>

              <div className="contact-card">
                <div className="contact-icon">
                  <i className="fas fa-phone"></i>
                </div>
                <h3>Phone Support</h3>
                <p>Speak directly with our support team during business hours</p>
                <a href="tel:+15551234567" className="contact-link">
                  +1 (555) 123-4567
                </a>
              </div>

              <div className="contact-card">
                <div className="contact-icon">
                  <i className="fas fa-map-marker-alt"></i>
                </div>
                <h3>Visit Us</h3>
                <p>Stop by our office for in-person assistance or community events</p>
                <div className="contact-link">
                  123 Community Street<br />
                  Neighborhood City, MYSORE 12345
                </div>
              </div>
            </div>

            {/* <div className="contact-form-container">
              <div className="form-header">
                <h3>Send us a Message</h3>
                <p>Fill out the form below and we'll get back to you within 24 hours</p>
              </div>

              <form className="contact-form">
                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="firstName">First Name</label>
                    <input type="text" id="firstName" name="firstName" required />
                  </div>
                  <div className="form-group">
                    <label htmlFor="lastName">Last Name</label>
                    <input type="text" id="lastName" name="lastName" required />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="email">Email Address</label>
                    <input type="email" id="email" name="email" required />
                  </div>
                  <div className="form-group">
                    <label htmlFor="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="subject">Subject</label>
                  <select id="subject" name="subject" required>
                    <option value="">Select a topic</option>
                    <option value="general">General Inquiry</option>
                    <option value="technical">Technical Support</option>
                    <option value="community">Community Management</option>
                    <option value="feedback">Feedback & Suggestions</option>
                    <option value="partnership">Partnership Opportunities</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="message">Message</label>
                  <textarea id="message" name="message" rows={6} required placeholder="Tell us how we can help you..."></textarea>
                </div>

                <button type="submit" className="btn btn-primary form-submit">
                  <i className="fas fa-paper-plane"></i>
                  <span>Send Message</span>
                </button>
              </form>
            </div> */}
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="faq-section">
        <div className="section-container">
          <div className="section-header">
            <h2 className="section-title">Frequently Asked Questions</h2>
            <p className="section-subtitle">
              Find answers to common questions about NeighBuzz. Can't find what you're
              looking for? Feel free to contact our support team.
            </p>
          </div>

          <div className="faq-content">
            <div className="faq-list">
              {faqData.map((faq, index) => (
                <div key={index} className={`faq-item ${activeQuestion === index ? 'active' : ''}`}>
                  <div
                    className="faq-question"
                    onClick={() => toggleQuestion(index)}
                  >
                    <h3>{faq.question}</h3>
                    <div className="faq-icon">
                      <i className={`fas ${activeQuestion === index ? 'fa-minus' : 'fa-plus'}`}></i>
                    </div>
                  </div>
                  <div className={`faq-answer ${activeQuestion === index ? 'show' : ''}`}>
                    <div className="faq-answer-content">
                      <p>{faq.answer}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="faq-cta">
              <div className="cta-content">
                <h3>Still have questions?</h3>
                <p>Our support team is here to help you get the most out of NeighBuzz</p>
                <button
                  className="btn btn-primary"
                  onClick={() => scrollToSection('contact')}
                >
                  <i className="fas fa-headset"></i>
                  <span>Contact Support</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer Component */}
      <Footer />
    </div>
  );
};

export default HomePage;
