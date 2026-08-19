import axios from 'axios';

const baseURL = 'http://localhost:8080';

const api = axios.create({
  baseURL: baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequestUrl = error.config?.url;
    if (error.response?.status === 401 && !originalRequestUrl?.includes('/auth/login-request')) {
      try {
        await api.post('/auth/logout');
      } catch {
        console.error('Logout request failed. User session may already be invalid.');
      }

      if (window.location.pathname !== '/') {
        window.location.href = '/';
      }

      return Promise.reject(new Error('Oturum süreniz doldu. Lütfen tekrar giriş yapın.'));
    }

    const backendMessage = error.response?.data?.message;
    if (backendMessage) {
      return Promise.reject(new Error(backendMessage));
    }

    return Promise.reject(error instanceof Error ? error : new Error(String(error)));
  },
);

export { api };