// composables/useProfileForm.ts
import { ref } from 'vue';
import type { User } from '@/types/user.types';

export function useProfileForm() {
  const profileForm = ref({
    id: '',
    firstName: '',
    lastName: '',
    email: '',
  });
  const password = ref('');
  const confirmPassword = ref('');

  const populateFrom = (user: User) => {
    profileForm.value.id = user.id ?? '';
    profileForm.value.firstName = user.firstName;
    profileForm.value.lastName = user.lastName;
    profileForm.value.email = user.email;
  };

  const validatePasswords = (): string | null => {
    if (password.value && password.value !== confirmPassword.value) {
      return 'Girdiğiniz şifreler birbiriyle eşleşmiyor.';
    }
    return null;
  };

  const buildUpdatePayload = (): Record<string, unknown> => {
    const payload: Record<string, unknown> = {
      firstName: profileForm.value.firstName,
      lastName: profileForm.value.lastName,
      email: profileForm.value.email,
    };
    if (password.value) {
      payload.password = password.value;
    }
    return payload;
  };

  const clearPasswords = () => {
    password.value = '';
    confirmPassword.value = '';
  };

  return {
    profileForm,
    password,
    confirmPassword,
    populateFrom,
    validatePasswords,
    buildUpdatePayload,
    clearPasswords,
  };
}
