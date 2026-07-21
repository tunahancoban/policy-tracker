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
