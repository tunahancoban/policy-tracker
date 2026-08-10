<template>
    <div class="q-px-sm q-py-xs">
        <div class="text-subtitle1 text-weight-bold q-mb-md">Son İşlemler</div>

        <div v-if="!activities || activities.length === 0" class="empty-state">
            <div class="empty-state__icon">
                <q-icon name="history" size="32px" color="grey-5" />
            </div>
            <div class="empty-state__title">Henüz işlem kaydı yok</div>
            <div class="empty-state__description">Sistem üzerinde gerçekleştirilen işlemler burada listelenir.</div>
        </div>

        <q-timeline v-else color="primary" class="activity-timeline" style="max-height: 400px; overflow-y: auto;">
            <q-timeline-entry v-for="(activity, index) in activities" :key="index" :subtitle="formatDate(activity.dateTime)"
                :icon="getActivityIcon(activity.type)">
                <template v-slot:title>
                    <span class="text-weight-medium" style="font-size: 0.9rem;">{{ activity.type }}</span>
                </template>
                <div class="text-grey-8" style="font-size: 0.85rem;">{{ activity.detail }}</div>
                <div class="text-caption text-grey-6 q-mt-xs">
                    <q-icon name="person" size="14px" class="q-mr-xs" />{{ activity.user }}
                </div>
            </q-timeline-entry>
        </q-timeline>
    </div>
</template>

<script setup lang="ts">
import type { Activity } from '@/types/dashboard.types';
import { formatDate } from '@/utils/dateHelper';

defineProps<{ activities: Activity[] }>();

const getActivityIcon = (type: string): string => {
    const lower = type.toLowerCase();
    if (lower.includes('oluştur') || lower.includes('ekle')) return 'add_circle';
    if (lower.includes('güncelle') || lower.includes('düzenle')) return 'edit';
    if (lower.includes('sil')) return 'delete';
    return 'info';
};
</script>