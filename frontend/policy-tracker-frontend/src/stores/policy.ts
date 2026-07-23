// src/stores/policy.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Policy } from '../types/policy.types';
import type { CustomerSummary } from '../types/dashboard.types'; // Summary tipinizin bulunduğu yer
import { policyService } from '@/restservices/policyService';
import { dashboardService } from '@/restservices/dashboardService';

export const usePolicyStore = defineStore('policy', () => {
  // --- STATE ---
  const policies = ref<Policy[]>([]);
  const summary = ref<CustomerSummary | null>(null); // ➕ Özet verisini tutmak için eklendi
  const isLoading = ref<boolean>(false);

  // --- ACTIONS ---

  // Tüm poliçeleri genel çekme
  const fetchPolicies = async (params: Record<string, string> = {}) => {
    isLoading.value = true;
    try {
      policies.value = await policyService.getPolicy(params);
    } catch (error) {
      console.error('Poliçeler yüklenirken hata oluştu:', error);
      policies.value = [];
    } finally {
      isLoading.value = false;
    }
  };

  // ➕ YENİ: Müşteriye özel poliçeleri ve özet kart verilerini eşzamanlı çeken action
  const fetchCustomerPoliciesAndSummary = async (customerId: string) => {
    isLoading.value = true;
    try {
      const [policiesRes, summaryRes] = await Promise.all([
        policyService.getPolicy({ customerId }),
        dashboardService.getCustomerSummary(customerId),
      ]);
      policies.value = policiesRes;
      summary.value = summaryRes;
    } catch (error) {
      console.error('Müşteri poliçeleri ve özeti yüklenirken hata oluştu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const addPolicy = async (newPolicy: Policy) => {
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
      const targetId = result.policyId || policyId; // Bazen result içinden id gelmeyebilir garantisi
      const index = policies.value.findIndex((c) => c.policyId === targetId);

      if (index !== -1) {
        policies.value[index] = result;
      }
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
      }
    } catch (error) {
      console.error('Poliçe silinemedi: ', error);
      throw error;
    }
  };

  return {
    policies,
    summary, // ➕ dışarıya aktarıldı
    isLoading,
    fetchPolicies,
    fetchCustomerPoliciesAndSummary, // ➕ dışarıya aktarıldı
    addPolicy,
    updatePolicy,
    deletePolicy,
  };
});
