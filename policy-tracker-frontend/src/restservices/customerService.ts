import { api } from '../boot/axios';
import type { Customer } from '../types/customer.types';
import type { Page } from '../types/api.types';
export const customerService = {
  async getCustomer(params?: Record<string, string>): Promise<Page<Customer>> {
    const response = await api.get<Page<Customer>>('/rest/api/customers', {
      params,
    });
    return response.data;
  },

  async getCustomerById(customerId: string): Promise<Customer> {
    const response = await api.get<Customer>(`/rest/api/customers/${customerId}`);
    return response.data;
  },

  async addCustomer(newCustomer: Customer): Promise<Customer> {
    const response = await api.post<Customer>(`/rest/api/customers`, newCustomer);
    return response.data;
  },

  async updateCustomer(updatedCustomer: Customer) {
    const response = await api.patch<Customer>(
      `/rest/api/customers/${updatedCustomer.customerId}`,
      updatedCustomer,
    );

    return response.data;
  },

  async deleteCustomer(customerId: string) {
    await api.delete<void>(`/rest/api/customers/${customerId}`);
  },
};
