import { api } from '../boot/axios';
import type { RegisterRequest, User, UpdateUserRequest } from '../types/user.types';
import type { Page } from '../types/api.types';

export const userService = {
  async getUsers(params?: Record<string, string>): Promise<Page<User>> {
    const response = await api.get<Page<User>>(`/rest/api/users`, {
      params,
    });

    return response.data;
  },
  async addUser(newUser: RegisterRequest): Promise<User> {
    const response = await api.post<User>(`/rest/api/users`, newUser);
    return response.data;
  },

  async updateUser(updatedUser: UpdateUserRequest, userId: string): Promise<User> {
    const response = await api.patch<User>(`/rest/api/users/${userId}`, updatedUser);
    return response.data;
  },

  async deleteUser(userId: string) {
    await api.delete<void>(`/rest/api/user/${userId}`);
  },
  async searchUsers(params: Record<string, string>): Promise<Page<User>> {
    const response = await api.get<Page<User>>(`/rest/api/users`, {
      params: { ...params, page: '0', size: '20' },
    });
    return response.data;
  },

  async getProfile(): Promise<User | undefined> {
    const response = await api.get<User>('/rest/api/profile');
    return response.data;
  },

  async updateProfile(updates: Record<string, unknown>): Promise<User> {
    const response = await api.put<User>('/rest/api/profile', updates);
    return response.data;
  },
};
