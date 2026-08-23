import { defineStore } from 'pinia';
import { ref } from 'vue';
import { dashboardService } from '@/restservices/dashboardService';
import { policyService } from '@/restservices/policyService';
import type { Policy } from '@/types/policy.types';
import type { DashboardSummary, Activity, ChartResponse } from '@/types/dashboard.types';

export const useDashboardStore = defineStore('dashboard', () => {
  const summary = ref<DashboardSummary>({
    totalCustomers: 0,
    activePolicyNumber: 0,
    expiringSoonPolicies: 0,
    expiredPolicies: 0,
  });

  const activities = ref<Activity[]>([]);

  const chartDataFromApi = ref<ChartResponse>({
    typeLabels: {},
    monthlyPremium: {},
    numberOfCriticalPolicies: 0,
    numberOfNormalPolicies: 0,
    numberOfWarningPolicies: 0,
  });

  const renewalPolicies = ref<Policy[]>([]);
  const renewalTotalRows = ref<number>(0);
  const renewalLoading = ref<boolean>(false);
  const renewalCurrentPage = ref<number>(0);
  const renewalPageSize = ref<number>(5);
  const isDashboardLoading = ref<boolean>(false);

  const fetchDashboard = async (listNumber: number, year: number) => {
    isDashboardLoading.value = true;
    try {
      const [summaryResult, activitiesResult, chartsResult] = await Promise.all([
        dashboardService.getSummary(),
        dashboardService.getRecentActivities(listNumber),
        dashboardService.getCharts(year),
      ]);

      summary.value = summaryResult;
      activities.value = activitiesResult;
      chartDataFromApi.value = chartsResult;
    } catch (err) {
      activities.value = [];
      throw err;
    } finally {
      isDashboardLoading.value = false;
    }
  };

  const fetchRenewalPolicies = async (page: number, size: number, sort?: string) => {
    renewalLoading.value = true;
    try {
      renewalCurrentPage.value = page;
      renewalPageSize.value = size;

      const params: Record<string, string> = {
        page: page.toString(),
        size: size.toString(),
      };

      if (sort) {
        params.sort = sort;
      }

      const pageData = await policyService.getPolicy(params);
      renewalPolicies.value = pageData.content || [];
      renewalTotalRows.value = pageData.totalElements || 0;
    } catch (err) {
      renewalPolicies.value = [];
      renewalTotalRows.value = 0;
      throw err;
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
    renewalCurrentPage,
    renewalPageSize,
    isDashboardLoading,
    fetchDashboard,
    fetchRenewalPolicies,
  };
});
