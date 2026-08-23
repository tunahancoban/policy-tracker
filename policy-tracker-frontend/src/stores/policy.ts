import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { CreatePolicyRequest, Policy, RenewPolicyRequest } from '../types/policy.types';
import type { CustomerSummary } from '../types/dashboard.types';
import { policyService } from '@/restservices/policyService';
import { dashboardService } from '@/restservices/dashboardService';

export const usePolicyStore = defineStore('policy', () => {
  // Genel Liste State
  const policies = ref<Policy[]>([]);
  const isLoading = ref<boolean>(false);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const currentPage = ref(0);
  const pageSize = ref(5);

  const selectedPolicy = ref<Policy | null>(null);

  const customerPolicies = ref<Policy[]>([]);
  const customerPoliciesLoading = ref<boolean>(false);
  const customerPoliciesTotal = ref(0);
  const customerPoliciesTotalPages = ref(0);
  const customerPoliciesPage = ref(0);
  const customerPoliciesPageSize = ref(5);

  const summary = ref<CustomerSummary | null>(null);

  const fetchPolicies = async (params: Record<string, string> = {}) => {
    isLoading.value = true;
    try {
      const result = await policyService.getPolicy(params);
      policies.value = result.content;
      totalElements.value = result.totalElements;
      totalPages.value = result.totalPages;
      return result;
    } catch (err) {
      policies.value = [];
      totalElements.value = 0;
      totalPages.value = 0;
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchPolicyById = async (policyId: string) => {
    isLoading.value = true;
    try {
      const policy = await policyService.getPolicyById(policyId);
      selectedPolicy.value = policy;
      return policy;
    } catch (err) {
      selectedPolicy.value = null;
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchCustomerPoliciesAndSummary = async (
    customerId: string,
    page: string,
    size: string,
    sort?: string,
  ) => {
    customerPoliciesLoading.value = true;
    isLoading.value = true;
    try {
      const [policiesRes, summaryRes] = await Promise.all([
        policyService.getPolicy({
          customerId,
          page,
          size,
          ...(sort ? { sort } : {}),
        }),
        dashboardService.getCustomerSummary(customerId),
      ]);

      customerPolicies.value = policiesRes.content;
      customerPoliciesTotal.value = policiesRes.totalElements;
      customerPoliciesTotalPages.value = policiesRes.totalPages;
      summary.value = summaryRes;

      return { policies: policiesRes, summary: summaryRes };
    } catch (err) {
      customerPolicies.value = [];
      customerPoliciesTotal.value = 0;
      customerPoliciesTotalPages.value = 0;
      summary.value = null;
      throw err;
    } finally {
      customerPoliciesLoading.value = false;
      isLoading.value = false;
    }
  };

  const addPolicy = async (newPolicy: CreatePolicyRequest) => {
    isLoading.value = true;
    try {
      const addedPolicy = await policyService.addPolicy(newPolicy);
      policies.value.unshift(addedPolicy);
      totalElements.value += 1;
      return addedPolicy;
    } finally {
      isLoading.value = false;
    }
  };

  const updatePolicy = async (policyId: string, updatedPolicy: Partial<Policy>) => {
    isLoading.value = true;
    try {
      const result = await policyService.updatePolicy(policyId, updatedPolicy);
      const targetId = result.policyId || policyId;

      const index = policies.value.findIndex((c) => c.policyId === targetId);
      if (index !== -1) policies.value[index] = result;

      const customerIndex = customerPolicies.value.findIndex((c) => c.policyId === targetId);
      if (customerIndex !== -1) customerPolicies.value[customerIndex] = result;

      if (selectedPolicy.value?.policyId === targetId) {
        selectedPolicy.value = result;
      }

      return result;
    } finally {
      isLoading.value = false;
    }
  };

  const deletePolicy = async (policyId: string) => {
    isLoading.value = true;
    try {
      await policyService.deletePolicy(policyId);
      policies.value = policies.value.filter((c) => c.policyId !== policyId);
      customerPolicies.value = customerPolicies.value.filter((c) => c.policyId !== policyId);
      totalElements.value = Math.max(0, totalElements.value - 1);
      customerPoliciesTotal.value = Math.max(0, customerPoliciesTotal.value - 1);
    } finally {
      isLoading.value = false;
    }
  };

  const renewPolicy = async (renewData: RenewPolicyRequest) => {
    isLoading.value = true;
    try {
      const renewedPolicy = await policyService.renewPolicy(renewData);
      policies.value.unshift(renewedPolicy);
      totalElements.value += 1;
      return renewedPolicy;
    } finally {
      isLoading.value = false;
    }
  };

  return {
    policies,
    isLoading,
    totalElements,
    totalPages,
    currentPage,
    pageSize,
    fetchPolicies,

    selectedPolicy,
    fetchPolicyById,

    customerPolicies,
    customerPoliciesLoading,
    customerPoliciesTotal,
    customerPoliciesTotalPages,
    customerPoliciesPage,
    customerPoliciesPageSize,
    fetchCustomerPoliciesAndSummary,
    summary,

    addPolicy,
    updatePolicy,
    deletePolicy,
    renewPolicy,
  };
});
