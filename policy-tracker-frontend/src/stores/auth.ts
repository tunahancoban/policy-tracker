// src/stores/auth.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authService } from '@/restservices/authService';
import type { UserData } from '@/types/user.types';

export const useAuthStore = defineStore('auth', () => {
  const userRole = ref<string | null>(null);
  const userEmail = ref<string | null>(null);
  const id = ref<string | null>(null);
  const isInitialized = ref<boolean>(false);

  const isAuthenticated = computed(() => !!userEmail.value);
  const isAdmin = computed(() => userRole.value === 'ROLE_ADMIN');

  const saveLoginData = (userData: UserData) => {
    userRole.value = userData.role;
    id.value = userData.id;
    userEmail.value = userData.userEmail;
  };

  const clearAuthData = () => {
    userRole.value = null;
    userEmail.value = null;
    id.value = null;
  };

  const login = async (email: string, password: string) => {
    const restResponse = await authService.login(email, password);
    if (restResponse) {
      saveLoginData(restResponse);
      isInitialized.value = true;
    }
  };

  const checkAuth = async (): Promise<boolean> => {
    try {
      const userData = await authService.checkAuth();
      if (userData?.userEmail) {
        saveLoginData(userData);
        return true;
      }
      clearAuthData();
      return false;
    } catch {
      clearAuthData();
      return false;
    } finally {
      isInitialized.value = true;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.error('Logout isteği sırasında hata oluştu:', err);
    } finally {
      clearAuthData();
      isInitialized.value = false;
    }
  };

  return {
    id,
    userRole,
    userEmail,
    isInitialized,
    isAuthenticated,
    isAdmin,
    saveLoginData,
    clearAuthData,
    checkAuth,
    logout,
    login,
  };
});
