// composables/usePolicyDetail.ts
import { storeToRefs } from 'pinia';
import { ref } from 'vue';
import { usePolicyStore } from '../stores/policy';
import { useInstallmentStore } from '../stores/installment';
import { useUserStore } from '../stores/user';
import type { User } from '../types/user.types';

export function usePolicyDetail(policyId: string) {
  const policyStore = usePolicyStore();
  const installmentStore = useInstallmentStore();
  const userStore = useUserStore();
  const selectedUser = ref<User | null>(null);

  const { selectedPolicy: policy, isLoading } = storeToRefs(policyStore);
  const {
    installments,
    totalElements: installmentsTotal,
    isLoading: isInstallmentsLoading,
  } = storeToRefs(installmentStore);

  const getUser = async (userId: string) => {
    try {
      selectedUser.value = await userStore.fetchUserById(userId);
    } catch (error) {
      console.error('Kullanıcı bilgisi alınırken hata oluştu:', error);
      selectedUser.value = null;
    }
  };

  const fetchInstallmentsOnly = async (page: number = 1, size: number = 5) => {
    try {
      await installmentStore.fetchInstallments({
        policyId,
        page: page - 1,
        size,
      });
    } catch (error) {
      console.error('Taksitler yüklenirken hata oluştu:', error);
      throw error;
    }
  };

  const loadAllData = async () => {
    try {
      await Promise.all([
        policyStore.fetchPolicyById(policyId),
        fetchInstallmentsOnly(installmentStore.currentPage || 1, installmentStore.pageSize || 5),
      ]);
    } catch (error) {
      console.error('Poliçe detay verileri yüklenirken hata oluştu:', error);
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
