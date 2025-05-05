import { useState, useEffect } from 'react';
import {
  TextField,
  Button,
  Container,
  Box,
  Typography,
  InputAdornment,
  Paper,
  styled 
} from '@mui/material';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import SearchIcon from '@mui/icons-material/Search';
import './App.css';
import Login from './components/Login';
import Register from './components/Register';
import Navbar from './components/Navbar';
import History from './components/History';
import { isAuthenticated, logout, authAxios } from './services/authService';

// Protected Route component to handle authentication
const ProtectedRoute = ({ children }) => {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());
  const [query, setQuery] = useState('');
  const [searchHistory, setSearchHistory] = useState([]);
  
  useEffect(() => {
    // Load search history when user logs in
    if (isLoggedIn) {
      fetchSearchHistory();
    } else {
      setSearchHistory([]);
    }
  }, [isLoggedIn]);
  
  const fetchSearchHistory = async () => {
    try {
      const userId = localStorage.getItem('userId');
      const response = await authAxios.get(`http://localhost:8080/api/history/user/${userId}`);
      setSearchHistory(response.data || []);
    } catch (error) {
      console.error('Error fetching search history:', error);
    }
  };

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
  };
  
  const handleSearch = (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    
    // If logged in, save search to history
    if (isLoggedIn) {
      const userId = localStorage.getItem('userId');
      authAxios.post(`http://localhost:8080/api/history/user/${userId}?query=${encodeURIComponent(query)}`)
        .then(() => fetchSearchHistory())
        .catch(error => console.error('Error saving search:', error));
    }
    
    // Implement search functionality here
    console.log('Searching for:', query);
  };
  
  const handleLucky = () => {
    // Implement "I'm feeling lucky" functionality here
    console.log('I\'m feeling lucky!');
  };

  return (
    <Router>
      <div className="app-container">
        <Navbar isLoggedIn={isLoggedIn} handleLogout={handleLogout} />

        <Routes>
          <Route path="/" element={
            <ProtectedRoute>
              <SearchPage 
                query={query} 
                setQuery={setQuery} 
                handleSearch={handleSearch} 
                handleLucky={handleLucky} 
              />
            </ProtectedRoute>
          } />
          <Route path="/login" element={
            isLoggedIn ? 
            <Navigate to="/" /> : 
            <Login setIsLoggedIn={setIsLoggedIn} />
          } />
          <Route path="/register" element={
            isLoggedIn ? 
            <Navigate to="/" /> : 
            <Register setIsLoggedIn={setIsLoggedIn} />
          } />
          <Route path="/profile" element={
            <ProtectedRoute>
              <div>Profile Page (Coming soon)</div>
            </ProtectedRoute>
          } />
          <Route path="/history" element={
            <ProtectedRoute>
              <History searchHistory={searchHistory} />
            </ProtectedRoute>
          } />
          <Route path="*" element={
            <ProtectedRoute>
              <Navigate to="/" />
            </ProtectedRoute>
          } />
        </Routes>

        <FooterBox>
          <Typography variant="body2" color="textSecondary">
            SearchMIW © {new Date().getFullYear()}
          </Typography>
        </FooterBox>
      </div>
    </Router>
  );
}

function SearchPage({ query, setQuery, handleSearch, handleLucky }) {
  return (
    <Container maxWidth="md" sx={{ mt: 8, textAlign: 'center' }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h2" component="h1" fontWeight="bold">
          <span style={{ color: '#4285F4' }}>S</span>
          <span style={{ color: '#EA4335' }}>e</span>
          <span style={{ color: '#FBBC05' }}>a</span>
          <span style={{ color: '#4285F4' }}>r</span>
          <span style={{ color: '#34A853' }}>c</span>
          <span style={{ color: '#EA4335' }}>h</span>
          <span style={{ color: '#4285F4' }}>MIW</span>
        </Typography>
      </Box>

      <SearchPaper component="form" onSubmit={handleSearch}>
        <InputAdornment position="start" sx={{ pl: 2 }}>
          <SearchIcon color="action" />
        </InputAdornment>
        <TextField
          fullWidth
          variant="standard"
          placeholder="Search the web"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          InputProps={{ disableUnderline: true }}
          sx={{ ml: 1, flex: 1 }}
        />
      </SearchPaper>

      <Box sx={{ mt: 2 }}>
        <SearchButton 
          variant="contained" 
          type="submit" 
          onClick={handleSearch}
          sx={{ mr: 2 }}
        >
          Google Search
        </SearchButton>
        <SearchButton 
          variant="contained" 
          onClick={handleLucky}
        >
          I'm Feeling Lucky
        </SearchButton>
      </Box>
    </Container>
  );
}

// Styled components
const SearchPaper = styled(Paper)(({ theme }) => ({
  padding: '2px 4px',
  display: 'flex',
  alignItems: 'center',
  width: '100%',
  borderRadius: 100,
  marginBottom: theme.spacing(4),
  elevation: 3
}));

const SearchButton = styled(Button)(({ theme }) => ({
  textTransform: 'none',
  backgroundColor: '#f8f9fa',
  color: '#3c4043',
  boxShadow: 'none',
  '&:hover': {
    backgroundColor: '#f1f3f4',
    boxShadow: '0 1px 1px rgba(0,0,0,0.1)',
  }
}));

const FooterBox = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(8),
  position: 'fixed',
  bottom: 20,
  left: 0,
  right: 0
}));

export default App;
