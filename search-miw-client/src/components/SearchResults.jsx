import React from 'react';
import { 
  Container, 
  Typography, 
  Box, 
  List, 
  ListItem, 
  ListItemText, 
  Paper,
  Divider,
  Link,
  CircularProgress
} from '@mui/material';

const SearchResults = ({ results, loading, error }) => {
  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, textAlign: 'center' }}>
        <CircularProgress />
        <Typography variant="body1" sx={{ mt: 2 }}>
          Searching...
        </Typography>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Typography variant="h6" color="error">
          Error: {error.message || "Failed to fetch search results"}
        </Typography>
      </Container>
    );
  }

  if (!results) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Typography variant="body1">
          Enter a search term to see results
        </Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Box sx={{ mb: 2 }}>
        <Typography variant="h6">
          Found {results.totalResults} results for "{results.query}" ({results.searchTime} ms)
        </Typography>
      </Box>

      {results.results && results.results.length > 0 ? (
        <Paper elevation={1}>
          <List>
            {results.results.map((item, index) => (
              <React.Fragment key={item.id}>
                {index > 0 && <Divider />}
                <ListItem>
                  <ListItemText
                    primary={
                      <Link href={item.url} target="_blank" rel="noopener" underline="hover">
                        {item.title}
                      </Link>
                    }
                    secondary={item.description || "No description available"}
                  />
                </ListItem>
              </React.Fragment>
            ))}
          </List>
        </Paper>
      ) : (
        <Typography variant="body1">
          No results found for "{results.query}". Try a different search term.
        </Typography>
      )}
    </Container>
  );
};

export default SearchResults;
