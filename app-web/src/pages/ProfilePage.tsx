import React, { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext';
import { getProfile } from '../services/api';
import { Button, Container, Typography, CircularProgress } from '@mui/material';

interface UserProfile {
  nombre: string;
  email: string;
}

const ProfilePage: React.FC = () => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const auth = useAuth();

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await getProfile();
        setProfile(response.data.perfil);
      } catch (err) {
        setError('Failed to fetch profile');
      } finally {
        setLoading(false);
      }
    };

    if (auth.isAuthenticated) {
      fetchProfile();
    } else {
      setLoading(false);
    }
  }, [auth.isAuthenticated]);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">{error}</Typography>;
  if (!auth.isAuthenticated) return <Typography>Please login to view your profile.</Typography>;

  return (
    <Container maxWidth="sm">
      <Typography variant="h4" component="h1" gutterBottom>
        Profile
      </Typography>
      {profile && (
        <div>
          <Typography variant="h6">Name: {profile.nombre}</Typography>
          <Typography variant="h6">Email: {profile.email}</Typography>
        </div>
      )}
      <Button variant="contained" onClick={auth.logout} sx={{ mt: 3 }}>
        Logout
      </Button>
    </Container>
  );
};

export default ProfilePage;
