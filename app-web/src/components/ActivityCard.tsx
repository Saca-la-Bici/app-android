import React from 'react';
import { Card, CardContent, CardMedia, Typography, Button, CardActions } from '@mui/material';
import type { Activity } from '../types';

interface ActivityCardProps {
  activity: Activity;
}

const ActivityCard: React.FC<ActivityCardProps> = ({ activity }) => {
  return (
    <Card sx={{ maxWidth: 345, m: 2 }}>
      <CardMedia
        component="img"
        height="140"
        image={activity.imagen}
        alt={activity.titulo}
      />
      <CardContent>
        <Typography gutterBottom variant="h5" component="div">
          {activity.titulo}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {activity.descripcion}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Date: {activity.fecha}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Time: {activity.hora}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Location: {activity.ubicacion}
        </Typography>
      </CardContent>
      <CardActions>
        <Button size="small">Learn More</Button>
      </CardActions>
    </Card>
  );
};

export default ActivityCard;
