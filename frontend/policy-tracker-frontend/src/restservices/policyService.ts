import { api } from '../boot/axios';
import type { Policy } from '../types/policy.types';
import type { ApiResponse } from '../types/api.types';

export const policyService = {
  async getPolicy(params?: Record<string, string>): Promise<Policy[]> {
    const response = await api.get<ApiResponse<Policy[] | Policy>>(`/rest/api/policy/with-params`, {
      params,
    });
    const resData = response.data.data;
    if (!response.data.success || !resData) return [];

    return Array.isArray(resData) ? resData : [resData];
  },
  async addPolicy(newPolicy: Policy): Promise<Policy> {
    const response = await api.post<ApiResponse<Policy>>(
      `/rest/api/policy/create-policy `,
      newPolicy,
    );
    const resData = response.data.data;
    if (!response.data.success || !resData) return resData;

    return resData;
  },

  async updatePolicy(policyId: string, updatedPolicy: Partial<Policy>) {
    const response = await api.patch<ApiResponse<Policy>>(
      `/rest/api/policy/update-policy/${policyId}`,
      updatedPolicy,
    );
    const resData = response.data.data;
    if (!response.data.success || !resData) throw new Error(response.data.message);
    return resData;
  },

  async deletePolicy(policyId: string): Promise<boolean> {
    const response = await api.delete<ApiResponse<null>>(
      `/rest/api/policy/delete-policy/${policyId}`,
    );
    return response.data.success;
  },
};
