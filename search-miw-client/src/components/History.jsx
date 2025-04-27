import {
  Container,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Paper,
  Box,
  styled
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { format } from 'date-fns';
import { useNavigate } from 'react-router-dom';

function History({ searchHistory }) {
  const navigate = useNavigate();

  const handleClick = (query) => {
    // Navigate to home and set the query (in a real app, you might need to lift state or use context)
    navigate('/?q=' + encodeURIComponent(query));
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Search History
        </Typography>
        
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
                  key={index}
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
