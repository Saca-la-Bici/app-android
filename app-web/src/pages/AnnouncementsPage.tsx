import React, { useEffect, useState } from 'react';
import { getAnnouncements } from '../services/api';
import { Card, CardContent, Typography } from '@mui/material';

interface Announcement {
  id: number;
  titulo: string;
  descripcion: string;
}

const AnnouncementsPage: React.FC = () => {
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAnnouncements = async () => {
      try {
        const response = await getAnnouncements();
        setAnnouncements(response.data);
      } catch (err) {
        setError('Failed to fetch announcements');
      } finally {
        setLoading(false);
      }
    };

    fetchAnnouncements();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div style={{ padding: '20px' }}>
      <Typography variant="h4" gutterBottom>
        Announcements
      </Typography>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px' }}>
        {announcements.map((announcement) => (
          <Card key={announcement.id} sx={{ maxWidth: 345 }}>
            <CardContent>
              <Typography variant="h5" component="div">
                {announcement.titulo}
              </Typography>
              <Typography sx={{ mb: 1.5 }} color="text.secondary">
                {announcement.descripcion}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default AnnouncementsPage;
