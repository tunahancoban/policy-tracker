// composables/useDashboardData.ts
import { ref } from 'vue';
import { dashboardService } from '@/restservices/dashboardService';
import { policyService } from '@/restservices/policyService';
import type { Policy } from '@/types/policy.types';
import type { DashboardSummary, Activity, ChartResponse } from '@/types/dashboard.types';

export function useDashboardData() {
  const summary = ref<DashboardSummary>({
    totalCustomers: 0,
    activePolicyNumber: 0,
    expiringSoonPolicies: 0,
    expiredPolicies: 0,
  });
  const activities = ref<Activity[]>([]);
  const chartDataFromApi = ref<ChartResponse>({ typeLabels: {}, monthlyPremium: {} });

  const renewalPolicies = ref<Policy[]>([]);
  const renewalTotalRows = ref<number>(0);
  const renewalLoading = ref(false);

  const loadDashboard = async (listNumber: number) => {
    try {
      const [summaryResult, activitiesResult, chartsResult] = await Promise.all([
        dashboardService.getSummary(),
        dashboardService.getRecentActivities(listNumber),
        dashboardService.getCharts(),
      ]);
      summary.value = summaryResult;
      activities.value = activitiesResult;
      chartDataFromApi.value = chartsResult;
    } catch (error) {
      console.error('Dashboard verileri yüklenirken hata:', error);
      throw error;
    }
  };

  const loadRenewalPolicies = async (page: number, size: number) => {
    renewalLoading.value = true;
    try {
      const params: Record<string, string> = {
        page: page.toString(),
        size: size.toString(),
      };

      const pageData = await policyService.getPolicy(params);

      renewalPolicies.value = pageData.content || [];
      renewalTotalRows.value = pageData.totalElements || 0;
    } catch (error) {
      console.error('Yenileme poliçeleri yüklenirken hata:', error);
      renewalPolicies.value = [];
      renewalTotalRows.value = 0;
    } finally {
      renewalLoading.value = false;
    }
  };

  return {
    summary,
    activities,
    chartDataFromApi,
    renewalPolicies,
    renewalTotalRows,
    renewalLoading,
    loadDashboard,
    loadRenewalPolicies,
  };
}
