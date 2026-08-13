// src/utils/diffObjects.ts

/**
 * İki obje arasında değeri farklılaşan alanları bulur ve
 * sadece değişen alanları içeren yeni bir obje döndürür (PATCH payload'ı gibi).
 *
 * Yeni bir form alanı eklendiğinde bu fonksiyonun değişmesine gerek yoktur;
 * `keys` listesine eklemek yeterlidir. (OCP)
 */
export function diffObjects<T extends object>(
  original: T,
  updated: Partial<T>,
  keys: (keyof T)[],
): Partial<T> {
  const result: Partial<T> = {};

  for (const key of keys) {
    const updatedValue = updated[key];
    if (updatedValue !== undefined && updatedValue !== original[key]) {
      result[key] = updatedValue;
    }
  }

  return result;
}
