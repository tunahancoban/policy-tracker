export interface Installment {
  policyId: string;
  customerId: string;
  installmentNo: number;
  amount: number;
  paymentStatus: paymentStatus; //Fix here
  paidAt: string;
  dueDate: string;
}

export const paymentStatusOptions = ['PAID', 'UNPAID', 'DUE'] as const;
export type paymentStatus = (typeof paymentStatusOptions)[number];
