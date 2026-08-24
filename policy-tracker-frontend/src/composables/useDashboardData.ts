import { storeToRefs } from 'pinia';
import { useDashboardStore } from '../stores/dashboard';

export function useDashboardData() {
  const dashboardStore = useDashboardStore();

  const {
    summary,
    activities,
    chartDataFromApi,
    renewalPolicies,
    renewalTotalRows,
    renewalLoading,
    renewalCurrentPage,
    renewalPageSize,
    isDashboardLoading,
  } = storeToRefs(dashboardStore);

  const loadDashboard = (listNumber: number, year: number) =>
    dashboardStore.fetchDashboard(listNumber, year);

  const loadRenewalPolicies = (page: number, size: number, sort?: string) =>
    dashboardStore.fetchRenewalPolicies(page, size, sort);

  return {
    summary,
    activities,
    chartDataFromApi,
    renewalPolicies,
    renewalTotalRows,
    renewalLoading,
    renewalCurrentPage,
    renewalPageSize,
    isDashboardLoading,
    loadDashboard,
    loadRenewalPolicies,
  };
}
