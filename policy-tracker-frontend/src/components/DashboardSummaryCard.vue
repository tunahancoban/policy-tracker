<template>
    <div class="row q-col-gutter-md">
        <!-- Skeleton Loading -->
        <template v-if="loading">
            <div v-for="n in 4" :key="n" class="col-12 col-sm-6 col-md-3">
                <q-card class="summary-card q-pa-md">
                    <div class="row items-center q-gutter-md">
                        <div class="skeleton-box" style="width: 48px; height: 48px; border-radius: 6px;" />
                        <div class="col">
                            <div class="skeleton-box skeleton-title" />
                            <div class="skeleton-box skeleton-text" style="width: 40%;" />
                        </div>
                    </div>
                </q-card>
            </div>
        </template>

        <!-- Actual Cards -->
        <template v-else>
            <div v-for="card in cards" :key="card.key" class="col-12 col-sm-6 col-md-3 fade-in-up"
                :class="card.delayClass">
                <q-card class="summary-card q-pa-md full-height">
                    <div class="row items-center no-wrap q-gutter-md">
                        <div class="summary-card__icon-area" :style="{ backgroundColor: card.bgColor }">
                            <q-icon :name="card.icon" :color="card.iconColor" size="24px" />
                        </div>
                        <div class="col">
                            <div class="summary-card__value">{{ card.value }}</div>
                            <div class="summary-card__label">{{ card.label }}</div>
                        </div>
                    </div>
                </q-card>
            </div>
        </template>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { DashboardSummary } from '@/types/dashboard.types';

const props = defineProps<{
    summary: DashboardSummary;
    loading?: boolean;
}>();

const cards = computed(() => [
    {
        key: 'customers',
        label: 'Toplam Müşteri',
        value: props.summary.totalCustomers,
        icon: 'people',
        iconColor: 'primary',
        bgColor: '#e8f0fe',
        delayClass: '',
    },
    {
        key: 'active',
        label: 'Aktif Poliçe',
        value: props.summary.activePolicyNumber,
        icon: 'verified',
        iconColor: 'positive',
        bgColor: '#e6f9ee',
        delayClass: 'fade-in-up-delay-1',
    },
    {
        key: 'expiring',
        label: 'Yakında Sona Erecek',
        value: props.summary.expiringSoonPolicies,
        icon: 'schedule',
        iconColor: 'warning',
        bgColor: '#fff8e1',
        delayClass: 'fade-in-up-delay-2',
    },
    {
        key: 'expired',
        label: 'Süresi Dolmuş',
        value: props.summary.expiredPolicies,
        icon: 'warning',
        iconColor: 'negative',
        bgColor: '#fdecea',
        delayClass: 'fade-in-up-delay-3',
    },
]);
</script>