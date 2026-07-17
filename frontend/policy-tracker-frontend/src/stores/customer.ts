import { defineStore } from 'pinia';
import { ref } from 'vue';
import { api } from '../boot/axios';
import type { Customer } from '../types/customer.types';
import type { ApiResponse } from '../types/api.types';

export const useCustomerStore = defineStore('customer', () => {
  const customerData = ref<Customer[]>([]);
  const isInitialized = ref<boolean>(false);
  const isLoading = ref<boolean>(false);

  const fetchCustomerData = async () => {
    try {
      const response = await api.get<ApiResponse<Customer[]>>('/rest/api/customer/with-params');

      const restResponse = response.data;

      if (restResponse.success && restResponse.data) {
        customerData.value = Array.isArray(restResponse.data)
          ? restResponse.data
          : [restResponse.data];
      } else {
        customerData.value = [];
      }
    } catch (error) {
      console.error('F5 anında fetchCustomerData başarısız oldu:', error);
      customerData.value = [];
    } finally {
      isInitialized.value = true;
    }
  };
  const searchCustomer = async (searchParams: Record<string, string>) => {
    isLoading.value = true;
    try {
      const response = await api.get<ApiResponse<Customer[]>>('/rest/api/customer/with-params', {
        params: searchParams,
      });

      if (response.data.success && response.data.data) {
        customerData.value = Array.isArray(response.data.data)
          ? response.data.data
          : [response.data.data];
      } else {
        customerData.value = [];
      }
    } catch (error) {
      console.error('searchCustomer başarısız oldu:', error);
      customerData.value = [];
    } finally {
      isLoading.value = false;
    }
  };

  const addCustomer = async (newCustomer: Customer) => {
    isLoading.value = true;
    try {
      const response = await api.post<ApiResponse<Customer[]>>(
        '/rest/api/customer/create-customer',
        newCustomer,
      );
      if (response.data.success && response.data.data) {
        if (!Array.isArray(customerData.value)) {
          customerData.value = [];
        }

        if (Array.isArray(response.data.data)) {
          customerData.value.push(...response.data.data);
        } else {
          customerData.value.push(response.data.data);
        }
      }
    } catch (error) {
      console.error('addCustomer başarısız oldu:', error);
    } finally {
      isLoading.value = false;
    }
  };

  const updateCustomer = async (updatedCustomer: Customer) => {
    isLoading.value = true;
    try {
      const response = await api.patch<ApiResponse<Customer[]>>(
        `/rest/api/customer/update-customer/${updatedCustomer.customerId}`,
        updatedCustomer,
      );
      if (response.data.success && response.data.data) {
        if (Array.isArray(customerData.value)) {
          const resData = response.data.data as unknown;

          const targetID = Array.isArray(resData)
            ? (resData as Customer[])[0]?.customerId
            : (resData as Customer)?.customerId;

          const finalID = targetID ?? updatedCustomer.customerId;

          const index = customerData.value.findIndex((c) => c.customerId === finalID);
          if (index !== -1) {
            const incomingObj = Array.isArray(resData)
              ? (resData as Customer[])[0]
              : (resData as Customer);

            customerData.value[index] = incomingObj ?? updatedCustomer;
          }
        }
      }
    } catch (error) {
      console.error('updateCustomer başarısız oldu:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  };

  const deleteCustomer = async (customerId: string) => {
    try {
      const response = await api.delete<ApiResponse<null>>(
        `/rest/api/customer/delete-customer/${customerId}`,
      );
      if (response.data.success) {
        customerData.value = [];
      } else {
        console.error('Müşteri silinemedi:', response.data.message);
      }
    } catch (error) {
      console.error('deleteCustomer başarısız oldu:', error);
      throw error;
    }
  };

  return {
    customerData,
    isInitialized,
    isLoading,
    fetchCustomerData,
    addCustomer,
    updateCustomer,
    deleteCustomer,
    searchCustomer,
  };
});
