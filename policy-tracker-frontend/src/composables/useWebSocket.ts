import { ref, onUnmounted } from 'vue';
import { Client, type IMessage } from '@stomp/stompjs';

export function useWebSocket() {
  const isConnected = ref(false);
  let stompClient: Client | null = null;

  const connect = (onDashboardUpdate?: (data: string) => void) => {
    stompClient = new Client({
      webSocketFactory: () => new WebSocket(import.meta.env.QCLI_WS_URL || ''),
      debug: (str: string) => console.log('[WS Debug]:', str),
      reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
      isConnected.value = true;
      console.log('WebSocket Bağlantısı Başarılı!');

      if (onDashboardUpdate) {
        stompClient?.subscribe('/topic/dashboard-summary', (message: IMessage) => {
          onDashboardUpdate(message.body);
        });
      }
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
