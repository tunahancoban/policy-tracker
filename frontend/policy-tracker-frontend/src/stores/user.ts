// src/stores/user.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { api } from '../boot/axios';
import type { User } from '../types/user.types';
import type { RegisterRequest } from '../types/user.types';
import type { ApiResponse } from '../types/api.types';

export const useUserStore = defineStore('user', () => {
  const users = ref<User[]>([]);
  const isLoading = ref<boolean>(false);

  const fetchUsers = async () => {
    isLoading.value = true;
    try {
      const response = await api.get<ApiResponse<User[]>>('/rest/api/user/with-params');
      if (response.data.success && response.data.data) {
        users.value = response.data.data;
      }
    } catch (error) {
      console.error('Kullanıcılar getirilirken hata:', error);
    } finally {
      isLoading.value = false;
    }
  };

  const addUser = async (newUser: RegisterRequest) => {
    isLoading.value = true;
    try {
      const response = await api.post<ApiResponse<User>>('/rest/api/user/create-user', newUser);
      if (response.data.success && response.data.data) {
        users.value.push(response.data.data);
      }
    } catch (error) {
      console.error('addUser başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const updateUser = async (updatedUser: User) => {
    isLoading.value = true;
    try {
      const response = await api.patch<ApiResponse<User>>(
        `/rest/api/user/update-user/${updatedUser.id}`,
        updatedUser,
      );

      if (response.data.success && response.data.data) {
        const responseData = response.data.data;

        const incomingUser = Array.isArray(responseData) ? responseData[0] : responseData;

        const finalID = incomingUser?.id ?? updatedUser.id;

        const index = users.value.findIndex((u) => u.id === finalID);

        if (index !== -1) {
          users.value[index] = incomingUser ?? updatedUser;
        }
      }
    } catch (error) {
      console.error('updateUser başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };
  const deleteUser = async (id: string) => {
    try {
      const response = await api.delete<ApiResponse<null>>(`/rest/api/user/delete-user/${id}`);
      if (response.data.success) {
        users.value = users.value.filter((u) => u.id !== id);
      } else {
        console.error('Kullanıcı silinemedi:', response.data.message);
      }
    } catch (error) {
      console.error('deleteUser başarısız oldu: ', error);
      throw error;
    }
  };
  const updateMyProfile = async (updates: Record<string, unknown>) => {
    isLoading.value = true;
    try {
      const response = await api.put<ApiResponse<void>>(
        '/rest/api/profile/update-profile',
        updates,
      );
      return response.data;
    } catch (error) {
      console.error('updateMyProfile başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  return { users, isLoading, fetchUsers, updateUser, deleteUser, addUser, updateMyProfile };
});
