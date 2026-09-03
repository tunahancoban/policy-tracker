import axios, { type AxiosError, type AxiosInstance } from 'axios';
import { Notify } from 'quasar';
import { ApiError, AuthError, ForbiddenError, NetworkError, ValidationError } from '../error/errors';
import { useAuthStore } from '../stores/auth';
import { useNotify } from '../composables/useNotify';

// Backend Hata Tipleri
const { notifyError } = useNotify();

export interface SpringDefaultError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  errors?: Record<string, string>;
}

export type ApiErrorResponse = SpringDefaultError & ProblemDetail;

const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api: AxiosInstance = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

function extractErrorMessage(data: unknown, fallback: string): string {
  if (!data) return fallback;

  if (typeof data === 'string') {
    return data;
  }

  if (typeof data === 'object') {
    const errorObj = data as ApiErrorResponse;
    return errorObj.detail || errorObj.message || errorObj.error || fallback;
  }

  return fallback;
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorResponse>) => {
    // 1. Ağ / Timeout Hatası (Sunucuya ulaşılamadı)
    if (!error.response) {
      notifyError('Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin.');
      return Promise.reject(new NetworkError());
    }

    const status = error.response.status;
    const data = error.response.data;
    const originalRequestUrl = error.config?.url;

    // 2. 401 - Oturum Süresi Doldu / Yetkisiz
    if (status === 401 && !originalRequestUrl?.includes('/auth/login-request')) {
      try {
        const auth = useAuthStore();
        auth.clearAuthData();
        await api.post('/auth/logout');
      } catch {
        console.error('Logout request failed. User session may already be invalid.');
      }
      notifyError('Oturum süreniz doldu. Lütfen tekrar giriş yapın.');

      if (window.location.pathname !== '/') {
        window.location.href = '/login';
      }

      return Promise.reject(new AuthError(data as ProblemDetail));
    }

    if (status === 403) {
      const message = extractErrorMessage(data, 'Bu işlem için yetkiniz bulunmamaktadır.');
      notifyError(message);

      return Promise.reject(new ForbiddenError(data as ProblemDetail));
    }

    if (data?.errors && Object.keys(data.errors).length > 0) {
      return Promise.reject(new ValidationError(data as ProblemDetail));
    }

    const errorMessage = extractErrorMessage(data, 'Bir işlem hatası oluştu.');
    Notify.create({
      type: 'negative',
      message: errorMessage,
      position: 'top',
      timeout: 5000,
    });

    return Promise.reject(new ApiError(errorMessage, status, data as ProblemDetail));
  },
);

export { api };
