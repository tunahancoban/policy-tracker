// composables/usePolicyDetail.ts
import { storeToRefs } from 'pinia';
import { ref } from 'vue';
import { usePolicyStore } from '@/stores/policy';
import { useInstallmentStore } from '@/stores/installment';
import { useUserStore } from '@/stores/user';
import type { User } from '@/types/user.types';

export function usePolicyDetail(policyId: string) {
  const policyStore = usePolicyStore();
  const installmentStore = useInstallmentStore();
  const userStore = useUserStore();
  const selectedUser = ref<User | null>(null);

  const { selectedPolicy: policy, isLoading: isLoading } = storeToRefs(policyStore);
  const {
    installments,
    totalElements: installmentsTotal,
    isLoading: isInstallmentsLoading,
  } = storeToRefs(installmentStore);

  const getUser = async (userId: string) => {
    try {
      const user = await userStore.fetchUserById(userId);
      selectedUser.value = user; // .value üzerinden atama yapılır
    } catch (error) {
      console.error('Kullanıcı bilgisi alınırken hata oluştu:', error);
      selectedUser.value = null;
    }
  };
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
    selectedUser,
    installmentsTotal,
    getUser,
    installments,
    isInstallmentsLoading,
    fetchInstallmentsOnly,
  };
}
