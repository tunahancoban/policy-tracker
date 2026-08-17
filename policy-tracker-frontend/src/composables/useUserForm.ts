// src/composables/useUserForm.ts
import { ref } from 'vue';
import type { User, UserForm, RegisterRequest, UpdateUserRequest } from '@/types/user.types';
import { diffObjects } from '@/utils/diffObjects';

const INITIAL_FORM: UserForm = {
  id: '',
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  role: 'ROLE_USER',
};

const PATCHABLE_KEYS: (keyof User)[] = ['firstName', 'lastName', 'email', 'role'];

export function useUserForm() {
  const form = ref<UserForm>({ ...INITIAL_FORM });
  const newPassword = ref('');
  const wantsPasswordChange = ref(false);
  const isEditMode = ref(false);
  const originalUser = ref<User | null>(null);

  const resetForCreate = () => {
    isEditMode.value = false;
    form.value = { ...INITIAL_FORM };
    newPassword.value = '';
    wantsPasswordChange.value = false;
    originalUser.value = null;
  };

  const resetForEdit = (user: User) => {
    isEditMode.value = true;
    originalUser.value = user;
    form.value = {
      id: user.id,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      role: user.role,
    };
    newPassword.value = '';
    wantsPasswordChange.value = false;
  };

  const buildCreatePayload = (): RegisterRequest => ({
    firstName: form.value.firstName,
    lastName: form.value.lastName,
    email: form.value.email,
    password: form.value.password!,
    role: form.value.role,
  });

  const buildUpdatePayload = (): UpdateUserRequest | null => {
    if (!originalUser.value) {
      throw new Error('Güncellenecek kullanıcı bilgisi eksik.');
    }

    const patchData: UpdateUserRequest = diffObjects(
      originalUser.value,
      form.value,
      PATCHABLE_KEYS,
    );

    if (wantsPasswordChange.value && form.value.password) {
      patchData.password = form.value.password;
    }

    return Object.keys(patchData).length === 0 ? null : patchData;
  };

  return {
    form,
    newPassword,
    wantsPasswordChange,
    isEditMode,
    originalUser,
    resetForCreate,
    resetForEdit,
    buildCreatePayload,
    buildUpdatePayload,
  };
}
