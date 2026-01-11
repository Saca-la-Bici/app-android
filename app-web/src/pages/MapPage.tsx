import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Polyline } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { getRoutes } from '../services/api';
import type { LatLngExpression } from 'leaflet';

interface Route {
  id: string;
  nombre: string;
  puntos: { lat: number; lng: number }[];
}

const MapPage: React.FC = () => {
  const [routes, setRoutes] = useState<Route[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRoutes = async () => {
      try {
        const response = await getRoutes();
        setRoutes(response.data.rutas);
      } catch (err) {
        setError('Failed to fetch routes');
      } finally {
        setLoading(false);
      }
    };

    fetchRoutes();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <MapContainer center={[20.6736, -103.344]} zoom={13} style={{ height: '100vh', width: '100%' }}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {routes.map((route) => (
        <Polyline
          key={route.id}
          positions={route.puntos.map(p => [p.lat, p.lng] as LatLngExpression)}
        />
      ))}
    </MapContainer>
  );
};

export default MapPage;
