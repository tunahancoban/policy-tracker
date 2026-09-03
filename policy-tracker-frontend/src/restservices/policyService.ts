import { api } from '../boot/axios';
import type { Policy, CreatePolicyRequest, RenewPolicyRequest } from '../types/policy.types';
import type { Page } from '../types/api.types';

export const policyService = {
  async getPolicy(params?: Record<string, string>): Promise<Page<Policy>> {
    const response = await api.get<Page<Policy>>(`/rest/api/policies`, {
      params,
    });
    return response.data;
  },

  async getPolicyById(policyId: string): Promise<Policy> {
    const response = await api.get<Policy>(`/rest/api/policies/${policyId}`);
    return response.data;
  },

  async addPolicy(newPolicy: CreatePolicyRequest): Promise<Policy> {
    const response = await api.post<Policy>(`/rest/api/policies`, newPolicy);
    return response.data;
  },

  async renewPolicy(newPolicy: RenewPolicyRequest): Promise<Policy> {
    const response = await api.post<Policy>(`/rest/api/policies/renew`, newPolicy);
    return response.data;
  },

  async updatePolicy(policyId: string, updatedPolicy: Partial<Policy>) {
    const response = await api.patch<Policy>(
      `/rest/api/policies/${policyId}`,
      updatedPolicy,
    );
    return response.data;
  },

  async deletePolicy(policyId: string) {
    await api.delete<void>(`/rest/api/policies/${policyId}`);
  },
};
