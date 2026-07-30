// src/stores/auth.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authService } from '@/restservices/authService';
import type { UserData } from '@/types/user.types';

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

  const clearAuthData = () => {
    userRole.value = null;
    userEmail.value = null;
  };

  const login = async (email: string, password: string) => {
    try {
      const restResponse = await authService.login(email, password);

      if (restResponse) {
        const { role, userEmail } = restResponse;
        saveLoginData({ role, userEmail });
        isInitialized.value = true;
      }
    } catch (error) {
      console.error('Giriş yapılırken hata oluştu:', error);
      clearAuthData();
      throw error;
    }
  };

  const checkAuth = async () => {
    try {
      const userData = await authService.checkAuth();

      if (userData && userData.userEmail) {
        saveLoginData({
          role: userData.role,
          userEmail: userData.userEmail,
        });
      } else {
        clearAuthData();
      }
    } catch (error) {
      console.error('checkAuth başarısız oldu:', error);
      clearAuthData();
    } finally {
      isInitialized.value = true;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout isteği sırasında hata oluştu:', error);
    } finally {
      clearAuthData();
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
    login,
  };
});
