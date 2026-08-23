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
      const response = await userService.getUsers(params);
      users.value = response.content;
      return response;
    } catch (err) {
      users.value = [];
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchUserById = async (id: string): Promise<User | null> => {
    isLoading.value = true;
    try {
      const response = await userService.getUsers({ id });
      return (response.content.at(0) as User) ?? null;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchProfile = async (): Promise<User | null> => {
    isLoading.value = true;
    try {
      const profile = await userService.getProfile();
      return profile ?? null;
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
      return created;
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
      return result;
    } finally {
      isLoading.value = false;
    }
  };

  const updateMyProfile = async (updates: Record<string, unknown>): Promise<User | undefined> => {
    isLoading.value = true;
    try {
      return await userService.updateProfile(updates);
    } finally {
      isLoading.value = false;
    }
  };

  const searchUser = async (searchParams: Record<string, string>): Promise<void> => {
    isLoading.value = true;
    try {
      const result = await userService.searchUsers(searchParams);
      users.value = result.content;
    } catch (err) {
      users.value = [];
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const deleteUser = async (id: string) => {
    isLoading.value = true;
    try {
      await userService.deleteUser(id);
      users.value = users.value.filter((u) => u.id !== id);
    } finally {
      isLoading.value = false;
    }
  };

  return {
    users,
    isLoading,
    fetchUsers,
    fetchUserById,
    fetchProfile,
    addUser,
    updateUser,
    updateMyProfile,
    searchUser,
    deleteUser,
  };
});
