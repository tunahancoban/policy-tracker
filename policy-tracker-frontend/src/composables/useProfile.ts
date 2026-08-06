import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/user';
import { useAuthStore } from '@/stores/auth';
import type { User } from '@/types/user.types';

export function useProfile() {
  const userStore = useUserStore();
  const authStore = useAuthStore();
  const { isLoading } = storeToRefs(userStore);

  const loadProfile = async (): Promise<User | null> => {
    await authStore.checkAuth();

    if (!authStore.isAuthenticated) {
      throw new Error('NOT_AUTHENTICATED');
    }

    const currentMe = await userStore.fetchProfile();
    if (!currentMe || !currentMe.email) {
      throw new Error('PROFILE_INCOMPLETE');
    }

    return currentMe;
  };

  const updateProfile = async (payload: Record<string, unknown>) => {
    await userStore.updateMyProfile(payload);
  };

  return { isLoading, loadProfile, updateProfile };
}
