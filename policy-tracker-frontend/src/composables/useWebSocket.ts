import { ref, onUnmounted } from 'vue';
import { Client, type IMessage } from '@stomp/stompjs';
import type { Notification } from '@/types/notification.types';

export interface ConnectCallbacks {
  onDashboardUpdate?: (data: string) => void;
  onNotificationReceived?: (notification: Notification) => void;
}

export function useWebSocket() {
  const isConnected = ref(false);
  let stompClient: Client | null = null;

  // Hem eski tekli callback kullanımını hem de yeni obje bazlı kullanımı destekler
  const connect = (options?: ConnectCallbacks | ((data: string) => void)) => {
    const callbacks: ConnectCallbacks =
      typeof options === 'function' ? { onDashboardUpdate: options } : options || {};

    stompClient = new Client({
      webSocketFactory: () => new WebSocket(import.meta.env.QCLI_WS_URL || ''),
      debug: (str: string) => console.log('[WS Debug]:', str),
      reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
      isConnected.value = true;

      // 1. Dashboard Subscription
      if (callbacks.onDashboardUpdate) {
        stompClient?.subscribe('/topic/dashboard-summary', (message: IMessage) => {
          callbacks.onDashboardUpdate?.(message.body);
        });
      }

      // 2. Notification Subscription
      if (callbacks.onNotificationReceived) {
        stompClient?.subscribe('/topic/notifications', (message: IMessage) => {
          try {
            const notification: Notification = JSON.parse(message.body);
            callbacks.onNotificationReceived?.(notification);
          } catch (err) {
            console.error('[WS Parse Error]:', err);
          }
        });
      }
    };

    stompClient.onDisconnect = () => {
      isConnected.value = false;
    };

    stompClient.onStompError = (frame) => {
      console.error('[WS Error]:', frame.headers['message'], frame.body);
      isConnected.value = false;
    };

    stompClient.activate();
  };

  const disconnect = () => {
    if (stompClient && stompClient.active) {
      void stompClient.deactivate();
      isConnected.value = false;
    }
  };

  onUnmounted(() => {
    disconnect();
  });

  return { isConnected, connect, disconnect };
}
