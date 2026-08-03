// src/stores/policy.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Policy } from '../types/policy.types';
import type { CustomerSummary } from '../types/dashboard.types';
import { policyService } from '@/restservices/policyService';
import { dashboardService } from '@/restservices/dashboardService';

export const usePolicyStore = defineStore('policy', () => {
  const policies = ref<Policy[]>([]);
  const isLoading = ref<boolean>(false);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const currentPage = ref(0);
  const pageSize = ref(5);

  const selectedPolicy = ref<Policy | null>();

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
    } catch (error) {
      console.error('Poliçeler yüklenirken hata oluştu:', error);
      policies.value = [];
      totalElements.value = 0;
      totalPages.value = 0;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchPolicyById = async (policyId: string) => {
    isLoading.value = true;
    try {
      const policy = await policyService.getPolicyById(policyId);
      selectedPolicy.value = policy;
    } catch (error) {
      console.error('Poliçe bilgisi çekilemedi', error);
      selectedPolicy.value = null;
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
    } catch (error) {
      console.error('Müşteri poliçeleri ve özeti yüklenirken hata oluştu:', error);
      throw error;
    } finally {
      customerPoliciesLoading.value = false;
      isLoading.value = false;
    }
  };

  const addPolicy = async (newPolicy: Omit<Policy, 'policyId'>) => {
    isLoading.value = true;
    try {
      const addedPolicy = await policyService.addPolicy(newPolicy);
      policies.value.push(addedPolicy);
    } catch (error) {
      console.error('Poliçe eklenirken hata oluştu:', error);
      throw error;
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
    } catch (error) {
      console.error('Poliçe güncellenirken hata oluştu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const deletePolicy = async (policyId: string) => {
    try {
      const response = await policyService.deletePolicy(policyId);
      if (response) {
        policies.value = policies.value.filter((c) => c.policyId !== policyId);
        customerPolicies.value = customerPolicies.value.filter((c) => c.policyId !== policyId);
      }
    } catch (error) {
      console.error('Poliçe silinemedi: ', error);
      throw error;
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
  };
});
