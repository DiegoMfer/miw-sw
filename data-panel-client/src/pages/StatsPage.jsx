import React, { useEffect, useState } from 'react';
import { request, gql } from 'graphql-request';
import { LineChart, BarChart } from '@mui/x-charts';
import { Container, Grid, Paper, Typography, CircularProgress, Alert } from '@mui/material';
import { getToken } from '../services/authService'; // Import getToken

const API_URL = import.meta.env.VITE_AGGREGATOR_API_URL;

const GET_STATS_DATA = gql`
  query GetStatsData {
    historyEntries {
      id
      timestamp
    }
    users {
      id
      createdAt
    }
  }
`;

const processHistoryDataForChart = (historyEntries) => {
  if (!historyEntries || historyEntries.length === 0) {
    return { xAxis: [{ data: [], scaleType: 'band' }], series: [{ data: [] }] };
  }
  const countsByDate = historyEntries.reduce((acc, entry) => {
    const date = new Date(entry.timestamp).toISOString().split('T')[0];
    acc[date] = (acc[date] || 0) + 1;
    return acc;
  }, {});

  const sortedDates = Object.keys(countsByDate).sort((a, b) => new Date(a) - new Date(b));
  const seriesData = sortedDates.map(date => countsByDate[date]);

  return {
    xAxis: [{ data: sortedDates, scaleType: 'band', label: 'Date' }],
    series: [{ data: seriesData, label: 'Searches', type: 'line', curve: 'linear' }],
  };
};

const processUsersDataForChart = (users) => {
  if (!users || users.length === 0) {
    return { xAxis: [{ data: [], scaleType: 'band' }], series: [{ data: [] }] };
  }
  const countsByDate = users.reduce((acc, user) => {
    const date = new Date(user.createdAt).toISOString().split('T')[0];
    acc[date] = (acc[date] || 0) + 1;
    return acc;
  }, {});

  const sortedDates = Object.keys(countsByDate).sort((a, b) => new Date(a) - new Date(b));
  const seriesData = sortedDates.map(date => countsByDate[date]);

  return {
    xAxis: [{ data: sortedDates, scaleType: 'band', label: 'Date' }],
    series: [{ data: seriesData, label: 'Registrations', type: 'bar' }],
  };
};

function StatsPage() {
  const [historyChartData, setHistoryChartData] = useState({ xAxis: [], series: [] });
  const [usersChartData, setUsersChartData] = useState({ xAxis: [], series: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!API_URL) {
        setError('API URL is not configured. Please set VITE_AGGREGATOR_API_URL.');
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        const token = getToken(); // Get the token
        const headers = {};
        if (token) {
          headers['Authorization'] = `Bearer ${token}`; // Prepare headers
        }

        // Make sure the API_URL for GraphQL is correct, e.g., http://localhost:8080/api/graphql
        // The curl example uses /api/graphql, ensure API_URL points to this or adjust accordingly.
        // If API_URL is just the base (http://localhost:8080), append /api/graphql
        const graphqlEndpoint = API_URL.endsWith('/graphql') ? API_URL : `${API_URL}/api/graphql`;


        const data = await request(graphqlEndpoint, GET_STATS_DATA, undefined, headers); // Pass headers
        setHistoryChartData(processHistoryDataForChart(data.historyEntries));
        setUsersChartData(processUsersDataForChart(data.users));
        setError(null);
      } catch (err) {
        console.error("Failed to fetch stats data:", err);
        setError(err.message || 'Failed to fetch data. Check console for details.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Container>
        <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom component="h1">
        Application Statistics
      </Typography>
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 400 }}>
            <Typography variant="h6" gutterBottom component="h2">
              Searches Over Time
            </Typography>
            {historyChartData.series[0]?.data.length > 0 ? (
              <LineChart
                xAxis={historyChartData.xAxis}
                series={historyChartData.series}
                // width and height are managed by the container size or can be set explicitly
                // For responsiveness, ensure the Paper component has a defined height.
              />
            ) : (
              <Typography sx={{mt: 2}}>No search data available.</Typography>
            )}
          </Paper>
        </Grid>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 400 }}>
            <Typography variant="h6" gutterBottom component="h2">
              User Registrations Over Time
            </Typography>
            {usersChartData.series[0]?.data.length > 0 ? (
              <BarChart
                xAxis={usersChartData.xAxis}
                series={usersChartData.series}
              />
            ) : (
              <Typography sx={{mt: 2}}>No user registration data available.</Typography>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}

export default StatsPage;
