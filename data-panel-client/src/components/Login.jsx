import { useState } from 'react';
import { 
  Container, 
  Box, 
  Typography, 
  TextField, 
  Button, 
  Paper,
  styled,
  Alert
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/authService';

function Login({ setIsLoggedIn }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (email && password) {
      try {
        setLoading(true);
        await login(email, password);
        setIsLoggedIn(true);
        navigate('/'); // Navigate to main page after login
      } catch (err) {
        setError(err.message || 'Login failed. Please try again.');
      } finally {
        setLoading(false);
      }
    } else {
      setError('Please enter both email and password.');
    }
  };

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Typography component="h1" variant="h5" sx={{ mb: 3 }}>
          Sign in to Data Panel
        </Typography>
        
        <LoginPaper>
          <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
            {error && (
              <Alert severity="error" sx={{ mb: 2, width: '100%' }}>
                {error}
              </Alert>
            )}
            
            <TextField
              margin="normal"
              required
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              autoFocus
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            
            <LoginButton
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </LoginButton>
          </Box>
        </LoginPaper>
      </Box>
    </Container>
  );
}

// Styled components (optional, or use inline sx props)
const LoginPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  borderRadius: '8px',
  width: '100%',
}));

const LoginButton = styled(Button)(({ theme }) => ({
  backgroundColor: '#1976d2', // Example color
  '&:hover': {
    backgroundColor: '#115293',
  },
}));

export default Login;
