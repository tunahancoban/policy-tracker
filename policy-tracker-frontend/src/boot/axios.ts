import axios from 'axios';
console.log('BASE_URL:', import.meta.env.QCLI_BASE_URL);
console.log('FULL ENV:', import.meta.env);

const api = axios.create({
  baseURL: import.meta.env.QCLI_BASE_URL || '',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => {
    return response;
  },
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

    const finalError = error instanceof Error ? error : new Error(String(error));

    return Promise.reject(finalError);
  },
);

export { api };
