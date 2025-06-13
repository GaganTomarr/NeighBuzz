import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import "./AuthForm.css";
import {
  validateConfirmPassword,
  validateEmail,
  validatePassword,
  validateUsername,
} from "../validator/RegisterValidations";

const Register: React.FC = () => {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmedPassword, setShowConfirmedPassword] = useState(false);
  const navigate = useNavigate();

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmedPassword((prev) => !prev);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Run all validations
    const usernameError = validateUsername(username);
    if (usernameError) {
      toast.error(usernameError);
      return;
    }

    const emailError = validateEmail(email);
    if (emailError) {
      toast.error(emailError);
      return;
    }

    const passwordError = validatePassword(password);
    if (passwordError) {
      toast.error(passwordError);
      return;
    }

    const confirmPasswordError = validateConfirmPassword(password, confirmPassword);
    if (confirmPasswordError) {
      toast.error(confirmPasswordError);
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password }),
      });

      if (response.ok) {
        toast.success("Registration successful! Redirecting to login...");
        setTimeout(() => {
          navigate("/login", {
            state: { prefillUsername: username, prefillPassword: password },
          });
        }, 1500);
      } else {
        const errorText = await response.text();
        try {
          const errorData = JSON.parse(errorText);
          toast.error(errorData.message || "Registration failed");
        } catch {
          toast.error(errorText || "Registration failed");
        }
      }
    } catch (err) {
      console.error("Registration error:", err);
      toast.error("An error occurred during registration. Please try again.");
    }
  };

  return (
    <div className="auth-container">
      <Toaster position="top-center" reverseOrder={false} />
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-icon">
            <i className="bi bi-person-plus"></i>
          </div>
          <h2>Create Account</h2>
          <p className="auth-subtitle">Join our community today</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">
              <i className="bi bi-person"></i>
              Username
            </label>
            <input
              type="text"
              id="username"
              placeholder="Choose a username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              className="form-control"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">
              <i className="bi bi-envelope"></i>
              Email Address
            </label>
            <input
              type="email"
              id="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="form-control"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">
              <i className="bi bi-lock"></i>
              Password
            </label>
            <div className="password-input-container">
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                placeholder="Create a password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="form-control"
                autoComplete="new-password"
              />
              {password && (
                <span
                  className="password-toggle-icon"
                  onClick={togglePasswordVisibility}
                  role="button"
                  tabIndex={0}
                  aria-label="Toggle password visibility"
                >
                  {showPassword ? (
                    <i className="bi bi-eye-slash"></i>
                  ) : (
                    <i className="bi bi-eye"></i>
                  )}
                </span>
              )}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">
              <i className="bi bi-shield-check"></i>
              Confirm Password
            </label>
            <div className="password-input-container">
              <input
                type={showConfirmedPassword ? "text" : "password"}
                id="confirmPassword"
                placeholder="Confirm your password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                className="form-control"
                autoComplete="new-password"
              />
              {confirmPassword && (
                <span
                  className="password-toggle-icon"
                  onClick={toggleConfirmPasswordVisibility}
                  role="button"
                  tabIndex={0}
                  aria-label="Toggle confirm password visibility"
                >
                  {showConfirmedPassword ? (
                    <i className="bi bi-eye-slash"></i>
                  ) : (
                    <i className="bi bi-eye"></i>
                  )}
                </span>
              )}
            </div>
          </div>

          <button type="submit" className="submit-button">
            <i className="bi bi-person-check"></i>
            Create Account
          </button>

          <div className="auth-divider">
            <span>or</span>
          </div>

          <p className="switch-form-link">
            Already have an account? 
            <Link to="/login" className="auth-link">Sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Register;
