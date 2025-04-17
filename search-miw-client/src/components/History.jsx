import {
  Container,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  ListItemSecondaryAction,
  Paper,
  Box,
  IconButton,
  Button,
  styled
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import DeleteIcon from '@mui/icons-material/Delete';
import { format } from 'date-fns';
import { useNavigate } from 'react-router-dom';
import { deleteSearch, clearHistory } from '../services/graphqlService';

function History({ searchHistory, onHistoryUpdate }) {
  const navigate = useNavigate();

  const handleClick = (query) => {
    navigate('/?q=' + encodeURIComponent(query));
  };

  const handleDeleteItem = async (id) => {
    try {
      await deleteSearch(id);
      // Notify parent to update history
      if (onHistoryUpdate) onHistoryUpdate();
    } catch (error) {
      console.error('Error deleting search item:', error);
    }
  };

  const handleClearHistory = async () => {
    try {
      await clearHistory();
      // Notify parent to update history
      if (onHistoryUpdate) onHistoryUpdate();
    } catch (error) {
      console.error('Error clearing history:', error);
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4, mb: 4 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h4" component="h1">
            Search History
          </Typography>
          {searchHistory.length > 0 && (
            <Button 
              variant="outlined" 
              color="error" 
              onClick={handleClearHistory}
              startIcon={<DeleteIcon />}
            >
              Clear All
            </Button>
          )}
        </Box>
        
        {searchHistory.length === 0 ? (
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
                  button 
                  key={item.id || index}
                  onClick={() => handleClick(item.query)}
                  divider={index < searchHistory.length - 1}
                >
                  <ListItemIcon>
                    <SearchIcon color="action" />
                  </ListItemIcon>
                  <ListItemText 
                    primary={item.query}
                    secondary={format(new Date(item.timestamp), 'MMM dd, yyyy HH:mm')}
                  />
                  <ListItemSecondaryAction>
                    <IconButton 
                      edge="end" 
                      aria-label="delete"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDeleteItem(item.id);
                      }}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </ListItemSecondaryAction>
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
