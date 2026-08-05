import { api } from '../boot/axios';
import type { LoginResponse } from '@/types/api.types';
import type { UserData } from '@/types/user.types';

export const authService = {
  async login(email: string, password: string): Promise<LoginResponse> {
    const response = await api.post('/rest/api/auth/login-request', {
      email: email,
      password: password,
    });

    return response.data;
  },

  async checkAuth(): Promise<UserData> {
    const response = await api.get('/rest/api/profile/me');
    return response.data;
  },

  async logout(): Promise<void> {
    await api.post('/rest/api/auth/logout');
  },
};
