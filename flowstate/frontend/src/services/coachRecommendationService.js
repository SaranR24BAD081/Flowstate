import api from './api';

const coachRecommendationService = {
  issue: (data) => api.post('/api/recommendations', data),
  getForPractitioner: (practitionerId, params) =>
    api.get(`/api/recommendations/practitioner/${practitionerId}`, { params }),
  getMyPending: () => api.get('/api/recommendations/mine/pending'),
  getMyIssued: (params) => api.get('/api/recommendations/mine/issued', { params }),
  acknowledge: (id) => api.put(`/api/recommendations/${id}/acknowledge`),
  dismiss: (id) => api.put(`/api/recommendations/${id}/dismiss`),
  getPractitioners: () => api.get('/api/users/practitioners'),
};

export default coachRecommendationService;
