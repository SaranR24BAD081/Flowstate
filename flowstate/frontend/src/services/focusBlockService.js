import api from './api';

const focusBlockService = {
  create: (data) => api.post('/api/blocks', data),
  getMine: () => api.get('/api/blocks/mine'),
  getCapacityPlan: () => api.get('/api/blocks/capacity-plan'),
  protect: (id) => api.put(`/api/blocks/${id}/protect`),
  unprotect: (id) => api.put(`/api/blocks/${id}/unprotect`),
  delete: (id) => api.delete(`/api/blocks/${id}`),
};

export default focusBlockService;
