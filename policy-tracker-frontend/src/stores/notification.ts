import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Notification } from '@/types/notification.types';
import { notificationService } from '@/restservices/notificationService';

export const useNotificationStore = defineStore('notifications', () => {
  const notifications = ref<Notification[]>([]);
  const currentPage = ref(0);
  const totalPages = ref(0);
  const isLastPage = ref(false);
  const unreadCount = ref(0);
  const isLoading = ref(false);

  async function fetchNotifications(page = 0, size = 20) {
    if (isLoading.value) return;

    isLoading.value = true;
    try {
      const data = await notificationService.fetchNotifications({ page, size });

      if (page === 0) {
        notifications.value = data.content;
      } else {
        const existingIds = new Set(notifications.value.map((n) => n.id));
        const newItems = data.content.filter((n: Notification) => !existingIds.has(n.id));
        notifications.value.push(...newItems);
      }

      currentPage.value = data.number;
      totalPages.value = data.totalPages;
      isLastPage.value = data.last;
      return data;
    } catch (err) {
      if (page === 0) {
        notifications.value = [];
      }
      throw err;
    } finally {
      isLoading.value = false;
    }
  }

  async function loadMore() {
    if (isLastPage.value || isLoading.value) return;
    return fetchNotifications(currentPage.value + 1);
  }

  async function fetchUnreadCount() {
    try {
      const data = await notificationService.fetchUnreadCount();
      unreadCount.value = data;
    } catch (err) {
      console.warn('Okunmamış bildirim sayısı alınamadı:', err);
    }
  }

  function addFromSocket(notification: Notification) {
    const exists = notifications.value.some((n) => n.id === notification.id);
    if (!exists) {
      notifications.value.unshift(notification);
      if (!notification.read) {
        unreadCount.value++;
      }
    }
  }

  async function markAsRead(id: string) {
    const target = notifications.value.find((n) => n.id === id);
    if (!target || target.read) return;

    target.read = true;
    unreadCount.value = Math.max(0, unreadCount.value - 1);

    try {
      await notificationService.markAsRead(id);
    } catch (err) {
      target.read = false;
      unreadCount.value++;
      throw err;
    }
  }

  // 6. Sıfırlama
  function reset() {
    notifications.value = [];
    currentPage.value = 0;
    totalPages.value = 0;
    isLastPage.value = false;
    unreadCount.value = 0;
  }

  return {
    notifications,
    currentPage,
    totalPages,
    isLastPage,
    unreadCount,
    isLoading,
    fetchNotifications,
    loadMore,
    fetchUnreadCount,
    addFromSocket,
    markAsRead,
    reset,
  };
});
