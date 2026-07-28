import type { ApiResponse, Page } from '@/types/api.types';

export function unwrapList<T>(response: { data: ApiResponse<T[] | T> }): T[] {
  const resData = response.data.data;
  if (!response.data.success || !resData) return [];
  return Array.isArray(resData) ? resData : [resData];
}

export function unwrapListToSingle<T>(response: { data: ApiResponse<T[]> }): T {
  const resData = response.data.data;
  if (!resData || !Array.isArray(resData) || resData.length === 0) {
    throw new Error('Böyle bir id mevcut değil');
  }
  return resData[0]!;
}
export function unwrapSingle<T>(response: { data: ApiResponse<T> }): T {
  const resData = response.data.data;
  if (!response.data.success || !resData)
    throw new Error(`Bir hata gerçekleşti: ` + response.data.message);
  return resData;
}
export function unwrapPaged<T>(response: { data: ApiResponse<Page<T>> }): Page<T> {
  const resData = response.data.data;
  if (!response.data.success || !resData)
    throw new Error(`Bir hata gerçekleşti: ` + response.data.message);
  return resData;
}
