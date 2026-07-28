import { api } from '../boot/axios';
import type { RegisterRequest, User, UpdateUserRequest } from '../types/user.types';
import type { ApiResponse } from '../types/api.types';
import { unwrapList, unwrapSingle } from '@/utils/apiResponseHandler';

export const userService = {
  async getCustomer(params?: Record<string, string>): Promise<User[]> {
    const response = await api.get<ApiResponse<User[]>>(`/rest/api/user/with-params`, {
      params,
    });

    return unwrapList<User>(response);
  },
  async addUser(newUser: RegisterRequest): Promise<User> {
    const response = await api.post<ApiResponse<User>>(`/rest/api/user/create-user `, newUser);
    return unwrapSingle<User>(response);
  },

  async updateUser(updatedUser: UpdateUserRequest, userId: string): Promise<User> {
    const response = await api.patch<ApiResponse<User>>(
      `/rest/api/user/update-user/${userId}`,
      updatedUser,
    );
    return unwrapSingle<User>(response);
  },

  async deleteUser(userId: string): Promise<boolean> {
    const response = await api.delete<ApiResponse<null>>(`/rest/api/user/delete-user/${userId}`);
    return response.data.success;
  },
  async getProfile(): Promise<User | undefined> {
    const response = await api.get<ApiResponse<User>>('/rest/api/profile/get-profile');
    return unwrapSingle<User>(response);
  },

  async updateProfile(updates: Record<string, unknown>): Promise<User> {
    const response = await api.put<ApiResponse<User>>('/rest/api/profile/update-profile', updates);
    return unwrapSingle<User>(response);
  },
};
