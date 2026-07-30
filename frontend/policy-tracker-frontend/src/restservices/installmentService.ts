import { api } from '../boot/axios';
import type { Installment } from '@/types/installment.types';
import type { ApiResponse, Page } from '../types/api.types';
import { unwrapPaged } from '@/utils/apiResponseHandler';

export const installmentService = {
  async getInstallment(params?: Record<string, string>): Promise<Page<Installment>> {
    const response = await api.get<ApiResponse<Page<Installment>>>(
      `/rest/api/installment/with-params`,
      {
        params,
      },
    );
    return unwrapPaged<Installment>(response);
  },
};
