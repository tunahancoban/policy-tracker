import { api } from '../boot/axios';
import type { Installment } from '../types/installment.types';
import type { Page } from '../types/api.types';

export const installmentService = {
  async getInstallment(params?: Record<string, string | number>): Promise<Page<Installment>> {
    const response = await api.get<Page<Installment>>(`/rest/api/installments`, {
      params,
    });
    return response.data;
  },
  async updateInstallment(
    installmentId: string,
    params?: Record<string, string>,
  ): Promise<Installment> {
    const response = await api.patch<Installment>(
      `/rest/api/installments/${installmentId}`,
      {},
      {
        params,
      },
    );
    return response.data;
  },
};
