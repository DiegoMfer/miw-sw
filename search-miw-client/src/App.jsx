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
import { isAuthenticated } from './services/authService';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());
  const [query, setQuery] = useState('');
  const [searchHistory, setSearchHistory] = useState([]);
  
  useEffect(() => {
    // Check authentication status on component mount
    setIsLoggedIn(isAuthenticated());
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) {
      // Save search to history if logged in
      if (isLoggedIn) {
        setSearchHistory([
          { query: query, timestamp: new Date().toISOString() },
          ...searchHistory
        ].slice(0, 10)); // Keep only the 10 most recent searches
      }
      
      // Here you would typically make an API call to get search results
      alert(`You searched for: ${query}`);
    }
  };

  const handleLucky = () => {
    // This would typically take you directly to a random or first result
    alert("I'm Feeling Lucky!");
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
  };

  return (
    <Router>
      <div className="app-container">
        <Navbar isLoggedIn={isLoggedIn} handleLogout={handleLogout} />

        <Routes>
          <Route path="/" element={<SearchPage 
            query={query} 
            setQuery={setQuery} 
            handleSearch={handleSearch} 
            handleLucky={handleLucky} 
          />} />
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
            isLoggedIn ? 
            <div>Profile Page (Coming soon)</div> : 
            <Navigate to="/login" />
          } />
          <Route path="/history" element={
            isLoggedIn ? 
            <History searchHistory={searchHistory} /> : 
            <Navigate to="/login" />
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
