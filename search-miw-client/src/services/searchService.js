import { authAxios } from './authService';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const searchService = {
  search: async (query, language = 'en') => {
    try {
      // Build the search URL
      let url = `${API_URL}/api/search?query=${encodeURIComponent(query)}`;
      
      // Add optional parameters if provided
      if (language) {
        url += `&language=${encodeURIComponent(language)}`;
      }
      
      // Get userId from local storage if user is logged in
      const userId = localStorage.getItem('userId');
      if (userId) {
        url += `&userId=${userId}`;
      }
      
      // Make the API call
      // We're using authAxios which will automatically include the Bearer token
      // if the user is authenticated
      const response = await authAxios.get(url);
      return response.data;
    } catch (error) {
      console.error('Error searching:', error);
      throw error;
    }
  }
};
