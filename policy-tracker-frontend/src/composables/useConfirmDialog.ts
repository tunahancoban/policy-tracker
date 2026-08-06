import { useQuasar } from 'quasar';

interface ConfirmOptions {
  title?: string;
  message: string;
  okLabel?: string;
  cancelLabel?: string;
  color?: string;
}

export function useConfirmDialog() {
  const $q = useQuasar();

  const confirm = (options: ConfirmOptions): Promise<boolean> => {
    return new Promise((resolve) => {
      $q.dialog({
        title: options.title || 'İşlem Onayı',
        message: options.message,
        cancel: {
          label: options.cancelLabel || 'Vazgeç',
          flat: true,
          color: 'grey-7',
        },
        ok: {
          label: options.okLabel || 'Evet, Onayla',
          color: options.color || 'negative',
        },
        persistent: true,
      })
        .onOk(() => resolve(true))
        .onCancel(() => resolve(false))
        .onDismiss(() => resolve(false));
    });
  };

  return { confirm };
}
