<template>
    <div class="q-pa-sm">
        <div class="text-subtitle1 text-weight-bold text-grey-8 q-mb-md row items-center justify-between">
            <div class="row items-center">
                <q-icon name="history" color="primary" size="22px" class="q-mr-xs" />
                Son İşlemler
            </div>
            <q-badge color="primary" outline>{{ activities?.length || 0 }} İşlem</q-badge>
        </div>

        <!-- Boş Durum -->
        <div v-if="!activities || activities.length === 0" class="empty-state">
            <div class="empty-state__icon">
                <q-icon name="history" size="32px" color="grey-5" />
            </div>
            <div class="empty-state__title">Henüz işlem kaydı yok</div>
            <div class="empty-state__description">Sistem üzerinde gerçekleştirilen işlemler burada listelenir.</div>
        </div>

        <!-- YENİ MODERN LİSTE -->
        <div v-else class="activity-scroll-area" style="max-height: 380px; overflow-y: auto;">
            <q-list class="q-gutter-y-xs">
                <q-item v-for="(activity, index) in activities" :key="index" class="activity-item rounded-borders">
                    <!-- Sol Taraf: İkon Başlığı -->
                    <q-item-section avatar>
                        <q-avatar :color="getIconBgColor(activity.type)" :text-color="getIconColor(activity.type)"
                            size="38px" font-size="20px">
                            <q-icon :name="getActivityIcon(activity.type)" />
                        </q-avatar>
                    </q-item-section>

                    <!-- Orta Taraf: İşlem Tipi ve Detayı -->
                    <q-item-section>
                        <q-item-label class="text-weight-bold text-grey-9 row items-center justify-between">
                            <span>{{ activity.type }}</span>
                            <span class="text-caption text-grey-6 text-weight-regular">
                                {{ formatDate(activity.dateTime) }}
                            </span>
                        </q-item-label>
                        <q-item-label caption class="text-grey-7 q-mt-xs">
                            {{ activity.detail }}
                        </q-item-label>
                    </q-item-section>

                    <!-- Sağ Taraf: İşlemi Yapan Kullanıcı -->
                    <q-item-section side class="gt-xs">
                        <q-chip dense flat color="grey-2" text-color="grey-8" icon="person" size="12px">
                            {{ activity.user }}
                        </q-chip>
                    </q-item-section>
                </q-item>
            </q-list>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { Activity } from '@/types/dashboard.types';
import { formatDate } from '@/utils/dateHelper';

defineProps<{ activities: Activity[] }>();

const getActivityIcon = (type: string): string => {
    const lower = type.toLowerCase();
    if (lower.includes('oluştur') || lower.includes('ekle')) return 'add_circle';
    if (lower.includes('güncelle') || lower.includes('düzenle')) return 'edit_note';
    if (lower.includes('sil')) return 'delete_outline';
    if (lower.includes('yenile') || lower.includes('renew')) return 'autorenew';
    return 'notifications_none';
};

const getIconColor = (type: string): string => {
    const lower = type.toLowerCase();
    if (lower.includes('oluştur') || lower.includes('ekle')) return 'positive';
    if (lower.includes('güncelle') || lower.includes('düzenle')) return 'primary';
    if (lower.includes('sil')) return 'negative';
    if (lower.includes('yenile')) return 'warning';
    return 'grey-8';
};

const getIconBgColor = (type: string): string => {
    const lower = type.toLowerCase();
    if (lower.includes('oluştur') || lower.includes('ekle')) return 'green-1';
    if (lower.includes('güncelle') || lower.includes('düzenle')) return 'blue-1';
    if (lower.includes('sil')) return 'red-1';
    if (lower.includes('yenile')) return 'amber-1';
    return 'grey-3';
};
</script>

<style scoped>
.activity-item {
    background: #f8f9fa;
    border: 1px solid #eef0f2;
    transition: all 0.2s ease;
    padding: 10px 14px;
}

.activity-item:hover {
    background: #ffffff;
    border-color: #d1d5db;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transform: translateY(-1px);
}

.activity-scroll-area::-webkit-scrollbar {
    width: 5px;
}

.activity-scroll-area::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 4px;
}
</style>