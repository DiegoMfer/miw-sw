import { getToken } from './authService';

const API_URL = import.meta.env.VITE_API_URL;

// GraphQL endpoint
const graphqlEndpoint = `${API_URL}/graphql`;

// Helper function to make GraphQL requests
async function executeGraphQL(query, variables = {}) {
  const token = getToken();
  
  const response = await fetch(graphqlEndpoint, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    body: JSON.stringify({
      query,
      variables
    })
  });

  if (!response.ok) {
    throw new Error('GraphQL request failed');
  }

  const data = await response.json();
  
  if (data.errors) {
    throw new Error(data.errors[0].message);
  }
  
  return data.data;
}

// Save a search to history
export const saveSearch = async (query) => {
  const mutation = `
    mutation SaveSearch($query: String!) {
      saveSearch(query: $query) {
        id
        query
        timestamp
      }
    }
  `;
  
  return executeGraphQL(mutation, { query });
};

// Get user's search history
export const getSearchHistory = async (limit = 10) => {
  const query = `
    query GetSearchHistory($limit: Int) {
      searchHistory(limit: $limit) {
        id
        query
        timestamp
      }
    }
  `;
  
  const result = await executeGraphQL(query, { limit });
  return result.searchHistory;
};

// Delete a specific search entry
export const deleteSearch = async (id) => {
  const mutation = `
    mutation DeleteSearch($id: String!) {
      deleteSearch(id: $id)
    }
  `;
  
  const result = await executeGraphQL(mutation, { id });
  return result.deleteSearch;
};

// Clear all search history
export const clearHistory = async () => {
  const mutation = `
    mutation ClearHistory {
      clearHistory
    }
  `;
  
  const result = await executeGraphQL(mutation);
  return result.clearHistory;
};

// Get search results from Wikidata
export const getSearchResults = async (query, language = 'en') => {
  const graphqlQuery = `
    query Search($query: String!, $language: String) {
      search(query: $query, language: $language) {
        query
        results {
          id
          title
          description
          url
        }
        totalResults
        searchTime
      }
    }
  `;
  
  const result = await executeGraphQL(graphqlQuery, { query, language });
  return result.search;
};
