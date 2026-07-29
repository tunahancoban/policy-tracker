// composables/useCustomerList.ts
import { storeToRefs } from 'pinia';
import { useCustomerStore } from '@/stores/customer';
import type { Customer } from '@/types/customer.types';

export function useCustomerList() {
  const customerStore = useCustomerStore();
  const {
    customerData: customers,
    isLoading,
    totalElements,
    currentPage,
    pageSize,
  } = storeToRefs(customerStore);

  const loadCustomers = (params: Record<string, string> = {}) =>
    customerStore.fetchCustomerData(params);

  const deleteCustomer = async (customerId: string) => {
    await customerStore.deleteCustomer(customerId);
  };

  const updateCustomerStatus = async (customer: Customer) => {
    await customerStore.updateCustomer(customer);
  };

  return {
    customers,
    isLoading,
    totalElements,
    currentPage,
    pageSize,
    loadCustomers,
    deleteCustomer,
    updateCustomerStatus,
  };
}
