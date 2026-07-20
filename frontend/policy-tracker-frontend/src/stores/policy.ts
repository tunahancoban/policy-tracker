// src/stores/policy.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { api } from '../boot/axios';
import type { Policy } from '../types/policy.types';
import type { ApiResponse } from '../types/api.types';

export const usePolicyStore = defineStore('policy', () => {
  const policies = ref<Policy[]>([]);
  const isLoading = ref<boolean>(false);

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
      const response = await api.post<ApiResponse<Policy>>(
        '/rest/api/policy/create-policy',
        newPolicy,
      );
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
  const updatePolicy = async (id: string, updatedFields: Partial<Omit<Policy, 'policyId'>>) => {
    isLoading.value = true;
    try {
      // Backend RestResponse<Void> döndüğü için buradaki tipi Void (veya any) olarak güncelliyoruz
      const response = await api.patch<ApiResponse<void>>(
        `/rest/api/policy/update-policy/${id}`,
        updatedFields,
      );

      // Backend başarılı döndüyse (success: true)
      if (response.data.success) {
        // Local state'deki poliçeyi bulup, gönderdiğimiz güncel alanlarla manuel birleştiriyoruz
        const index = policies.value.findIndex((p) => p.policyId === id);
        if (index !== -1) {
          policies.value[index] = {
            ...policies.value[index],
            ...updatedFields,
          } as Policy;
        }
      }
    } catch (error) {
      console.error('Poliçe güncellenirken hata oluştu:', error);
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
    updatePolicy,
  };
});
