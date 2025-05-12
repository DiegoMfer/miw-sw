import { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Login from './components/Login'; // Import the Login component
import { isAuthenticated, logout } from './services/authService'; // Import auth functions
import StatsPage from './pages/StatsPage'; 
import Navbar from './components/Navbar'; 
import WelcomePage from './pages/WelcomePage'; 

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
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
      <Navbar handleLogout={handleLogout} /> {/* Pass handleLogout to Navbar */}
      
      <Routes>
        <Route path="/" element={<WelcomePage />} /> 
        <Route path="/stats" element={<StatsPage />} />
        {/* Add a catch-all or redirect for logged-in users if they hit an unknown path */}
        <Route path="*" element={<Navigate to="/" />} /> 
      </Routes>
    </>
  );
}

export default App;
