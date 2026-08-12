export interface CreatePolicyRequest {
  type: string;
  startDate: string;
  endDate: string;
  premium: number;
  customerId: string;
  note: string;
  installment: number;
}

export interface RenewPolicyRequest {
  previousPolicyId: string;
  startDate: string;
  endDate: string;
  premium: number;
  installment: number;
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
  previousPolicyId: string;
  rootPolicyId: string;
  renewalSequence: number;
  createdAt: string;
  updatedAt: string;
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
    name: 'startDate',
    label: 'Başlangıç Tarihi',
    field: 'startDate',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'endDate',
    label: 'Bitiş Tarihi',
    field: 'endDate',
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

export const policyTypeOptions = [
  { label: 'Trafik Sigortası', value: 'TRAFIK' },
  { label: 'Afet Sigortası', value: 'DASK' },
  { label: 'KASKO Sigortası', value: 'KASKO' },
  { label: 'Sağlık Sigortası', value: 'SAGLIK' },
];
