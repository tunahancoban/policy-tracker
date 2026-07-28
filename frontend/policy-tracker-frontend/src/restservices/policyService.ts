import { api } from '../boot/axios';
import type { Policy } from '../types/policy.types';
import type { ApiResponse, Page } from '../types/api.types';
import { unwrapPaged, unwrapSingle } from '@/utils/apiResponseHandler';

export const policyService = {
  async getPolicy(params?: Record<string, string>): Promise<Page<Policy>> {
    const response = await api.get<ApiResponse<Page<Policy>>>(`/rest/api/policy/with-params`, {
      params,
    });
    return unwrapPaged<Policy>(response);
  },
  async addPolicy(newPolicy: Omit<Policy, 'policyId'>): Promise<Policy> {
    const response = await api.post<ApiResponse<Policy>>(
      `/rest/api/policy/create-policy `,
      newPolicy,
    );
    return unwrapSingle<Policy>(response);
  },

  async updatePolicy(policyId: string, updatedPolicy: Partial<Policy>) {
    const response = await api.patch<ApiResponse<Policy>>(
      `/rest/api/policy/update-policy/${policyId}`,
      updatedPolicy,
    );
    return unwrapSingle<Policy>(response);
  },

  async deletePolicy(policyId: string): Promise<boolean> {
    const response = await api.delete<ApiResponse<null>>(
      `/rest/api/policy/delete-policy/${policyId}`,
    );
    return response.data.success;
  },
};
