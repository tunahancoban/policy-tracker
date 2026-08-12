<template>
    <q-btn flat dense round icon="notifications" aria-label="Notifications">
        <q-badge v-if="notificationStore.unreadCount > 0" color="red" floating rounded>
            {{ notificationStore.unreadCount > 9 ? '9+' : notificationStore.unreadCount }}
        </q-badge>

        <q-menu anchor="bottom right" self="top right" class="notification-menu" @show="handleMenuShow">
            <div class="notification-panel">
                <div class="row items-center justify-between q-pa-md notification-panel__header">
                    <span class="text-weight-bold text-subtitle1">Bildirimler</span>
                </div>

                <q-separator />

                <q-scroll-area style="height: 360px" class="notification-panel__list" @scroll="handleScroll">
                    <q-list v-if="notificationStore.notifications.length > 0" separator>
                        <q-item v-for="notification in notificationStore.notifications" :key="notification.id" clickable
                            v-close-popup
                            :class="['notification-item', { 'notification-item--unread': !notification.read }]"
                            @click="handleClick($event, notification)">
                            <q-item-section avatar>
                                <q-btn flat round :icon="iconForType(notification.notificationType.toString())"
                                    :color="!notification.read ? 'primary' : 'grey-6'"
                                    @click="markAsRead(notification)" />
                            </q-item-section>

                            <q-item-section>
                                <q-item-label :class="{ 'text-weight-bold': !notification.read }">
                                    {{ notification.title }}
                                </q-item-label>
                                <q-item-label caption lines="2">
                                    {{ notification.message }}
                                </q-item-label>
                                <q-item-label caption class="text-grey-5">
                                    {{ formatDate(notification.createdAt) }}
                                </q-item-label>
                            </q-item-section>

                            <q-item-section v-if="!notification.read" side>
                                <q-badge rounded color="primary" style="width: 8px; height: 8px" />
                            </q-item-section>
                        </q-item>

                        <div v-if="notificationStore.isLoading" class="row justify-center q-pa-sm">
                            <q-spinner color="primary" size="24px" />
                        </div>
                    </q-list>

                    <div v-else-if="!notificationStore.isLoading" class="notification-panel__empty">
                        <q-icon name="notifications_none" size="40px" color="grey-4" />
                        <div class="text-grey-6 q-mt-sm">Henüz bildirim yok</div>
                    </div>
                </q-scroll-area>
            </div>
        </q-menu>
    </q-btn>
</template>

<script setup lang="ts">
import { useNotificationStore } from '@/stores/notification';
import type { Notification } from '@/types/notification.types';
import type { QScrollArea } from 'quasar';
import { useRouter } from 'vue-router';

const router = useRouter();

const notificationStore = useNotificationStore();


function handleMenuShow() {
    if (notificationStore.notifications.length === 0 && !notificationStore.isLoading) {
        void notificationStore.fetchNotifications();
    }
}

function handleScroll(info: Parameters<NonNullable<QScrollArea['onScroll']>>[0]) {
    const { verticalPercentage } = info;
    if (verticalPercentage > 0.9 && !notificationStore.isLastPage && !notificationStore.isLoading) {
        void notificationStore.loadMore();
    }
}

function handleClick(evt: unknown, notification: Notification) {
    void router.push({ name: 'policy-detail', params: { id: notification.policyId } });
}

function markAsRead(notification: Notification) {
    void notificationStore.markAsRead(notification.id);
}

function iconForType(type: string): string {
    switch (type) {
        case 'POLICY_EXPIRY':
            return 'policy';
        case 'INSTALLMENT_DUE':
            return 'payments';
        case 'INSTALLMENT_OVERDUE':
            return 'warning';
        default:
            return 'notifications';
    }
}

function formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleString('tr-TR', {
        day: '2-digit',
        month: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    });
}
</script>

<style scoped>
.notification-menu {
    max-width: 380px;
}

.notification-panel {
    width: 360px;
}

.notification-panel__header {
    background-color: #fafafa;
}

.notification-panel__empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    padding: 32px 0;
}

.notification-item--unread {
    background-color: #f5f8ff;
}
</style>