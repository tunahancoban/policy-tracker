import { api } from '../boot/axios';
import type { DashboardSummary } from '@/types/dashboard.types';
import type { CustomerSummary } from '@/types/dashboard.types';
import type { ApiResponse } from '../types/api.types';

export const dashboardService = {
  async getSummary(): Promise<DashboardSummary> {
    const summaryResponse = await api.get<ApiResponse<DashboardSummary>>(
      `/rest/api/dashboard/get-summary`,
    );
    const resData: DashboardSummary = summaryResponse.data.data;

    return resData;
  },
  async getCustomerSummary(customerId: string): Promise<CustomerSummary> {
    const customerSummaryResponse = await api.get(`/rest/api/dashboard/get-summary/${customerId}`);
    const resData: CustomerSummary = customerSummaryResponse.data.data;

    return resData;
  },
};
