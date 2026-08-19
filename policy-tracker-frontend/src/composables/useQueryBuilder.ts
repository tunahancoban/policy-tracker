// src/composables/useQueryBuilder.ts
//
// PolicyPage.vue ve CustomerPage.vue içindeki buildQueryParams mantığını
// tek bir yerde toplar. Her sayfa kendi arama stratejisini (resolveSearchParam)
// ve sıralama haritasını (sortFieldMap) composable'a iletir.

import { SORT_FIELD_MAP } from '@/types/policy.types';
import { CUSTOMER_SORT_FIELD_MAP } from '@/types/customer.types';

export type SortFieldMap = Record<string, string>;

export interface QueryBuilderOptions {
  /** Sayfa başına kayıt sayısı (mevcut değer, override edilebilir). */
  pageSize: number;
  /** Geçerli sayfa numarası (0-tabanlı). */
  currentPage: number;
  /** Backend sıralama alanı haritası. */
  sortFieldMap: SortFieldMap;
  /** Serbest metin aramasını hangi query param'a yönlendireceğini belirler. */
  resolveSearchParam?: (query: string) => Record<string, string>;
  /** Ek sabit filtreler (örn. responsibleUserId, active). */
  extraParams?: Record<string, string>;
}

export function buildQueryParams(
  options: QueryBuilderOptions,
  overrides?: {
    page?: number;
    size?: number;
    search?: string;
    sortBy?: string | null;
    descending?: boolean;
  },
): Record<string, string> {
  const {
    sortFieldMap,
    resolveSearchParam,
    extraParams = {},
  } = options;

  const page = overrides?.page ?? options.currentPage;
  const size = overrides?.size ?? options.pageSize;
  const sortBy = overrides?.sortBy ?? null;
  const descending = overrides?.descending ?? false;

  const params: Record<string, string> = {
    page: String(page),
    size: String(size),
    ...extraParams,
  };

  // ── Arama parametresi ──────────────────────────────────────────────────────
  if (overrides?.search && overrides.search.trim() && resolveSearchParam) {
    const searchParam = resolveSearchParam(overrides.search.trim());
    Object.assign(params, searchParam);
  }

  // ── Sıralama ───────────────────────────────────────────────────────────────
  if (sortBy && sortFieldMap[sortBy]) {
    const direction = descending ? 'desc' : 'asc';
    params.sort = `${sortFieldMap[sortBy]},${direction}`;
  }

  return params;
}

// ── Hazır arama stratejileri ─────────────────────────────────────────────────

/** PolicyPage için: Poliçe No (TRF...) veya Müşteri ID (CST...) */
export function resolvePolicySearchParam(query: string): Record<string, string> {
  if (query.toUpperCase().startsWith('CST')) {
    return { customerId: query };
  }
  return { policyId: query };
}

/** CustomerPage için: CST ID, e-posta, telefon, TC kimlik veya isim */
export function resolveCustomerSearchParam(query: string): Record<string, string> {
  if (query.toUpperCase().startsWith('CST')) return { customerId: query };
  if (query.includes('@')) return { email: query };
  if (/^0\d{10}$/.test(query) || /^5\d{9}$/.test(query)) return { phoneNumber: query };
  if (/^\d{11}$/.test(query)) return { identityNumber: query };
  return { firstName: query };
}

// ── Kısayol: policy sıralama haritasıyla tam params üret ────────────────────
export { SORT_FIELD_MAP as POLICY_SORT_FIELD_MAP, CUSTOMER_SORT_FIELD_MAP };
