import type { ApiResponse, Page } from '@/types/api.types';

export function unwrapList<T>(response: { data: ApiResponse<T[] | T> }): T[] {
  const resData = response.data.data;
  if (!response.data.success || !resData) throw new Error(response.data.message);
  return Array.isArray(resData) ? resData : [resData];
}

export function unwrapListToSingle<T>(response: { data: ApiResponse<T[]> }): T {
  const resData = response.data.data;
  if (!response.data.success || !resData || !Array.isArray(resData) || resData.length === 0) {
    throw new Error(response.data.message);
  }
  return resData[0]!;
}
export function unwrapSingle<T>(response: { data: ApiResponse<T> }): T {
  const resData = response.data.data;
  if (!response.data.success || !resData) throw new Error(response.data.message);
  return resData;
}
export function unwrapPaged<T>(response: { data: ApiResponse<Page<T>> }): Page<T> {
  const resData = response.data.data;
  if (!response.data.success || !resData) throw new Error(response.data.message);
  return resData;
}
