export interface Installment {
  id: number;
  policyId: string;
  customerId: string;
  installmentNo: number;
  amount: number;
  status: paymentStatus; //Fix here
  paidAt: string;
  dueDate: string;
}

export const paymentStatusOptions = ['PAID', 'UNPAID', 'DUE'] as const;
export type paymentStatus = (typeof paymentStatusOptions)[number];
