import { defineStore } from 'pinia';
import { ref } from 'vue';
import { installmentService } from '../restservices/installmentService';
import type { Installment } from '../types/installment.types';

export const useInstallmentStore = defineStore('installment', () => {
  const installments = ref<Installment[]>([]);
  const isLoading = ref<boolean>(false);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const currentPage = ref(0);
  const pageSize = ref(5);

  const fetchInstallments = async (params: Record<string, string> = {}) => {
    isLoading.value = true;
    try {
      const result = await installmentService.getInstallment(params);
      installments.value = result.content;
      totalElements.value = result.totalElements;
      totalPages.value = result.totalPages;
    } catch (error) {
      console.error('Poliçeler yüklenirken hata oluştu:', error);
      installments.value = [];
      totalElements.value = 0;
      totalPages.value = 0;
    } finally {
      isLoading.value = false;
    }
  };

  const setPaymentStatus = async (installmentId: number, installmentNo: number, status: string) => {
    isLoading.value = true;
    try {
      const updatedInstallment = await installmentService.updateInstallment(
        installmentId.toString(),
        { status: status },
      );
      const index = installments.value.findIndex((i) => i.installmentNo === installmentNo);
      if (index !== -1) {
        installments.value[index] = updatedInstallment;
      }
    } catch (error) {
      console.error('Taksit güncellenirken hata oluştu:', error);
    } finally {
      isLoading.value = false;
    }
  };

  return {
    installments,
    isLoading,
    totalElements,
    totalPages,
    currentPage,
    pageSize,
    fetchInstallments,
    setPaymentStatus,
  };
});
