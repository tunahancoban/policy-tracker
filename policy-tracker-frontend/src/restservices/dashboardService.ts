import { api } from '../boot/axios';
import { type DashboardSummary } from '@/types/dashboard.types';
import type { CustomerSummary, ChartResponse } from '@/types/dashboard.types';
import type { Activity } from '@/types/dashboard.types';

export const dashboardService = {
  async getSummary(): Promise<DashboardSummary> {
    const dashboardSummaryResponse = await api.get<DashboardSummary>(
      `/rest/api/dashboard/get-summary`,
    );
    return dashboardSummaryResponse.data;
  },
  async getCustomerSummary(customerId: string): Promise<CustomerSummary> {
    const customerSummaryResponse = await api.get(`/rest/api/dashboard/get-summary/${customerId}`);
    return customerSummaryResponse.data;
  },
  async getRecentActivities(limit: number): Promise<Activity[]> {
    const activityResponse = await api.get<Activity[]>(
      `/rest/api/dashboard/get-recent-activities/${limit}`,
    );
    return activityResponse.data;
  },
  async getCharts(year: number): Promise<ChartResponse> {
    const chartResponse = await api.get<ChartResponse>(`/rest/api/dashboard/get-charts/${year}`);
    return chartResponse.data;
  },
};
