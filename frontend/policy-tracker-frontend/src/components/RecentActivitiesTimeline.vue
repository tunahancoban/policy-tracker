<template>
    <div class="q-px-sm q-py-xs scroll" style="max-height: 400px; overflow-y: auto;">
        <q-list bordered separator class="rounded-borders">
            <q-item-label header class="text-weight-bold text-subtitle1">
                Son İşlemler
            </q-item-label>

            <q-item v-for="(activity, index) in activities" :key="index" class="q-py-md">
                <!-- Orta Detay Alanı -->
                <q-item-section>
                    <q-item-label class="text-weight-medium">{{ activity.type }}</q-item-label>
                    <q-item-label caption lines="2" class="text-grey-8">
                        {{ activity.detail }}
                    </q-item-label>
                    <q-item-label caption class="text-caption text-grey-6 q-mt-xs">
                        <q-icon name="person" size="14px" class="q-mr-xs" />{{ activity.user }}
                    </q-item-label>
                </q-item-section>

                <!-- Sağ Tarih Alanı -->
                <q-item-section side top>
                    <q-badge outline color="primary" class="q-pa-xs">
                        {{ formatDate(activity.dateTime) }}
                    </q-badge>
                </q-item-section>
            </q-item>
        </q-list>
    </div>
</template>

<script setup lang="ts">
import type { Activity } from '@/types/dashboard.types';
import { formatDate } from '@/utils/dateHelper';

defineProps<{ activities: Activity[] }>();

</script>