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

  const loadRenewalPolicies = async () => {
    renewalLoading.value = true;
    try {
      const allPolicies = await policyService.getPolicy();
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const thirtyDaysLater = new Date(today);
      thirtyDaysLater.setDate(thirtyDaysLater.getDate() + 30);

      renewalPolicies.value = allPolicies
        .filter((p) => {
          const end = new Date(p.endDate);
          end.setHours(0, 0, 0, 0);
          return end >= today && end <= thirtyDaysLater;
        })
        .sort((a, b) => new Date(a.endDate).getTime() - new Date(b.endDate).getTime());
    } finally {
      renewalLoading.value = false;
    }
  };

  return {
    summary,
    activities,
    chartDataFromApi,
    renewalPolicies,
    renewalLoading,
    loadDashboard,
    loadRenewalPolicies,
  };
}
