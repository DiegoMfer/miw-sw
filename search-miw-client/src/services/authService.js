import axios from 'axios';

// Use environment variable with fallback and support for runtime replacement
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const AUTH_ENDPOINT = `${API_URL}/api/auth`;

// Store JWT token and user info in localStorage
const storeUserData = (data) => {
  localStorage.setItem('token', data.token);
  localStorage.setItem('userId', data.userId);
  localStorage.setItem('name', data.name);
  localStorage.setItem('email', data.email);
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

// Register a new user
export const register = async (email, password, name) => {
  try {
    console.log(`Registering at: ${AUTH_ENDPOINT}/register`);
    const response = await axios.post(`${AUTH_ENDPOINT}/register`, { name, email, password });
    storeUserData(response.data);
    return response.data;
  } catch (error) {
    console.error('Registration error:', error.response || error);
    if (error.response?.status === 409) {
      throw new Error('Email already in use');
    }
    throw new Error(error.response?.data?.message || 'Registration failed');
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

// Create axios instance with auth header
export const authAxios = axios.create();

// Add auth header to requests
authAxios.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
