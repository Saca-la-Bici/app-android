import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Footer from './components/Footer';
import HomePage from './pages/HomePage';
import ActivitiesPage from './pages/ActivitiesPage';
import AnnouncementsPage from './pages/AnnouncementsPage';
import StatsPage from './pages/StatsPage';
import ProfilePage from './pages/ProfilePage';
import MapPage from './pages/MapPage';
import FaqPage from './pages/FaqPage';
import RidesPage from './pages/RidesPage';
import LoginPage from './pages/LoginPage';
import { AuthProvider } from './AuthContext';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
        <Header />
        <main style={{ flex: '1' }}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/activities" element={<ActivitiesPage />} />
            <Route path="/announcements" element={<AnnouncementsPage />} />
            <Route path="/stats" element={<StatsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/map" element={<MapPage />} />
            <Route path="/faq" element={<FaqPage />} />
            <Route path="/rides" element={<RidesPage />} />
            <Route path="/login" element={<LoginPage />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </AuthProvider>
  );
};

export default App;
