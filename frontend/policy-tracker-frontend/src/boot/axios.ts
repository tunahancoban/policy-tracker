import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
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
