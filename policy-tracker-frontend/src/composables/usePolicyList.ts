import { storeToRefs } from 'pinia';
import { usePolicyStore } from '../stores/policy';
import type { CreatePolicyRequest, Policy, RenewPolicyRequest } from '../types/policy.types';

export function usePolicyList() {
  const policyStore = usePolicyStore();

  const { policies, isLoading, totalElements, currentPage, pageSize } = storeToRefs(policyStore);

  const loadPolicies = (params?: Record<string, string>) => policyStore.fetchPolicies(params);

  const createPolicy = async (newPolicy: CreatePolicyRequest) => {
    await policyStore.addPolicy(newPolicy);
    await loadPolicies();
  };

  const renewPolicy = async (newPolicy: RenewPolicyRequest) => {
    await policyStore.renewPolicy(newPolicy);
    await loadPolicies();
  };

  const updatePolicy = async (policyId: string, patchData: Partial<Policy>) => {
    await policyStore.updatePolicy(policyId, patchData);
    await loadPolicies();
  };

  const deletePolicy = async (policyId: string) => {
    await policyStore.deletePolicy(policyId);
    await loadPolicies();
  };

  return {
    policies,
    isLoading,
    totalElements,
    currentPage,
    pageSize,
    loadPolicies,
    updatePolicy,
    createPolicy,
    deletePolicy,
    renewPolicy,
  };
}
