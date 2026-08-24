export interface User {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  createdAt?: string;
}

export interface UserData {
  role: string;
  id: string;
  userEmail: string;
}

export const userColumns = [
  {
    name: 'fullName',
    label: 'Ad Soyad',
    field: 'fullName',
    align: 'left' as const,
  },
  { name: 'email', label: 'E-posta', field: 'email', align: 'left' as const },
  { name: 'role', label: 'Rol', field: 'role', align: 'center' as const },
  { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const },
];

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface UpdateUserRequest {
  fullName?: string;
  email?: string;
  password?: string;
  role?: UserRole;
}

export interface UserForm {
  id: string;
  fullName: string;
  email: string;
  password?: string;
  role: UserRole;
}

export const userRoleOptions = ['ROLE_ADMIN', 'ROLE_USER'] as const;
export type UserRole = (typeof userRoleOptions)[number];

export interface ChangePasswordRequest {
  newPassword: string;
}
