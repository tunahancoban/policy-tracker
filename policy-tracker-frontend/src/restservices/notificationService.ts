import { api } from '../boot/axios';
import type { Notification } from '@/types/notification.types';
import type { Page } from '@/types/api.types';

export const notificationService = {
  async fetchNotifications(params: { page: number; size: number }) {
    const response = await api.get<Page<Notification>>('rest/api/notification/get-notifications', {
      params,
    });
    return response.data;
  },

  async fetchUnreadCount() {
    const response = await api.get<number>('rest/api/notification/get-unread');
    return response.data;
  },

  async markAsRead(notificationId: string) {
    await api.patch(`rest/api/notification/mark-as-read/${notificationId}`);
  },
};
