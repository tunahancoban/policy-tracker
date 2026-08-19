import type { Policy } from '@/types/policy.types';
import type { paymentStatus } from '@/types/installment.types';

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

// ── Policy type renk haritası ────────────────────────────────────────────────
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

// ── Tarih / para formatları (PolicyDetailPage ve diğer sayfalar için) ────────

/** ISO tarih string'ini Türkçe tarih formatına çevirir. Örn: "02.08.2026" */
export const formatDateOnly = (dateStr: string | null | undefined): string => {
  if (!dateStr) return '—';
  const date = new Date(dateStr);
  return date.toLocaleDateString('tr-TR', { year: 'numeric', month: '2-digit', day: '2-digit' });
};

/** Sayıyı Türkçe para formatında gösterir. Örn: "1.500,00 TL" */
export const formatCurrency = (value: number | null | undefined): string => {
  if (value === null || value === undefined) return '—';
  return value.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' TL';
};

// ── Taksit ödeme durumu yardımcıları ────────────────────────────────────────

/** Quasar renk adını döndürür (q-chip color prop için). */
export const getStatusColor = (status: paymentStatus): string => {
  const map: Record<paymentStatus, string> = {
    PAID: 'positive',
    DUE: 'negative',
    UNPAID: 'warning',
  };
  return map[status] ?? 'warning';
};

/** Kullanıcıya gösterilecek Türkçe etiket döndürür. */
export const getStatusLabel = (status: paymentStatus): string => {
  const map: Record<paymentStatus, string> = {
    PAID: 'Ödendi',
    DUE: 'Vadesi Geçti',
    UNPAID: 'Ödenmedi',
  };
  return map[status] ?? 'Ödenmedi';
};
