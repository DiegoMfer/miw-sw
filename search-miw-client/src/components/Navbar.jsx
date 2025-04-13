import {
  AppBar,
  Toolbar,
  Button,
  Box,
  Typography,
  styled
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import HistoryIcon from '@mui/icons-material/History';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LogoutIcon from '@mui/icons-material/Logout';
import HomeIcon from '@mui/icons-material/Home';

function Navbar({ isLoggedIn, handleLogout }) {
  return (
    <StyledAppBar position="static" elevation={0}>
      <Toolbar>
        <LogoLink to="/">
          <Typography variant="h6" fontWeight="bold">
            <span style={{ color: '#4285F4' }}>S</span>
            <span style={{ color: '#EA4335' }}>e</span>
            <span style={{ color: '#FBBC05' }}>a</span>
            <span style={{ color: '#4285F4' }}>r</span>
            <span style={{ color: '#34A853' }}>c</span>
            <span style={{ color: '#EA4335' }}>h</span>
            <span style={{ color: '#4285F4' }}>MIW</span>
          </Typography>
        </LogoLink>
        
        <Box sx={{ flexGrow: 1 }} />

        <NavButton component={RouterLink} to="/" startIcon={<HomeIcon />}>
          Home
        </NavButton>
        
        {isLoggedIn ? (
          <>
            <NavButton component={RouterLink} to="/history" startIcon={<HistoryIcon />}>
              History
            </NavButton>
            <NavButton component={RouterLink} to="/profile" startIcon={<AccountCircleIcon />}>
              Profile
            </NavButton>
            <NavButton onClick={handleLogout} startIcon={<LogoutIcon />}>
              Logout
            </NavButton>
          </>
        ) : (
          <NavButton component={RouterLink} to="/login">
            Login
          </NavButton>
        )}
      </Toolbar>
    </StyledAppBar>
  );
}

// Styled components
const StyledAppBar = styled(AppBar)(({ theme }) => ({
  backgroundColor: 'white',
  color: '#5f6368',
  borderBottom: '1px solid #dadce0'
}));

const NavButton = styled(Button)(({ theme }) => ({
  marginLeft: theme.spacing(1),
  color: '#5f6368',
  '&:hover': {
    backgroundColor: '#f1f3f4',
  }
}));

const LogoLink = styled(RouterLink)(({ theme }) => ({
  textDecoration: 'none',
  display: 'flex',
  alignItems: 'center'
}));

export default Navbar;
