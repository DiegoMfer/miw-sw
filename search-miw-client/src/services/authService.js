// Auth service for handling login/logout and token management

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Login function - authenticates user and stores token
const login = async (email, password) => {
  try {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({ email, password }),
      // Change from 'include' to 'same-origin' to avoid sending cookies
      // We're using token-based auth so we don't need to send cookies
      credentials: 'same-origin'
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    const data = await response.json();
    
    // Store token in localStorage
    localStorage.setItem('token', data.token);
    return data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};

// Register function - creates new user account
const register = async (email, password, name) => {
  try {
    const response = await fetch(`${API_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({ email, password, name }),
      // Change from 'include' to 'same-origin' to avoid sending cookies
      credentials: 'same-origin'
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Registration failed');
    }

    const data = await response.json();
    
    // Store token in localStorage after successful registration
    localStorage.setItem('token', data.token);
    return data;
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
};

// Logout function - removes authentication token
const logout = () => {
  localStorage.removeItem('token');
};

// Get token from localStorage
const getToken = () => {
  return localStorage.getItem('token');
};

// Check if user is authenticated
const isAuthenticated = () => {
  const token = localStorage.getItem('token');
  return !!token;
};

// Export all functions as a single object
export { 
  login, 
  register, 
  logout, 
  getToken, 
  isAuthenticated 
};
