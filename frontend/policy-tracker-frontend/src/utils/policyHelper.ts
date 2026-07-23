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
