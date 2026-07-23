import { api } from '../boot/axios';
import type { Customer } from '@/types/customer.types';
import type { ApiResponse } from '@/types/api.types';
import { unwrapList, unwrapListToSingle, unwrapSingle } from '@/utils/apiResponseHandler';
export const customerService = {
  async getCustomer(params?: Record<string, string>): Promise<Customer[]> {
    const response = await api.get<ApiResponse<Customer[]>>(`/rest/api/customer/with-params`, {
      params,
    });
    return unwrapList<Customer>(response);
  },

  async getCustomerById(customerId: string): Promise<Customer> {
    const response = await api.get<ApiResponse<Customer[]>>('/rest/api/customer/with-params', {
      params: { customerId },
    });
    return unwrapListToSingle<Customer>(response);
  },

  async addCustomer(newCustomer: Customer): Promise<Customer> {
    const response = await api.post<ApiResponse<Customer>>(
      `/rest/api/customer/create-customer `,
      newCustomer,
    );
    return unwrapSingle<Customer>(response);
  },

  async updateCustomer(updatedCustomer: Customer) {
    const response = await api.patch<ApiResponse<Customer>>(
      `/rest/api/customer/update-customer/${updatedCustomer.customerId}`,
      updatedCustomer,
    );

    return unwrapSingle<Customer>(response);
  },

  async deleteCustomer(customerId: string): Promise<boolean> {
    const response = await api.delete<ApiResponse<null>>(
      `/rest/api/customer/delete-customer/${customerId}`,
    );
    return response.data.success;
  },
};
