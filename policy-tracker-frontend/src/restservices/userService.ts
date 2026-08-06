import { api } from '../boot/axios';
import type { RegisterRequest, User, UpdateUserRequest } from '../types/user.types';
import type { Page } from '../types/api.types';

export const userService = {
  async getUsers(params?: Record<string, string>): Promise<Page<User>> {
    const response = await api.get<Page<User>>(`/rest/api/user/with-params`, {
      params,
    });

    return response.data;
  },
  async addUser(newUser: RegisterRequest): Promise<User> {
    const response = await api.post<User>(`/rest/api/user/create-user`, newUser);
    return response.data;
  },

  async updateUser(updatedUser: UpdateUserRequest, userId: string): Promise<User> {
    const response = await api.patch<User>(`/rest/api/user/update-user/${userId}`, updatedUser);
    return response.data;
  },

  async deleteUser(userId: string) {
    await api.delete<void>(`/rest/api/user/delete-user/${userId}`);
  },
  async getProfile(): Promise<User | undefined> {
    const response = await api.get<User>('/rest/api/profile/get-profile');
    return response.data;
  },

  async updateProfile(updates: Record<string, unknown>): Promise<User> {
    const response = await api.put<User>('/rest/api/profile/update-profile', updates);
    return response.data;
  },
};
