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
const policyTypeColor: Record<string, string> = {
  KASKO: '#418fe8',
  TRAFIK: '#24a999',
  SAGLIK: '#529f56',
  KONUT: '#ffb758',
  DASK: '#975bbc',
};

export const getPolicyTypeColor = (type: string | undefined | null): string => {
  if (!type) return '#607D8B';

  const normalizedType = type.trim().toUpperCase();
  return policyTypeColor[normalizedType] || '#607D8B';
};
