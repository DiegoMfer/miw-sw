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
import { BrowserRouter as Router, Routes, Route, Navigate, useSearchParams } from 'react-router-dom';
import SearchIcon from '@mui/icons-material/Search';
import './App.css';
import Login from './components/Login';
import Register from './components/Register';
import Navbar from './components/Navbar';
import History from './components/History';
import SearchResults from './components/SearchResults';
import { isAuthenticated } from './services/authService';
import { saveSearch, getSearchHistory, getSearchResults } from './services/graphqlService';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());
  const [query, setQuery] = useState('');
  const [searchHistory, setSearchHistory] = useState([]);
  const [searchResults, setSearchResults] = useState(null);
  const [isSearching, setIsSearching] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  
  useEffect(() => {
    // Check authentication status on component mount
    setIsLoggedIn(isAuthenticated());
    
    // Fetch search history if user is logged in
    if (isAuthenticated()) {
      fetchSearchHistory();
    }
  }, []);

  // Fetch search history from backend
  const fetchSearchHistory = async () => {
    try {
      const history = await getSearchHistory();
      setSearchHistory(history);
    } catch (error) {
      console.error('Error fetching search history:', error);
    }
  };

  // Function to update history after operations
  const onHistoryUpdate = () => {
    fetchSearchHistory();
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (query.trim()) {
      setIsSearching(true);
      setHasSearched(true);
      
      try {
        // Get search results
        const results = await getSearchResults(query);
        setSearchResults(results);
        
        // Save search to backend if logged in
        if (isLoggedIn) {
          const savedSearch = await saveSearch(query);
          console.log('Search saved:', savedSearch);
          // Refresh search history after saving
          await fetchSearchHistory();
        }
      } catch (error) {
        console.error('Error performing search:', error);
        setSearchResults({ 
          query: query, 
          results: [], 
          totalResults: 0, 
          searchTime: 0 
        });
      } finally {
        setIsSearching(false);
      }
    }
  };

  const handleLucky = () => {
    // This would typically take you directly to a random or first result
    alert("I'm Feeling Lucky!");
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setSearchResults(null);
    setHasSearched(false);
  };

  return (
    <Router>
      <div className="app-container">
        <Navbar isLoggedIn={isLoggedIn} handleLogout={handleLogout} />

        <Routes>
          <Route path="/" element={
            <MainPage
              query={query}
              setQuery={setQuery}
              handleSearch={handleSearch}
              handleLucky={handleLucky}
              searchResults={searchResults}
              isSearching={isSearching}
              hasSearched={hasSearched}
            />
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
            isLoggedIn ? 
            <div>Profile Page (Coming soon)</div> : 
            <Navigate to="/login" />
          } />
          <Route path="/history" element={
            isLoggedIn ? 
            <History searchHistory={searchHistory} onHistoryUpdate={onHistoryUpdate} /> : 
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

function MainPage({ 
  query, 
  setQuery, 
  handleSearch, 
  handleLucky, 
  searchResults, 
  isSearching, 
  hasSearched 
}) {
  // Extract search param if coming from history
  const [searchParams] = useSearchParams();
  const searchQuery = searchParams.get('q');
  
  // Set query from URL parameter if available
  useEffect(() => {
    if (searchQuery) {
      setQuery(searchQuery);
      
      // Automatically search if coming from URL
      const searchForm = new Event('submit');
      handleSearch(searchForm);
    }
  }, [searchQuery, setQuery]);

  const handleSubmit = (e) => {
    e.preventDefault();
    handleSearch(e);
  };

  return (
    <Container maxWidth={hasSearched ? "md" : "sm"} sx={{ mt: hasSearched ? 3 : 8 }}>
      {!hasSearched && (
        <Box sx={{ mb: 4, textAlign: 'center' }}>
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
      )}

      <SearchPaper component="form" onSubmit={handleSubmit}>
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

      {!hasSearched && (
        <Box sx={{ mt: 2, textAlign: 'center' }}>
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
      )}

      {hasSearched && (
        <SearchResults 
          results={searchResults} 
          loading={isSearching}
          query={query}
        />
      )}
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
