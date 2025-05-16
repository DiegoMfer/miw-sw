import {
  Container,
  Typography,
  Paper,
  Box,
  styled,
  CircularProgress,
  Alert,
  Grid,
  Divider,
  Avatar
} from '@mui/material';
import { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
      setLoading(true);
      setError(null);
      
      try {
        // Get the token from localStorage
        const token = localStorage.getItem('token');
        
        if (!token) {
          throw new Error('No authentication token found. Please log in again.');
        }
        
        // Use the gateway endpoint that will be redirected to the user service
        const response = await axios.get(`${API_URL}/api/profile`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        setProfile(response.data);
      } catch (err) {
        console.error('Error fetching profile:', err);
        setError(err.message || 'Failed to fetch profile');
      } finally {
        setLoading(false);
      }
    };
    
    fetchProfile();
  }, []);

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          My Profile
        </Typography>
        
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        ) : profile ? (
          <ProfilePaper>
            <Box sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Avatar 
                  sx={{ 
                    width: 80, 
                    height: 80,
                    bgcolor: '#4285F4',
                    fontSize: '2rem',
                    mr: 3
                  }}
                >
                  {profile.name ? profile.name.charAt(0) : 'U'}
                </Avatar>
                <Box>
                  <Typography variant="h5">{profile.name}</Typography>
                  <Typography variant="body1" color="textSecondary">
                    User ID: {profile.userId || profile.id}
                  </Typography>
                </Box>
              </Box>
              
              <Divider sx={{ my: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" fontWeight="bold">Email</Typography>
                  <Typography variant="body1">{profile.email}</Typography>
                </Grid>
                {profile.createdAt && (
                  <Grid item xs={12} md={6}>
                    <Typography variant="subtitle1" fontWeight="bold">Account Created</Typography>
                    <Typography variant="body1">
                      {new Date(profile.createdAt).toLocaleDateString()}
                    </Typography>
                  </Grid>
                )}
                {profile.role && (
                  <Grid item xs={12} md={6}>
                    <Typography variant="subtitle1" fontWeight="bold">Role</Typography>
                    <Typography variant="body1">{profile.role}</Typography>
                  </Grid>
                )}
              </Grid>
            </Box>
          </ProfilePaper>
        ) : (
          <Alert severity="info">No profile data available.</Alert>
        )}
      </Box>
    </Container>
  );
}

// Styled components
const ProfilePaper = styled(Paper)(({ theme }) => ({
  borderRadius: '8px',
  overflow: 'hidden'
}));

export default Profile;
