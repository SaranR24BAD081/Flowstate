import api from './api';

const distractionLogService = {
  log: (data) => api.post('/api/distractions', data),
  getForSession: (sessionId) => api.get(`/api/distractions/session/${sessionId}`),
  getBreakdown: () => api.get('/api/distractions/breakdown'),
  getTrend: () => api.get('/api/distractions/trend'),
  getAll: (params) => api.get('/api/distractions', { params }),
};

export default distractionLogService;
