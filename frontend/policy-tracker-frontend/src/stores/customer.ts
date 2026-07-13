import { defineStore } from 'pinia';
import { ref } from 'vue';
import { api } from '../boot/axios';

interface CustomerData {
  customerId: string;
  firstName: string;
  lastName: string;
  identityNumber: string;
  email: string;
  phoneNumber: string;
  city: string;
  district: string;
  fullAddress: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export const useCustomerStore = defineStore('customer', () => {
  const customerData = ref<CustomerData[]>([]);
  const isInitialized = ref<boolean>(false);
  const isLoading = ref<boolean>(false);

  const fetchCustomerData = async () => {
    try {
      const response = await api.get<ApiResponse<CustomerData[]>>('/rest/api/customer/with-params');

      const restResponse = response.data;

      if (restResponse.success && restResponse.data) {
        // Gelen veri zaten liste (Array) olduğu için doğrudan eşitliyoruz
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
      // URL'i temiz tutup, nesneyi doğrudan config içindeki params'a paslıyoruz
      const response = await api.get<ApiResponse<CustomerData[]>>(
        '/rest/api/customer/with-params',
        { params: searchParams },
      );

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

  const addCustomer = async (newCustomer: CustomerData) => {
    isLoading.value = true;
    try {
      const response = await api.post<ApiResponse<CustomerData[]>>(
        '/rest/api/customer/create-customer',
        newCustomer,
      );
      if (response.data.success && response.data.data) {
        // Eğer customerData henüz bir dizi değilse boş bir dizi olarak başlat
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

  const updateCustomer = async (updatedCustomer: CustomerData) => {
    isLoading.value = true;
    try {
      const response = await api.patch<ApiResponse<CustomerData[]>>(
        `/rest/api/customer/update-customer/${updatedCustomer.customerId}`,
        updatedCustomer,
      );
      if (response.data.success && response.data.data) {
        if (Array.isArray(customerData.value)) {
          const resData = response.data.data as unknown;

          const targetID = Array.isArray(resData)
            ? (resData as CustomerData[])[0]?.customerId
            : (resData as CustomerData)?.customerId;

          const finalID = targetID ?? updatedCustomer.customerId;

          const index = customerData.value.findIndex((c) => c.customerId === finalID);
          if (index !== -1) {
            const incomingObj = Array.isArray(resData)
              ? (resData as CustomerData[])[0]
              : (resData as CustomerData);

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
