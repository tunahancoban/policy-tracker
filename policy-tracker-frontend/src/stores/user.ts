// src/stores/user.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { userService } from '@/restservices/userService';
import type { RegisterRequest, User, UpdateUserRequest } from '@/types/user.types';

export const useUserStore = defineStore('user', () => {
  const users = ref<User[]>([]);
  const isLoading = ref<boolean>(false);

  const fetchUsers = async (params?: Record<string, string>) => {
    isLoading.value = true;
    try {
      users.value = (await userService.getUsers(params)).content;
    } catch (error) {
      console.error('Kullanıcılar getirilirken hata:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchUserById = async (id: string): Promise<User> => {
    isLoading.value = true;
    try {
      const user = await userService.getUsers({ id });
      return user.content.at(0) as User;
    } catch (error) {
      console.error(`Kullanıcı ${id} getirilirken hata:`, error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchProfile = async (): Promise<User | null> => {
    isLoading.value = true;
    try {
      const profile = await userService.getProfile();
      return profile ?? null;
    } catch (error) {
      console.error('Profil getirilirken hata:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const addUser = async (payload: RegisterRequest) => {
    isLoading.value = true;
    try {
      const created = await userService.addUser(payload);
      if (created) {
        users.value.push(created);
      }
    } catch (error) {
      console.error('addUser başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const updateUser = async (updatedUser: UpdateUserRequest, userId: string) => {
    isLoading.value = true;
    try {
      const result = await userService.updateUser(updatedUser, userId);
      if (result) {
        const index = users.value.findIndex((u) => u.id === result.id);
        if (index !== -1) {
          users.value[index] = result;
        }
      }
    } catch (error) {
      console.error('updateUser başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const searchUser = async (searchParams: Record<string, string>) => {
    isLoading.value = true;
    try {
      const result = await userService.searchUsers(searchParams);
      users.value = result.content;
    } catch (error) {
      console.error('Kullanıcı araması başarısız:', error);
      users.value = [];
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const deleteUser = async (id: string) => {
    try {
      await userService.deleteUser(id);
      users.value = users.value.filter((u) => u.id !== id);
    } catch (error) {
      console.error('deleteUser başarısız oldu:', error);
      throw error;
    }
  };

  const updateMyProfile = async (updates: Record<string, unknown>): Promise<User | undefined> => {
    isLoading.value = true;
    try {
      return await userService.updateProfile(updates);
    } catch (error) {
      console.error('updateMyProfile başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  return {
    users,
    isLoading,
    fetchUsers,
    fetchUserById,
    searchUser,
    updateUser,
    deleteUser,
    addUser,
    updateMyProfile,
    fetchProfile,
  };
});
