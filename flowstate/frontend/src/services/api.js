import axios from 'axios';

export const AUTH_STORAGE_KEYS = [
  'flowstate_access_token',
  'flowstate_refresh_token',
  'flowstate_user_id',
  'flowstate_username',
  'flowstate_full_name',
  'flowstate_role',
];

const api = axios.create({
  baseURL: 'http://localhost:8089',
  headers: { 'Content-Type': 'application/json' },
});

// Attach the JWT access token to every request.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('flowstate_access_token');
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401 clear the session and send the user back to the login page.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error?.config?.url || '';
    const isAuthCall = url.includes('/api/auth/');
    if (error?.response?.status === 401 && !isAuthCall) {
      AUTH_STORAGE_KEYS.forEach((key) => localStorage.removeItem(key));
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

/** Extracts the most useful human readable message from an axios error. */
export const getErrorMessage = (error, fallback = 'Something went wrong') => {
  const data = error?.response?.data;
  if (data?.fieldErrors && typeof data.fieldErrors === 'object') {
    const joined = Object.values(data.fieldErrors).join(', ');
    if (joined) return joined;
  }
  if (typeof data === 'string' && data) return data;
  return data?.message || fallback;
};

export default api;
