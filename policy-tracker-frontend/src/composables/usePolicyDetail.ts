// composables/useCustomerDetail.ts
import { storeToRefs } from 'pinia';
import { usePolicyStore } from '@/stores/policy';
import { useInstallmentStore } from '@/stores/installment';

export function usePolicyDetail(policyId: string) {
  const policyStore = usePolicyStore();
  const installmentStore = useInstallmentStore();
  const { selectedPolicy: policy, isLoading: isLoading } = storeToRefs(policyStore);
  const {
    installments,
    totalElements: installmentsTotal,
    isLoading: isInstallmentsLoading,
  } = storeToRefs(installmentStore);

  const loadAllData = async () => {
    try {
      await Promise.all([
        policyStore.fetchPolicyById(policyId),
        installmentStore.fetchInstallments({
          policyId: policyId,
          page: String(installmentStore.currentPage),
          size: String(installmentStore.pageSize),
        }),
      ]);
    } catch (error) {
      console.error('Poliçe detay verileri yüklenirken hata oluştu:', error);
      throw error;
    }
  };
  const fetchInstallmentsOnly = async () => {
    try {
      await installmentStore.fetchInstallments({
        policyId: policyId,
        page: String(installmentStore.currentPage),
        size: String(installmentStore.pageSize),
      });
    } catch (error) {
      console.error('Poliçeler yüklenirken hata oluştu:', error);
      throw error;
    }
  };

  return {
    policy,
    isLoading,
    loadAllData,
    installmentsTotal,
    installments,
    isInstallmentsLoading,
    fetchInstallmentsOnly,
  };
}
