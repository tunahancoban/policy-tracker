export interface Installment {
  id: number;
  policyId: string;
  customerId: string;
  installmentNo: number;
  amount: number;
  status: paymentStatus; //Fix here
  paidAt: string;
  dueDate: string;
  notifiedThresholds: number[];
}

export const paymentStatusOptions = ['PAID', 'UNPAID', 'DUE'] as const;
export type paymentStatus = (typeof paymentStatusOptions)[number];
export type InstallmentOption = 1 | 3 | 6;

export interface InstallmentSelectOption {
    label: string;
    value: InstallmentOption;
}

export const installmentOptions: InstallmentSelectOption[] = [
    { label: 'Tek Çekim (Peşin)', value: 1 },
    { label: '3 Taksit', value: 3 },
    { label: '6 Taksit', value: 6 }
];