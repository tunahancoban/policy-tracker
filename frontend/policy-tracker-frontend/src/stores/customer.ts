import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Customer } from '../types/customer.types';
import { customerService } from '@/restservices/customerService';

export const useCustomerStore = defineStore('customer', () => {
  const customerData = ref<Customer[]>([]);
  const selectedCustomer = ref<Customer | null>(null);
  const isInitialized = ref<boolean>(false);
  const isLoading = ref<boolean>(false);

  const totalElements = ref(0);
  const totalPages = ref(0);
  const currentPage = ref(0);
  const pageSize = ref(10);

  const fetchCustomerData = async (searchParams: Record<string, string> = {}) => {
    isLoading.value = true;
    try {
      const result = await customerService.getCustomer({
        ...searchParams,
        page: searchParams.page ?? String(currentPage.value),
        size: searchParams.size ?? String(pageSize.value),
      });
      customerData.value = result.content;
      totalElements.value = result.totalElements;
      totalPages.value = result.totalPages;
      isInitialized.value = true;
    } catch (error) {
      console.error('Müşteri verisi çekilemedi:', error);
      customerData.value = [];
      totalElements.value = 0;
      totalPages.value = 0;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchCustomerById = async (customerId: string) => {
    isLoading.value = true;
    try {
      const customer = await customerService.getCustomerById(customerId);
      selectedCustomer.value = customer;
    } catch (error) {
      console.error('Müşteri bilgisi çekilemedi', error);
      selectedCustomer.value = null;
    } finally {
      isLoading.value = false;
    }
  };

  const addCustomer = async (newCustomer: Customer) => {
    isLoading.value = true;
    try {
      const addedCustomer = await customerService.addCustomer(newCustomer);
      customerData.value.push(addedCustomer);
    } catch (error) {
      console.error('Müşteri eklenemedi: ', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const updateCustomer = async (updatedCustomer: Customer) => {
    isLoading.value = true;
    try {
      const result = await customerService.updateCustomer(updatedCustomer);
      const index = customerData.value.findIndex((c) => c.customerId === result.customerId);
      if (index !== -1) {
        customerData.value[index] = result;
      }
    } catch (error) {
      console.error('Müşteri güncellemesi başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const deleteCustomer = async (customerId: string) => {
    try {
      const response = await customerService.deleteCustomer(customerId);
      if (response) {
        customerData.value = customerData.value.filter((c) => c.customerId !== customerId);
      }
    } catch (error) {
      console.error('deleteCustomer başarısız oldu:', error);
      throw error;
    }
  };

  return {
    customerData,
    selectedCustomer,
    isInitialized,
    isLoading,
    totalElements,
    totalPages,
    currentPage,
    pageSize,
    fetchCustomerData,
    fetchCustomerById,
    addCustomer,
    updateCustomer,
    deleteCustomer,
  };
});
