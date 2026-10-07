import api from './api';

const authService = {
  login: (credentials) => api.post('/api/auth/login', credentials),
  register: (payload) => api.post('/api/auth/register', payload),
  refresh: (refreshToken) => api.post('/api/auth/refresh', { refreshToken }),
};

export default authService;
