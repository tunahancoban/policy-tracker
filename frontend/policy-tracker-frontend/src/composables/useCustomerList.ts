// composables/useCustomerList.ts
import { storeToRefs } from 'pinia';
import { useCustomerStore } from '@/stores/customer';
import type { Customer } from '@/types/customer.types';

export function useCustomerList() {
  const customerStore = useCustomerStore();
  const { customerData: customers, isLoading } = storeToRefs(customerStore);

  const loadCustomers = () => customerStore.fetchCustomerData();

  const deleteCustomer = async (customerId: string) => {
    await customerStore.deleteCustomer(customerId);
    await loadCustomers();
  };

  const updateCustomerStatus = async (customer: Customer, newStatus: boolean) => {
    await customerStore.updateCustomer({ ...customer, active: newStatus });
  };

  return { customers, isLoading, loadCustomers, deleteCustomer, updateCustomerStatus };
}
