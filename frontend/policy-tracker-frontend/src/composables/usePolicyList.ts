import { storeToRefs } from 'pinia';
import { usePolicyStore } from '@/stores/policy';

export function usePolicyList() {
  const policyStore = usePolicyStore();
  const { policies, isLoading } = storeToRefs(policyStore);

  const loadPolicies = (params?: Record<string, string>) => policyStore.fetchPolicies(params);

  return { policies, isLoading, loadPolicies };
}
