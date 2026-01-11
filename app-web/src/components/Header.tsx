import React from 'react';
import { Link } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';

const Header: React.FC = () => {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Saca la Bici
        </Typography>
        <Button color="inherit" component={Link} to="/">Home</Button>
        <Button color="inherit" component={Link} to="/activities">Activities</Button>
        <Button color="inherit" component={Link} to="/announcements">Announcements</Button>
        <Button color="inherit" component={Link} to="/stats">Stats</Button>
        <Button color="inherit" component={Link} to="/profile">Profile</Button>
        <Button color="inherit" component={Link} to="/map">Map</Button>
        <Button color="inherit" component={Link} to="/faq">FAQ</Button>
        <Button color="inherit" component={Link} to="/rides">Rides</Button>
        <Button color="inherit" component={Link} to="/login">Login</Button>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
