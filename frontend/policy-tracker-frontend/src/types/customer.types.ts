/**
 * Customer type definitions and table column configuration.
 * Single source of truth — imported by pages, composables, and components.
 */

/** Matches the backend CustomerData contract returned by the REST API. */
export interface Customer {
  customerId: string;
  firstName: string;
  lastName: string;
  identityNumber: string;
  email: string;
  phoneNumber: string;
  city: string;
  district: string;
  fullAddress: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

/** QTable column definitions for the customer list. */
export const customerColumns = [
  {
    name: 'customerId',
    label: 'Müşteri ID',
    field: 'customerId',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'fullName',
    label: 'Ad Soyad',
    field: (row: Customer) => `${row.firstName} ${row.lastName}`,
    align: 'left' as const,
    sortable: true,
  },
  { name: 'identityNumber', label: 'T.C. No', field: 'identityNumber', align: 'left' as const },
  { name: 'email', label: 'E-posta', field: 'email', align: 'left' as const },
  { name: 'phoneNumber', label: 'Telefon', field: 'phoneNumber', align: 'left' as const },
  { name: 'city', label: 'Şehir', field: 'city', align: 'left' as const, sortable: true },
  { name: 'active', label: 'Aktif/Pasif', field: 'active', align: 'center' as const },
  { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const },
];

export const initialForm = {
  customerId: '',
  firstName: '',
  lastName: '',
  identityNumber: '',
  email: '',
  phoneNumber: '',
  city: '',
  district: '',
  fullAddress: '',
  active: true,
  createdAt: '',
  updatedAt: '',
};
