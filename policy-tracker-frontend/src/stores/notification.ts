import { defineStore } from 'pinia';
import type { Notification } from '@/types/notification.types';
import { notificationService } from '@/restservices/notificationService';

interface NotificationState {
  notifications: Notification[];
  currentPage: number;
  totalPages: number;
  isLastPage: boolean;
  unreadCount: number;
  isLoading: boolean;
}

export const useNotificationStore = defineStore('notifications', {
  state: (): NotificationState => ({
    notifications: [],
    currentPage: 0,
    totalPages: 0,
    isLastPage: false,
    unreadCount: 0,
    isLoading: false,
  }),

  actions: {
    async fetchNotifications(page = 0, size = 20) {
      if (this.isLoading) return;

      this.isLoading = true;
      try {
        const data = await notificationService.fetchNotifications({ page, size });

        if (page === 0) {
          this.notifications = data.content;
        } else {
          this.notifications.push(...data.content);
        }

        this.currentPage = data.number;
        this.totalPages = data.totalPages;
        this.isLastPage = data.last;
      } catch (error) {
        console.error('Bildirimler yüklenirken hata oluştu:', error);
      } finally {
        this.isLoading = false;
      }
    },

    async loadMore() {
      if (this.isLastPage || this.isLoading) return;
      await this.fetchNotifications(this.currentPage + 1);
    },

    async fetchUnreadCount() {
      try {
        const data = await notificationService.fetchUnreadCount();
        this.unreadCount = data;
      } catch (error) {
        console.error('Okunmamış bildirim sayısı alınırken hata oluştu:', error);
      }
    },

    addFromSocket(notification: Notification) {
      const exists = this.notifications.some((n) => n.id === notification.id);
      if (!exists) {
        this.notifications.unshift(notification);
        if (!notification.read) {
          this.unreadCount++;
        }
      }
    },

    async markAsRead(id: string) {
      const target = this.notifications.find((n) => n.id === id);
      if (!target || target.read) return;

      try {
        await notificationService.markAsRead(id);
        target.read = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      } catch (error) {
        console.error('Bildirim okundu işaretlenirken hata oluştu:', error);
      }
    },

    reset() {
      this.notifications = [];
      this.currentPage = 0;
      this.totalPages = 0;
      this.isLastPage = false;
      this.unreadCount = 0;
    },
  },
});
