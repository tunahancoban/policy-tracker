// src/stores/auth.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { api } from '../boot/axios';

interface UserData {
  role: string;
  userEmail: string;
}

// Backend'den gelen genel API yanıt yapısı
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export const useAuthStore = defineStore('auth', () => {
  const userRole = ref<string | null>(null);
  const userEmail = ref<string | null>(null);
  const isInitialized = ref<boolean>(false);

  const isAuthenticated = computed(() => !!userEmail.value);

  const saveLoginData = (userData: UserData) => {
    userRole.value = userData.role;
    userEmail.value = userData.userEmail;
  };

  const checkAuth = async () => {
    try {
      // 1. Axios tipini ApiResponse<UserData> olarak güncelledik
      const response = await api.get<ApiResponse<UserData>>('/rest/api/auth/me');

      const restResponse = response.data;

      // 2. Gelen verinin başarılı olduğunu ve içinin dolu olduğunu kontrol ediyoruz
      if (restResponse.success && restResponse.data) {
        userRole.value = restResponse.data.role;
        userEmail.value = restResponse.data.userEmail;
      } else {
        // Eğer success false geldiyse state'i temizle
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

  const logout = () => {
    userRole.value = null;
    userEmail.value = null;
    isInitialized.value = false;
  };

  return {
    userRole,
    userEmail,
    isInitialized,
    isAuthenticated,
    saveLoginData,
    checkAuth,
    logout,
  };
});
