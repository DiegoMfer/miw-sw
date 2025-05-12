import axios from 'axios';

// Use environment variable with fallback for API URL
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const AUTH_ENDPOINT = `${API_URL}/api/auth`;

// Store JWT token and user info in localStorage
const storeUserData = (data) => {
  localStorage.setItem('token', data.token);
  // Optionally store other user details if needed
  if (data.userId) localStorage.setItem('userId', data.userId);
  if (data.name) localStorage.setItem('name', data.name);
  if (data.email) localStorage.setItem('email', data.email);
};

// Get JWT token from localStorage
export const getToken = () => {
  return localStorage.getItem('token');
};

// Login a user
export const login = async (email, password) => {
  try {
    console.log(`Logging in to: ${AUTH_ENDPOINT}/login`);
    const response = await axios.post(`${AUTH_ENDPOINT}/login`, { email, password });
    storeUserData(response.data);
    return response.data;
  } catch (error) {
    console.error('Login error:', error.response || error);
    throw new Error(error.response?.data?.message || 'Invalid credentials');
  }
};

// Logout user
export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userId');
  localStorage.removeItem('name');
  localStorage.removeItem('email');
};

// Check if user is authenticated
export const isAuthenticated = () => {
  return !!getToken();
};
