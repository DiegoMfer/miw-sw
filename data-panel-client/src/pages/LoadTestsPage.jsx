import React from 'react';
import { Container, Typography } from '@mui/material';

function LoadTestsPage() {
  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom component="h1">
        Load Tests
      </Typography>
      <Typography variant="body1">
        This page will display load testing information and results.
      </Typography>
      {/* Content for load tests will go here */}
    </Container>
  );
}

export default LoadTestsPage;
