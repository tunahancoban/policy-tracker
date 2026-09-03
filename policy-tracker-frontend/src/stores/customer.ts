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
    } catch (err) {
      customerData.value = [];
      totalElements.value = 0;
      totalPages.value = 0;
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const fetchCustomerById = async (customerId: string) => {
    isLoading.value = true;
    try {
      const customer = await customerService.getCustomerById(customerId);
      selectedCustomer.value = customer;
      return customer;
    } catch (err) {
      selectedCustomer.value = null;
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  const addCustomer = async (newCustomer: Customer) => {
    isLoading.value = true;
    try {
      const addedCustomer = await customerService.addCustomer(newCustomer);
      customerData.value.push(addedCustomer);
      totalElements.value += 1;
      return addedCustomer;
    } finally {
      isLoading.value = false;
    }
  };

  const updateCustomer = async (updatedCustomer: Partial<Customer> & { customerId: string }) => {
    isLoading.value = true;
    try {
      const result = await customerService.updateCustomer(updatedCustomer);
      const index = customerData.value.findIndex((c) => c.customerId === result.customerId);
      if (index !== -1) {
        customerData.value[index] = { ...customerData.value[index], ...result };
      }
      if (selectedCustomer.value?.customerId === result.customerId) {
        selectedCustomer.value = { ...selectedCustomer.value, ...result };
      }
      return result;
    } finally {
      isLoading.value = false;
    }
  };

  const deleteCustomer = async (customerId: string) => {
    isLoading.value = true;
    try {
      await customerService.deleteCustomer(customerId);
      customerData.value = customerData.value.filter((c) => c.customerId !== customerId);
      totalElements.value = Math.max(0, totalElements.value - 1);
    } finally {
      isLoading.value = false;
    }
  };

  const searchCustomer = async (searchParams: Record<string, string>) => {
    currentPage.value = 0;
    return fetchCustomerData({
      ...searchParams,
      page: '0',
    });
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
    searchCustomer,
    updateCustomer,
    deleteCustomer,
  };
});
