import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

interface UserData {
  token: string;
  role: string;
  userEmail: string;
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'));
  const userRole = ref<string | null>(localStorage.getItem('userRole'));
  const userEmail = ref<string | null>(localStorage.getItem('userEmail'));

  const isAuthenticated = computed(() => !!token.value);
  const isAdmin = computed(() => userRole.value === 'ROLE_ADMIN');

  const saveLoginData = (userData: UserData) => {
    token.value = userData.token;
    userRole.value = userData.role;
    userEmail.value = userData.userEmail;

    localStorage.setItem('token', userData.token);
    localStorage.setItem('userRole', userData.role);
    localStorage.setItem('userEmail', userData.userEmail);
  };

  const logout = () => {
    token.value = null;
    userRole.value = null;
    userEmail.value = null;

    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userEmail');
  };

  return {
    token,
    userRole,
    userEmail,
    isAuthenticated,
    isAdmin,
    saveLoginData,
    logout,
  };
});
