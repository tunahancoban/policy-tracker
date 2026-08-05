import { api } from '../boot/axios';
import type { Customer } from '@/types/customer.types';
import type { Page } from '@/types/api.types';
export const customerService = {
  async getCustomer(params?: Record<string, string>): Promise<Page<Customer>> {
    const response = await api.get<Page<Customer>>('/rest/api/customer/with-params', {
      params,
    });
    return response.data;
  },

  async getCustomerById(customerId: string): Promise<Customer> {
    const response = await api.get<Customer>(`/rest/api/customer/get-customer/${customerId}`);
    return response.data;
  },

  async addCustomer(newCustomer: Customer): Promise<Customer> {
    const response = await api.post<Customer>(`/rest/api/customer/create-customer`, newCustomer);
    return response.data;
  },

  async updateCustomer(updatedCustomer: Customer) {
    const response = await api.patch<Customer>(
      `/rest/api/customer/update-customer/${updatedCustomer.customerId}`,
      updatedCustomer,
    );

    return response.data;
  },

  async deleteCustomer(customerId: string) {
    await api.delete<void>(`/rest/api/customer/delete-customer/${customerId}`);
  },
};
