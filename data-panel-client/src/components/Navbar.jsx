import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

function Navbar({ handleLogout }) { // Accept handleLogout as a prop
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component={RouterLink} to="/" sx={{ flexGrow: 1, color: 'inherit', textDecoration: 'none' }}>
          Data Panel
        </Typography>
        <Button color="inherit" component={RouterLink} to="/stats">
          Stats
        </Button>
        {handleLogout && ( // Conditionally render logout button if handleLogout is provided
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        )}
        {/* Add more navigation links here as needed */}
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;
