export interface Policy {
  policyId: string;
  type: string;
  startDate: string;
  endDate: string;
  premium: number;
  customerId: string;
  note: string;
  installmentNumber: number;
}

export interface PolicyForm {
  customerId: string;
  type: string;
  note: string;
  installmentNumber: number;
  premium: number;
  startDate: string;
  endDate: string;
}

export const policyColumns = [
  {
    name: 'policyId',
    label: 'Poliçe No',
    field: 'policyId',
    align: 'left' as const,
    sortable: true,
  },
  { name: 'type', label: 'Poliçe Türü', field: 'type', align: 'left' as const, sortable: true },
  {
    name: 'startDate',
    label: 'Başlangıç Tarihi',
    field: 'startDate',
    align: 'center' as const,
    sortable: true,
  },
  {
    name: 'endDate',
    label: 'Bitiş Tarihi',
    field: 'endDate',
    align: 'center' as const,
    sortable: true,
  },
  { name: 'statu', label: 'Kalan Gün', field: 'endDate', align: 'center' as const, sortable: true },
  {
    name: 'premium',
    label: 'Prim Tutarı',
    field: (row: Policy) => `${row.premium} TL`,
    align: 'right' as const,
  },
  { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const },
];

export const policyTypeOptions = [
  { label: 'Trafik Sigortası', value: 'TRAFIK' },
  { label: 'Afet Sigortası', value: 'DASK' },
  { label: 'KASKO Sigortası', value: 'KASKO' },
  { label: 'Sağlık Sigortası', value: 'SAGLIK' },
];

export const stateOptions = [
  { label: 'Aktif', value: true },
  { label: 'Pasif', value: false },
];
