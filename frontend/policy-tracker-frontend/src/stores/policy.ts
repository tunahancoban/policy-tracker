// src/stores/policy.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { api } from '../boot/axios';
import type { Policy } from '../types/policy.types';
import type { ApiResponse } from '../types/api.types';

export const usePolicyStore = defineStore('policy', () => {
  const policies = ref<Policy[]>([]);
  const isLoading = ref<boolean>(false);

  // Genel poliçe listesini parametreli filtrelerle çeken fonksiyon
  const fetchPolicies = async (params: Record<string, string | null | undefined> = {}) => {
    isLoading.value = true;
    try {
      const response = await api.get<ApiResponse<Policy[]>>('/rest/api/policy/with-params', {
        params,
      });

      if (response.data.success && response.data.data) {
        policies.value = Array.isArray(response.data.data)
          ? response.data.data
          : [response.data.data];
      } else {
        policies.value = [];
      }
    } catch (error) {
      console.error('Poliçeler yüklenirken hata oluştu:', error);
      policies.value = [];
    } finally {
      isLoading.value = false;
    }
  };

  const addPolicy = async (newPolicy: Omit<Policy, 'policyId'>) => {
    isLoading.value = true;
    try {
      const response = await api.post<ApiResponse<Policy>>('/rest/api/policy/create', newPolicy);
      if (response.data.success && response.data.data) {
        policies.value.push(response.data.data);
      }
    } catch (error) {
      console.error('Poliçe eklenirken hata oluştu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  return {
    policies,
    isLoading,
    fetchPolicies,
    addPolicy,
  };
});
