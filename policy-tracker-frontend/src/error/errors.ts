import type { ProblemDetail } from '@/types/api.types';

export class ApiError extends Error {
  readonly status: number;
  readonly problem?: ProblemDetail | undefined;

  constructor(message: string | undefined, status: number, problem?: ProblemDetail) {
    super(message || 'Bir hata oluştu');
    this.name = 'ApiError';
    this.status = status;
    this.problem = problem;
  }
}

export class ValidationError extends ApiError {
  readonly fieldErrors: Record<string, string>;

  constructor(problem: ProblemDetail) {
    super(problem.detail, 400, problem);
    this.name = 'ValidationError';
    this.fieldErrors = problem.errors ?? {};
  }

  // İster .fieldErrors ister .errors olarak erişebilmek için alias getter
  get errors(): Record<string, string> {
    return this.fieldErrors;
  }
}

export class AuthError extends ApiError {
  constructor(problem?: ProblemDetail) {
    super(problem?.detail ?? 'Oturum sonlandı', 401, problem);
    this.name = 'AuthError';
  }
}

export class ForbiddenError extends ApiError {
  constructor(problem?: ProblemDetail) {
    super(problem?.detail ?? 'Bu işlem için yetkiniz yok', 403, problem);
    this.name = 'ForbiddenError';
  }
}

export class NetworkError extends Error {
  constructor(message = 'Sunucuya ulaşılamıyor') {
    super(message);
    this.name = 'NetworkError';
  }
}

// Type guard'lar - store/component'te instanceof yerine kullanılabilir
export function isValidationError(e: unknown): e is ValidationError {
  return e instanceof ValidationError;
}

export function isApiError(e: unknown): e is ApiError {
  return e instanceof ApiError;
}
