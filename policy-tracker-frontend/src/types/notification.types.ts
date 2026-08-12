export interface Notification {
  id: string;
  notificationType: NotificationTypes;
  policyId: string;
  installmentId: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}

export type NotificationTypes = [
  { label: 'Poliçe Hatırlatma'; value: 'POLICY_EXPIRING' },
  { label: 'Ödeme Hatırlatma'; value: 'INSTALLMENT_OVERDUE' },
];
