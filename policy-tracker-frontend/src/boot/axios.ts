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
  (error) => {
    const backendMessage = error.response?.data?.message;

    if (backendMessage) {
      return Promise.reject(new Error(backendMessage));
    }

    const finalError = error instanceof Error ? error : new Error(String(error));

    return Promise.reject(finalError);
  },
);

export { api };
