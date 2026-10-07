import api from './api';

const focusSessionService = {
  /** GET /api/sessions - all sessions, paginated (admins / coaches). */
  getAll: (params) => api.get('/api/sessions', { params }),
  /** GET /api/sessions/mine - the caller's sessions, paginated. */
  getMine: (params) => api.get('/api/sessions/mine', { params }),
  getById: (id) => api.get(`/api/sessions/${id}`),
  create: (data) => api.post('/api/sessions', data),
  update: (id, data) => api.put(`/api/sessions/${id}`, data),
  start: (id) => api.put(`/api/sessions/${id}/start`),
  complete: (id) => api.put(`/api/sessions/${id}/complete`),
  abandon: (id, abandonReason) => api.put(`/api/sessions/${id}/abandon`, { abandonReason }),
  /** DELETE /api/sessions/{id} - removes a SCHEDULED session. */
  delete: (id) => api.delete(`/api/sessions/${id}`),
  getStats: () => api.get('/api/sessions/stats'),
};

export default focusSessionService;
