import { api } from '../boot/axios';
import { type DashboardSummary } from '@/types/dashboard.types';
import type { CustomerSummary, ChartResponse } from '@/types/dashboard.types';
import type { Activity } from '@/types/dashboard.types';
import type { ApiResponse } from '../types/api.types';
import { unwrapList, unwrapSingle } from '@/utils/apiResponseHandler';

export const dashboardService = {
  async getSummary(): Promise<DashboardSummary> {
    const dashboardSummaryResponse = await api.get<ApiResponse<DashboardSummary>>(
      `/rest/api/dashboard/get-summary`,
    );
    return unwrapSingle<DashboardSummary>(dashboardSummaryResponse);
  },
  async getCustomerSummary(customerId: string): Promise<CustomerSummary> {
    const customerSummaryResponse = await api.get(`/rest/api/dashboard/get-summary/${customerId}`);
    return unwrapSingle<CustomerSummary>(customerSummaryResponse);
  },
  async getRecentActivities(limit: number): Promise<Activity[]> {
    const activityResponse = await api.get<ApiResponse<Activity[]>>(
      `/rest/api/dashboard/get-recent-activities/${limit}`,
    );
    return unwrapList<Activity>(activityResponse);
  },
  async getCharts(year: number): Promise<ChartResponse> {
    const chartResponse = await api.get<ApiResponse<ChartResponse>>(
      `/rest/api/dashboard/get-charts/${year}`,
    );
    return unwrapSingle<ChartResponse>(chartResponse);
  },
};
