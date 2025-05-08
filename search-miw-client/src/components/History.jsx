import {
  Container,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Paper,
  Box,
  styled,
  CircularProgress,
  Alert
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { format } from 'date-fns';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import axios from 'axios';

function History() {
  const navigate = useNavigate();
  const [searchHistory, setSearchHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSearchHistory = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Get the token from localStorage or your auth context/store
        const token = localStorage.getItem('token');
        
        if (!token) {
          throw new Error('No authentication token found. Please log in again.');
        }
        
        // Use the gateway endpoint that automatically redirects based on JWT token
        const response = await axios.get('http://localhost:8080/api/history?page=0&size=10', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        setSearchHistory(response.data);
      } catch (err) {
        console.error('Error fetching search history:', err);
        setError(err.message || 'Failed to fetch search history');
      } finally {
        setLoading(false);
      }
    };
    
    fetchSearchHistory();
  }, []);

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Search History
        </Typography>
        
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        ) : searchHistory.length === 0 ? (
          <EmptyHistoryPaper>
            <Typography variant="body1" color="textSecondary" sx={{ py: 4 }}>
              You haven't made any searches yet.
            </Typography>
          </EmptyHistoryPaper>
        ) : (
          <HistoryPaper>
            <List>
              {searchHistory.map((item, index) => (
                <ListItem 
                  key={item.id || index}
                  divider={index < searchHistory.length - 1}
                >
                  <ListItemIcon>
                    <SearchIcon color="action" />
                  </ListItemIcon>
                  <ListItemText 
                    primary={item.query}
                    secondary={format(new Date(item.timestamp), 'MMM dd, yyyy HH:mm')}
                  />
                </ListItem>
              ))}
            </List>
          </HistoryPaper>
        )}
      </Box>
    </Container>
  );
}

// Styled components
const HistoryPaper = styled(Paper)(({ theme }) => ({
  borderRadius: '8px',
  overflow: 'hidden'
}));

const EmptyHistoryPaper = styled(Paper)(({ theme }) => ({
  borderRadius: '8px',
  textAlign: 'center',
  padding: theme.spacing(2)
}));

export default History;
