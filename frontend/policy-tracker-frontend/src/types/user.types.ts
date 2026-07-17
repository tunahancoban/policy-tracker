export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ROLE_ADMIN' | 'ROLE_USER';
  createdAt?: string;
}

export const userColumns = [
  {
    name: 'fullName',
    label: 'Ad Soyad',
    field: (row: User) => `${row.firstName} ${row.lastName}`,
    align: 'left' as const,
  },
  { name: 'email', label: 'E-posta', field: 'email', align: 'left' as const },
  { name: 'role', label: 'Rol', field: 'role', align: 'center' as const },
  { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const },
];

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  role: 'ROLE_ADMIN' | 'ROLE_USER';
}
