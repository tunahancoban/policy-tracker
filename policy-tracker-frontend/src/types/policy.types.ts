import { formatDate } from '@/utils/dateHelper';

export interface CreatePolicyRequest {
  type: string;
  startDate: string;
  endDate: string;
  premium: number;
  customerId: string;
  isActive: string;
  responsibleUserId: string;
  note: string;
  installment: number;
}

export interface RenewPolicyRequest {
  previousPolicyId: string;
  startDate: string;
  endDate: string;
  premium: number;
  installment: number;
  responsibleUserId: string;
  note?: string;
}

export interface Policy {
  id: string;
  policyId: string;
  customerId: string;
  note: string;
  installment: number;
  type: string;
  startDate: string;
  endDate: string;
  premium: number;
  isActive: string;
  previousPolicyId: string;
  rootPolicyId: string;
  renewalSequence: number;
  responsibleUserId: string;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
  notifiedThresholds: number[];
}

export interface PolicyForm {
  customerId: string;
  type: string;
  note: string;
  installment: number;
  premium: number;
  startDate: string;
  endDate: string;
  responsibleUserId: string;
  isActive: string;
}

export const SORT_FIELD_MAP: Record<string, string> = {
  policyId: 'policyId',
  startDate: 'startDate',
  endDate: 'endDate',
  remainingDays: 'endDate',
  premium: 'premium',
  installment: 'installment',
};

export const policyColumns = [
  {
    name: 'policyId',
    label: 'Poliçe No',
    field: 'policyId',
    align: 'left' as const,
    sortable: true,
  },
  { name: 'type', label: 'Poliçe Türü', field: 'type', align: 'left' as const },
  {
    name: 'customerId',
    label: 'Müşteri',
    field: 'customerId',
    align: 'left' as const,
    sortable: false,
  },
  {
    name: 'startDate',
    label: 'Başlangıç Tarihi',
    field: (row: Policy) =>
      activeOptions.find((opt) => opt.value === row.startDate)?.label || formatDate(row.startDate),
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'endDate',
    label: 'Bitiş Tarihi ',
    field: (row: Policy) =>
      activeOptions.find((opt) => opt.value === row.endDate)?.label || formatDate(row.endDate),
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'remainingDays',
    label: 'Kalan Gün',
    field: 'endDate',
    align: 'center' as const,
    sortable: true,
  },
  {
    name: 'isActive',
    label: 'Aktif/Pasif',
    field: (row: Policy) =>
      activeOptions.find((opt) => opt.value === row.isActive)?.label || row.isActive,
    align: 'center' as const,
  },

  {
    name: 'installment',
    label: 'Ödeme Sayısı',
    field: (row: Policy) => `${row.installment} `,
    align: 'center' as const,
    sortable: true,
  },

  {
    name: 'premium',
    label: 'Prim Tutarı',
    field: (row: Policy) => `${row.premium} TL`,
    align: 'left' as const,
    sortable: true,
  },
  { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const },
];

export const activeOptions = [
  { value: 'ACTIVE', label: 'Aktif' },
  { value: 'PASSIVE', label: 'Pasif' },
];

export const policyTypeOptions = [
  { label: 'Trafik Sigortası', value: 'TRAFIK' },
  { label: 'Afet Sigortası', value: 'DASK' },
  { label: 'KASKO Sigortası', value: 'KASKO' },
  { label: 'Sağlık Sigortası', value: 'SAGLIK' },
];
