import { api } from '../boot/axios';
import type { Notification } from '../types/notification.types';
import type { Page } from '../types/api.types';

export const notificationService = {
  async fetchNotifications(params: { page: number; size: number }) {
    const response = await api.get<Page<Notification>>('rest/api/notifications', {
      params,
    });
    return response.data;
  },

  async fetchUnreadCount() {
    const response = await api.get<number>('rest/api/notifications/unread');
    return response.data;
  },

  async markAsRead(notificationId: string) {
    await api.patch(`rest/api/notifications/mark-as-read/${notificationId}`);
  },
};
