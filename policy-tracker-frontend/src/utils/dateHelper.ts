// src/utils/dateHelpers.ts

export const calculateRemainingDays = (endDateStr: string | null | undefined): string => {
  if (!endDateStr) return 'Belirsiz';

  const endDate = new Date(endDateStr);
  const today = new Date();

  endDate.setHours(0, 0, 0, 0);
  today.setHours(0, 0, 0, 0);

  const diffTime = endDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays < 0) {
    return 'Süresi Doldu';
  } else if (diffDays === 0) {
    return 'Bugün Son Gün';
  }
  return `${diffDays} Gün Kaldı`;
};

export const getRemainingDaysColor = (endDateStr: string | null | undefined): string => {
  if (!endDateStr) return 'grey';

  const endDate = new Date(endDateStr);
  const today = new Date();
  endDate.setHours(0, 0, 0, 0);
  today.setHours(0, 0, 0, 0);

  const diffTime = endDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  if (diffDays <= 0) {
    return 'grey';
  } else if (diffDays <= 7) {
    return 'negative'; // Kırmızı
  } else if (diffDays <= 15) {
    return 'warning'; //Turuncu
  } else if (diffDays <= 30) {
    return 'yellow-8'; // Sarı
  }
  return 'positive'; // Yeşil
};

export const getPolicyStatusGroup = (
  endDateStr: string | null | undefined,
): 'expired' | 'expiringSoon' | 'active' => {
  if (!endDateStr) return 'active';

  const endDate = new Date(endDateStr);
  const today = new Date();
  endDate.setHours(0, 0, 0, 0);
  today.setHours(0, 0, 0, 0);

  const diffTime = endDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays < 0) {
    return 'expired'; // Süresi geçmiş
  } else if (diffDays <= 30) {
    return 'expiringSoon'; // 30 gün içinde bitecek (Bugün dahil)
  }
  return 'active'; // Aktif (30 günden fazla var)
};

export const formatDate = (dateString: string): string => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return (
    date.toLocaleDateString('tr-TR') +
    ' ' +
    date.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })
  );
};

// ---------------------------------------------------------------------------
// Policy-form date helpers (previously duplicated in NewPolicyModal)
// ---------------------------------------------------------------------------

/**
 * Validates a slash-separated date string in YYYY/MM/DD format.
 * Returns true only when the parsed date components round-trip correctly.
 */
export const isValidDate = (dateStr: string | null | undefined): boolean => {
  if (!dateStr || dateStr.length !== 10) return false;

  const parts = dateStr.split('/');
  if (parts.length !== 3) return false;

  const year  = parseInt(parts[0] as string, 10);
  const month = parseInt(parts[1] as string, 10) - 1;
  const day   = parseInt(parts[2] as string, 10);

  const date = new Date(year, month, day);
  return (
    date.getFullYear() === year &&
    date.getMonth()    === month &&
    date.getDate()     === day
  );
};

/**
 * Converts an ISO dash-separated date (YYYY-MM-DD) to slash-separated (YYYY/MM/DD).
 * Returns an empty string for falsy input.
 */
export const formatDateToSlash = (dateStr?: string): string =>
  dateStr ? dateStr.replace(/-/g, '/') : '';

/** Returns today's date as a YYYY/MM/DD string. */
export const getTodayFormatted = (): string => {
  const today = new Date();
  const year  = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day   = String(today.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
};

/**
 * Given a YYYY/MM/DD start date, returns the same day one year later.
 * Returns an empty string if the input is malformed.
 */
export const getNextYearFormatted = (startDateFormatted: string): string => {
  const parts = startDateFormatted.split('/');
  if (parts.length !== 3) return '';
  const year = parseInt(parts[0] as string, 10) + 1;
  return `${year}/${parts[1]}/${parts[2]}`;
};
