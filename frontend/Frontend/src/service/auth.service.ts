import axios from 'axios';

const API_URL = 'http://your-backend-api/auth/';

class AuthService {
  login(username: string, password: string) {
    return axios
      .post(API_URL + 'login', {
        username,
        password
      })
      .then(response => {
        if (response.data.token) {
          localStorage.setItem('token', response.data.token);
          this.setUserFromToken(response.data.token);
        }
        return response.data;
      });
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  register(username: string, email: string, password: string) {
    return axios.post(API_URL + 'register', {
      username,
      email,
      password
    });
  }

  resetPassword(username: string, oldPassword: string, newPassword: string) {
    const token = this.getCurrentToken();
    return axios.post(
      API_URL + 'reset-password',
      {
        username,
        password: oldPassword,
        newPassword
      },
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) return JSON.parse(userStr);
    return null;
  }

  getCurrentToken() {
    return localStorage.getItem('token');
  }

  isLoggedIn() {
    const token = this.getCurrentToken();
    return !!token;
  }

  setUserFromToken(token: string) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      
      const decodedToken = JSON.parse(jsonPayload);
      localStorage.setItem('user', JSON.stringify({
        username: decodedToken.sub,
        roles: decodedToken.roles || []
      }));
    } catch (error) {
      console.error('Error parsing token', error);
    }
  }
}

export default new AuthService();
