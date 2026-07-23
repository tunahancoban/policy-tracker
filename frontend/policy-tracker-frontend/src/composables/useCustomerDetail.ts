// composables/useCustomerDetail.ts
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useCustomerStore } from '@/stores/customer';
import { usePolicyStore } from '@/stores/policy';
import type { Policy } from '@/types/policy.types';

export function useCustomerDetail(customerId: string) {
  const customerStore = useCustomerStore();
  const policyStore = usePolicyStore();

  const { selectedCustomer: customer, isLoading: isCustomerLoading } = storeToRefs(customerStore);
  const { policies, summary: rawSummary, isLoading: isPolicyLoading } = storeToRefs(policyStore);

  const summary = computed(() => {
    return (
      rawSummary.value ?? {
        totalPremium: 0,
        activePolicyNumber: 0,
        expiringSoonPolicies: 0,
        expiredPolicies: 0,
      }
    );
  });

  const isLoading = computed(() => isCustomerLoading.value || isPolicyLoading.value);

  const loadAllData = async () => {
    try {
      await Promise.all([
        customerStore.fetchCustomerDataById(customerId),
        policyStore.fetchCustomerPoliciesAndSummary(customerId),
      ]);
    } catch (error) {
      console.error('Müşteri detay verileri yüklenirken hata oluştu:', error);
      throw error;
    }
  };
  const updatePolicy = async (policyId: string, patchData: Partial<Policy>) => {
    await policyStore.updatePolicy(policyId, patchData);
    await loadAllData();
  };

  return {
    customer, // artık Customer | null
    policies,
    summary,
    isLoading,
    loadAllData,
    updatePolicy,
  };
}
