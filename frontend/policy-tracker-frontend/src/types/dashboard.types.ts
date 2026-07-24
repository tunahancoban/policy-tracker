export interface DashboardSummary {
  totalCustomers: number;
  activePolicyNumber: number;
  expiringSoonPolicies: number;
  expiredPolicies: number;
}

export interface CustomerSummary {
  totalPremium: number;
  activePolicyNumber: number;
  expiringSoonPolicies: number;
  expiredPolicies: number;
}
export interface Activity {
  type: string;
  detail: string;
  user: string;
  dateTime: string;
}
export interface ChartResponse {
  typeLabels: Record<string, number>;
  monthlyPremium: Record<string, number>;
}
