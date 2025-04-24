import { getToken } from './authService';

const API_URL = import.meta.env.VITE_API_URL;

// Search function that calls the REST API directly
export const searchWikidata = async (query, language = 'en') => {
  try {
    const token = getToken();
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    // Add authorization header if user is logged in
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_URL}/api/search?query=${encodeURIComponent(query)}&language=${language}`, {
      method: 'GET',
      headers
    });

    if (!response.ok) {
      throw new Error('Search failed');
    }

    return await response.json();
  } catch (error) {
    console.error('Search error:', error);
    throw error;
  }
};

// Use this when you want to get detailed info about a specific wikidata entity
export const getEntityDetails = async (entityId, language = 'en') => {
  try {
    const response = await fetch(`${API_URL}/api/search/entity/${entityId}?language=${language}`);
    
    if (!response.ok) {
      throw new Error('Failed to fetch entity details');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Entity details error:', error);
    throw error;
  }
};
