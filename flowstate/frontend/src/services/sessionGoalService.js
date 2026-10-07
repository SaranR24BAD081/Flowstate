import api from './api';

const sessionGoalService = {
  attach: (data) => api.post('/api/goals', data),
  getForSession: (sessionId) => api.get(`/api/goals/session/${sessionId}`),
  markOutcome: (id, achievedMinutes) => api.put(`/api/goals/${id}/outcome`, { achievedMinutes }),
  getCompletionRate: () => api.get('/api/goals/completion-rate'),
};

export default sessionGoalService;
