import React, { useState, useEffect, MouseEvent } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { useAuth } from "../AuthContext";
import Swal from "sweetalert2";
import "./AuthForm.css";

interface LocationState {
  prefillUsername?: string;
  prefillPassword?: string;
}

const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: string })?.from || "/";

  useEffect(() => {
    const state = location.state as LocationState | null;

    if (state?.prefillUsername) setUsername(state.prefillUsername);
    if (state?.prefillPassword) setPassword(state.prefillPassword);

    // Clear location state after prefill
    navigate(location.pathname, { replace: true, state: null });
  }, [location.state, location.pathname, navigate]);

  const togglePasswordVisibility = (e: MouseEvent<HTMLSpanElement>): void => {
    e.preventDefault();
    setShowPassword(!showPassword);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const data = await response.json();

        const token = data.token;
        const userId = data.userId;
        const returnedUsername = data.username;
        const isAdmin = data.isAdmin;

        if (!token || !userId) {
          setError("Invalid login response: token or userId missing");
          return;
        }

        await Swal.fire({
          icon: "success",
          title: "Login Successful!",
          text: `Welcome back, ${returnedUsername}!`,
          timer: 2000,
          showConfirmButton: false,
        });

        // Pass isAdmin to login function
        login(token, returnedUsername, userId, isAdmin);

        navigate(from, { replace: true });
      } else {
        const errorMessage = await response.text();
        setError(`Login failed: ${response.status} - ${errorMessage}`);
      }
    } catch (err) {
      console.error("Login error:", err);
      setError("An error occurred during login. Please try again.");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-icon">
            <i className="bi bi-person-circle"></i>
          </div>
          <h2>Welcome Back</h2>
          <p className="auth-subtitle">Sign in to your account</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="username">
              <i className="bi bi-person"></i>
              Username
            </label>
            <input
              type="text"
              id="username"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
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
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="form-control"
              />
              <span
                className="password-toggle-icon"
                onClick={togglePasswordVisibility}
                role="button"
                aria-label="Toggle password visibility"
                tabIndex={0}
              >
                {showPassword ? (
                  <i className="bi bi-eye-slash"></i>
                ) : (
                  <i className="bi bi-eye"></i>
                )}
              </span>
            </div>
          </div>

          <button type="submit" className="submit-button">
            <i className="bi bi-box-arrow-in-right"></i>
            Sign In
          </button>

          <div className="auth-divider">
            <span>or</span>
          </div>

          <p className="switch-form-link">
            Don't have an account? 
            <Link to="/register" className="auth-link">Create one</Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Login;
