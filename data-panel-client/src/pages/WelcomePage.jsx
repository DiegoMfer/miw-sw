import React from 'react';
import { Container, Typography, Box } from '@mui/material';

function WelcomePage() {
  return (
    <Container maxWidth="md">
      <Box
        sx={{
          mt: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Typography component="h1" variant="h3" gutterBottom>
          Welcome to the Data Panel
        </Typography>
        <Typography variant="h6" color="text.secondary">
          Navigate to the Stats page to view application statistics.
        </Typography>
      </Box>
    </Container>
  );
}

export default WelcomePage;
