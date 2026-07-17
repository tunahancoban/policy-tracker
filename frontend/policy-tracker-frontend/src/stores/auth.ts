// src/stores/auth.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { api } from '../boot/axios';
import type { ApiResponse } from '../types/api.types';
interface UserData {
  role: string;
  userEmail: string;
}

export const useAuthStore = defineStore('auth', () => {
  const userRole = ref<string | null>(null);
  const userEmail = ref<string | null>(null);
  const isInitialized = ref<boolean>(false);

  const isAuthenticated = computed(() => !!userEmail.value);
  const isAdmin = computed(() => userRole.value === 'ROLE_ADMIN');

  const saveLoginData = (userData: UserData) => {
    userRole.value = userData.role;
    userEmail.value = userData.userEmail;
  };

  const checkAuth = async () => {
    try {
      const response = await api.get<ApiResponse<UserData>>('/rest/api/profile/me');

      const restResponse = response.data;

      if (restResponse.success && restResponse.data) {
        userRole.value = restResponse.data.role;
        userEmail.value = restResponse.data.userEmail;
      } else {
        userRole.value = null;
        userEmail.value = null;
      }
    } catch (error) {
      console.error('F5 anında checkAuth başarısız oldu:', error);
      userRole.value = null;
      userEmail.value = null;
    } finally {
      isInitialized.value = true;
    }
  };

  const logout = async () => {
    try {
      await api.post<ApiResponse<void>>('/rest/api/auth/logout');
    } catch (error) {
      console.error('Logout isteği sırasında hata oluştu:', error);
    } finally {
      userRole.value = null;
      userEmail.value = null;
      isInitialized.value = false;
    }
  };

  return {
    userRole,
    userEmail,
    isInitialized,
    isAuthenticated,
    isAdmin,
    saveLoginData,
    checkAuth,
    logout,
  };
});
