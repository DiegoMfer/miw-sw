import { getToken } from './authService';

const API_URL = import.meta.env.VITE_API_URL;

// Get authenticated headers
const getAuthHeaders = () => {
  const token = getToken();
  if (!token) {
    throw new Error('Authentication required');
  }
  
  return {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Authorization': `Bearer ${token}`
  };
};

// Get user's search history with pagination
export const getUserSearchHistory = async (page = 0, size = 10) => {
  try {
    const response = await fetch(`${API_URL}/api/history/user/me?page=${page}&size=${size}`, {
      method: 'GET',
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error('Failed to fetch search history');
    }

    return await response.json();
  } catch (error) {
    console.error('History fetch error:', error);
    throw error;
  }
};

// Create a new history entry
export const createSearchHistory = async (query) => {
  try {
    const response = await fetch(`${API_URL}/api/history/user/me`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ query })
    });

    if (!response.ok) {
      throw new Error('Failed to save search to history');
    }

    return await response.json();
  } catch (error) {
    console.error('History save error:', error);
    throw error;
  }
};

// Delete a specific history entry
export const deleteHistoryEntry = async (id) => {
  try {
    const response = await fetch(`${API_URL}/api/history/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error('Failed to delete history entry');
    }

    return true;
  } catch (error) {
    console.error('History delete error:', error);
    throw error;
  }
};

// Clear all search history for current user
export const clearUserHistory = async () => {
  try {
    const response = await fetch(`${API_URL}/api/history/user/me`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      throw new Error('Failed to clear history');
    }

    return true;
  } catch (error) {
    console.error('Clear history error:', error);
    throw error;
  }
};
