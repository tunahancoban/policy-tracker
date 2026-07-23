import { api } from '../boot/axios';
import type { User } from '../types/user.types';
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
  async addUser(newUser: User): Promise<User> {
    const response = api.post<ApiResponse<User>>(`/rest/api/user/create-user `, newUser);
    const resData = (await response).data.data;
    if (!(await response).data.success || !resData) return resData;

    return resData;
  },

  async updateUser(updatedUser: User) {
    const response = api.patch<ApiResponse<User>>(
      `/rest/api/user/update-user/${updatedUser.id}`,
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
};
