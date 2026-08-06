import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/user';
import type { RegisterRequest, UpdateUserRequest } from '@/types/user.types';

export function useUserList() {
  const userStore = useUserStore();
  const { users, isLoading } = storeToRefs(userStore);

  const loadUsers = () => userStore.fetchUsers();

  const addUser = async (payload: RegisterRequest) => {
    await userStore.addUser(payload);
    await loadUsers();
  };

  const updateUser = async (user: UpdateUserRequest, userId: string) => {
    await userStore.updateUser(user, userId);
    await loadUsers();
  };

  const deleteUser = async (userId: string) => {
    await userStore.deleteUser(userId);
    await loadUsers();
  };

  return { users, isLoading, loadUsers, addUser, updateUser, deleteUser };
}
