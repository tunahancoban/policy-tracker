import { api } from '../boot/axios';
import type { RegisterRequest, User, UpdateUserRequest } from '../types/user.types';
import type { ApiResponse } from '../types/api.types';

export const userService = {
  async getCustomer(params?: Record<string, string>): Promise<User[]> {
    const response = api.get<ApiResponse<User[] | User>>(`/rest/api/user/with-params`, {
      params,
    });
    const resData = (await response).data.data;
    if (!(await response).data.success || !resData) return [];

    return Array.isArray(resData) ? resData : [resData];
  },
  async addUser(newUser: RegisterRequest): Promise<User> {
    const response = api.post<ApiResponse<User>>(`/rest/api/user/create-user `, newUser);
    const resData = (await response).data.data;
    if (!(await response).data.success || !resData) return resData;

    return resData;
  },

  async updateUser(updatedUser: UpdateUserRequest, userId: string): Promise<User> {
    const response = api.patch<ApiResponse<User>>(
      `/rest/api/user/update-user/${userId}`,
      updatedUser,
    );
    const resData = (await response).data.data;
    if (!(await response).data.success || !resData) return resData;
    return resData;
  },

  async deleteUser(userId: string): Promise<boolean> {
    const response = await api.delete<ApiResponse<null>>(`/rest/api/user/delete-user/${userId}`);
    return response.data.success;
  },
  async getProfile(): Promise<User | undefined> {
    const response = await api.get<ApiResponse<User>>('/rest/api/profile/get-profile');
    const resData = response.data.data;
    if (!response.data.success || !resData) return undefined;
    return resData;
  },

  async updateProfile(updates: Record<string, unknown>): Promise<User | undefined> {
    const response = await api.put<ApiResponse<User>>('/rest/api/profile/update-profile', updates);
    const resData = response.data.data;
    if (!response.data.success || !resData) return undefined;
    return resData;
  },
};
