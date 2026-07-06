import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

interface UserData {
  role: string;
  userEmail: string;
}

export const useAuthStore = defineStore('auth', () => {
  const userRole = ref<string | null>(localStorage.getItem('userRole'));
  const userEmail = ref<string | null>(localStorage.getItem('userEmail'));

  const isAuthenticated = computed(() => !!userEmail.value);
  const isAdmin = computed(() => userRole.value === 'ROLE_ADMIN');

  const saveLoginData = (userData: UserData) => {
    userRole.value = userData.role;
    userEmail.value = userData.userEmail;

    localStorage.setItem('userRole', userData.role);
    localStorage.setItem('userEmail', userData.userEmail);
  };

  const logout = () => {
    userRole.value = null;
    userEmail.value = null;

    localStorage.removeItem('userRole');
    localStorage.removeItem('userEmail');
  };

  return {
    userRole,
    userEmail,
    isAuthenticated,
    isAdmin,
    saveLoginData,
    logout,
  };
});
