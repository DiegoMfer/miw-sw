import { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import reactLogo from './assets/react.svg';
import viteLogo from '/vite.svg';
import './App.css';
import Login from './components/Login'; // Import the Login component
import { isAuthenticated, getToken, logout } from './services/authService'; // Import auth functions

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());
  const [graphQLData, setGraphQLData] = useState(null);
  const [loading, setLoading] = useState(false); // Keep loading for GraphQL data
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchGraphQLData = async () => {
      if (!isLoggedIn) {
        setGraphQLData(null); // Clear data if not logged in
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const token = getToken(); // Get token from authService

        if (!token) {
          // This case should ideally be handled by redirecting to login or by isLoggedIn state
          setIsLoggedIn(false); 
          throw new Error('Authentication token not found. Please log in.');
        }

        const response = await fetch('http://localhost:8080/api/graphql', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`, // Add Authorization header
          },
          body: JSON.stringify({
            query: `
              query IntrospectionQuery {
                __schema {
                  queryType {
                    name
                  }
                }
              }
            `,
          }),
        });

        if (!response.ok) {
          const errorData = await response.text();
          // If 401 or 403, token might be invalid/expired
          if (response.status === 401 || response.status === 403) {
            logout(); // Clear invalid token
            setIsLoggedIn(false);
            throw new Error(`Authentication error: ${response.status} ${response.statusText} - ${errorData}. Please log in again.`);
          }
          throw new Error(`Network response was not ok: ${response.status} ${response.statusText} - ${errorData}`);
        }

        const result = await response.json();
        setGraphQLData(result);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchGraphQLData();
  }, [isLoggedIn]); // Re-fetch when isLoggedIn changes

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
    setGraphQLData(null); // Clear data on logout
  };

  if (!isLoggedIn) {
    return (
      <Routes>
        <Route path="/login" element={<Login setIsLoggedIn={setIsLoggedIn} />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    );
  }

  return (
    <>
      <div>
        <a href="https://vite.dev" target="_blank" rel="noopener noreferrer">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank" rel="noopener noreferrer">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React Data Panel</h1>
      <button onClick={handleLogout} style={{ marginBottom: '20px' }}>Logout</button>
      
      <div className="graphql-data">
        <h2>GraphQL Data:</h2>
        {loading && <p>Loading...</p>}
        {error && <p style={{ color: 'red' }}>Error: {error}</p>}
        {graphQLData && (
          <pre>{JSON.stringify(graphQLData, null, 2)}</pre>
        )}
        {!graphQLData && !loading && !error && <p>No GraphQL data to display. Ensure you are logged in and the endpoint is working.</p>}
      </div>
    </>
  );
}

export default App;
