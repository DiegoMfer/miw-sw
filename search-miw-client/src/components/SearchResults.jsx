import React from 'react';
import { 
  Box, 
  Typography, 
  List, 
  ListItem,
  Divider,
  CircularProgress,
  Link
} from '@mui/material';
import { styled } from '@mui/material/styles';

function SearchResults({ results, loading, query }) {
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" my={4}>
        <CircularProgress />
      </Box>
    );
  }

  // Check if results is empty or has no results array
  if (!results || !results.results || results.results.length === 0) {
    return (
      <Box my={4}>
        <Typography variant="body1" color="textSecondary">
          No results found for "{query}"
        </Typography>
      </Box>
    );
  }

  return (
    <Box my={4}>
      <Box mb={2}>
        <Typography variant="body2" color="textSecondary">
          About {results.totalResults} results ({(results.searchTime / 1000).toFixed(2)} seconds)
        </Typography>
      </Box>
      
      <ResultList>
        {results.results.map((result, index) => (
          <React.Fragment key={result.id}>
            <ResultItem>
              <Link 
                href={result.url} 
                target="_blank" 
                rel="noopener noreferrer"
                underline="hover"
              >
                <ResultTitle variant="h6">{result.title}</ResultTitle>
              </Link>
              <ResultUrl variant="body2" color="success.main">
                {result.url}
              </ResultUrl>
              <ResultDescription variant="body2">
                {result.description || 'No description available'}
              </ResultDescription>
            </ResultItem>
            {index < results.results.length - 1 && <Divider />}
          </React.Fragment>
        ))}
      </ResultList>
    </Box>
  );
}

// Styled components
const ResultList = styled(List)(({ theme }) => ({
  padding: 0,
}));

const ResultItem = styled(ListItem)(({ theme }) => ({
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'flex-start',
  padding: theme.spacing(2, 0),
}));

const ResultTitle = styled(Typography)(({ theme }) => ({
  color: '#1a0dab',
  marginBottom: theme.spacing(0.5),
}));

const ResultUrl = styled(Typography)(({ theme }) => ({
  marginBottom: theme.spacing(0.5),
}));

const ResultDescription = styled(Typography)(({ theme }) => ({
  color: '#4d5156',
}));

export default SearchResults;
