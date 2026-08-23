import type { UserRole } from './user.types';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface SortInfo {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}

export interface PageableInfo {
  pageNumber: number;
  pageSize: number;
  sort: SortInfo;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface Page<T> {
  content: T[];
  pageable: PageableInfo;
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: SortInfo;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface LoginResponse {
  role: UserRole;
  id: string;
  jwt_token: string;
  userEmail: string;
}

export interface SpringDefaultError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  errors?: Record<string, string>;
}

// Backend'den dönebilecek tüm hata modelleri
export type ApiErrorResponse = SpringDefaultError & ProblemDetail;
