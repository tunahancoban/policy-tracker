// composables/useCustomerDetail.ts
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useCustomerStore } from '@/stores/customer';
import { usePolicyStore } from '@/stores/policy';
import { SORT_FIELD_MAP } from '@/types/policy.types';

export function useCustomerDetail(customerId: string) {
  const customerStore = useCustomerStore();
  const policyStore = usePolicyStore();

  const { selectedCustomer: customer, isLoading: isCustomerLoading } = storeToRefs(customerStore);

  const {
    customerPolicies: policies,
    summary: rawSummary,
    customerPoliciesLoading: isPoliciesLoading,
    customerPoliciesTotal: totalElements,
    customerPoliciesPage: currentPage,
    customerPoliciesPageSize: pageSize,
  } = storeToRefs(policyStore);

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

  const isInitialLoading = computed(() => isCustomerLoading.value && !customer.value);

  const loadAllData = async () => {
    try {
      await Promise.all([customerStore.fetchCustomerById(customerId), fetchPoliciesOnly()]);
    } catch (err) {
      console.error('Veriler yuklenirken bir hata oluştu: ', err);
    }
  };

  const fetchPoliciesOnly = async (sortBy?: string | null, descending?: boolean) => {
    try {
      let sortParam: string | undefined;
      if (sortBy && SORT_FIELD_MAP[sortBy]) {
        const direction = descending ? 'desc' : 'asc';
        sortParam = `${SORT_FIELD_MAP[sortBy]},${direction}`;
      }

      await policyStore.fetchCustomerPoliciesAndSummary(
        customerId,
        currentPage.value.toString(),
        pageSize.value.toString(),
        sortParam,
      );
    } catch (err) {
      console.error('Poliçeler yüklenirken hata oluştu: ', err);
    }
  };

  return {
    customer,
    policies,
    summary,
    isInitialLoading,
    isPoliciesLoading,
    totalElements,
    currentPage,
    pageSize,
    loadAllData,
    fetchPoliciesOnly,
  };
}
