import type { Policy } from '@/types/policy.types';

export const formatPolicyPayload = (eventData: Partial<Policy>) => {
  const payload: Partial<Policy> = {};

  if (eventData.customerId) payload.customerId = eventData.customerId;
  if (eventData.premium !== undefined) payload.premium = eventData.premium;

  if (eventData.startDate) {
    payload.startDate = `${eventData.startDate.replace(/\//g, '-')}`;
  }
  if (eventData.endDate) {
    payload.endDate = `${eventData.endDate.replace(/\//g, '-')}`;
  }
  if (eventData.type) {
    payload.type = eventData.type;
  }

  return payload;
};
export const policyColorMap: Record<string, string> = {
  KASKO: '#1976D2',
  TRAFIK: '#26A69A',
  SAGLIK: '#21BA45',
  KONUT: '#F2C037',
  DASK: '#9C27B0',
};

const typeBackgroundMap: Record<string, string> = {
  KASKO: 'blue-2',
  TRAFİK: 'teal-2',
  TRAFIK: 'teal-2',
  SAGLIK: 'green-2',
  KONUT: 'amber-2',
  DASK: 'purple-2',
};
const typeTextMap: Record<string, string> = {
  KASKO: 'blue-9',
  TRAFİK: 'teal-9',
  TRAFIK: 'teal-9',
  SAGLIK: 'green-9',
  KONUT: 'amber-9',
  DASK: 'purple-9',
};

export const getPolicyTypeColor = (type: string): string => typeBackgroundMap[type] || 'grey-3';
export const getPolicyTypeTextColor = (type: string): string => typeTextMap[type] || 'grey-9';
