// src/utils/diffObjects.ts

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
