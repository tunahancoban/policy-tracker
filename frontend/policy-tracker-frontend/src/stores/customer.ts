import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Customer } from '../types/customer.types';
import { customerService } from '@/restservices/customerService';

export const useCustomerStore = defineStore('customer', () => {
  const customerData = ref<Customer[]>([]);
  const selectedCustomer = ref<Customer | null>(null);
  const isInitialized = ref<boolean>(false);
  const isLoading = ref<boolean>(false);

  const fetchCustomerData = async (searchParams: Record<string, string> = {}) => {
    isLoading.value = true;
    try {
      customerData.value = await customerService.getCustomer(searchParams);
    } catch (error) {
      console.error('Müşteri verisi çekilemedi:', error);
      customerData.value = [];
    } finally {
      isLoading.value = false;
    }
  };

  const fetchAllCustomers = async () => {
    isLoading.value = true;
    try {
      customerData.value = await customerService.getCustomer({});
    } catch (error) {
      console.error('Müşteri verisi çekilemedi:', error);
      customerData.value = [];
    } finally {
      isLoading.value = false;
    }
  };

  const fetchCustomerDataById = async (customerId: string) => {
    isLoading.value = true;
    try {
      const customer = await customerService.getCustomerById(customerId);
      selectedCustomer.value = customer ?? null;
    } catch (error) {
      console.error('Müşteri bilgisi çekilemedi', error);
    } finally {
      isLoading.value = false;
    }
  };

  const addCustomer = async (newCustomer: Customer) => {
    isLoading.value = true;
    try {
      const addedCustomer: Customer = await customerService.addCustomer(newCustomer);
      customerData.value.push(addedCustomer);
    } catch (error) {
      console.error('Müşteri eklenemedi: ', error);
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
    fetchCustomerData,
    addCustomer,
    updateCustomer,
    deleteCustomer,
    fetchCustomerDataById,
    fetchAllCustomers,
  };
});
