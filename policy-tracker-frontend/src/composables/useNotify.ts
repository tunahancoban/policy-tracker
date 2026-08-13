// src/composables/useNotify.ts
import { useQuasar } from 'quasar';

const DEFAULTS = {
  position: 'top-right' as const,
  timeout: 4000,
};

export function useNotify() {
  const $q = useQuasar();

  const notifySuccess = (message: string) => {
    $q.notify({
      message,
      color: 'positive',
      icon: 'check_circle',
      ...DEFAULTS,
    });
  };

  const notifyError = (message: string, timeout = 5000) => {
    $q.notify({
      message,
      color: 'negative',
      icon: 'error',
      ...DEFAULTS,
      timeout,
    });
  };

  return { notifySuccess, notifyError };
}
