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
