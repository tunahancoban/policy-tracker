import { api } from '../boot/axios';
import type { Installment } from '@/types/installment.types';
import type { Page } from '../types/api.types';

export const installmentService = {
  async getInstallment(params?: Record<string, string>): Promise<Page<Installment>> {
    const response = await api.get<Page<Installment>>(`/rest/api/installment/with-params`, {
      params,
    });
    return response.data;
  },
};
