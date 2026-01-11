import axios from 'axios';
import { BASE_URL } from '../constants';

const apiClient = axios.create({
  baseURL: BASE_URL,
});

export const getAnnouncements = () => {
  return apiClient.get('/anuncios');
};

export const getEvents = () => {
  return apiClient.get('/actividades/consultar/eventos');
};

export const getRides = () => {
  return apiClient.get('/actividades/consultar/rodadas');
};

export const getWorkshops = () => {
  return apiClient.get('/actividades/consultar/talleres');
};

export const getRoutes = () => {
  return apiClient.get('/mapa/consultarRutas');
};

export const login = (data: any) => {
  return apiClient.post('/session/registrarUsuario', data);
};

export const getProfile = () => {
  return apiClient.get('/session/perfilCompleto');
};
