<template>
    <div class="row q-col-gutter-sm">
        <div v-for="card in cards" :key="card.key" class="col-12 col-sm-3">
            <q-card class="full-height summary-card shadow-1" :class="card.bgClass">
                <q-card-section class="column justify-between full-height">
                    <!-- Üst Kısım: Başlık ve İkon -->
                    <div class="row items-center justify-between no-wrap">
                        <div class="text-subtitle2 text-grey-8 font-weight-medium">{{ card.label }}</div>
                        <q-icon :name="card.icon" :class="card.textClass" size="22px" />
                    </div>

                    <!-- Alt Kısım: Değer -->
                    <div class="policy-summary-value q-mt-sm" :class="card.textClass">
                        {{ card.value }}
                    </div>
                </q-card-section>
            </q-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { CustomerSummary } from '@/types/dashboard.types';

const props = defineProps<{ summary: CustomerSummary }>();

const formatCurrency = (value: number | null | undefined): string => {
    if (value === null || value === undefined) return '—';
    return value.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' TL';
};

const cards = computed(() => [
    {
        key: 'active',
        label: 'Aktif Poliçe',
        value: props.summary.activePolicyNumber,
        bgClass: 'bg-soft-primary',
        textClass: 'text-primary',
        icon: 'verified'
    },
    {
        key: 'expiring',
        label: 'Yakında Sona Erecek',
        value: props.summary.expiringSoonPolicies,
        bgClass: 'bg-soft-warning',
        textClass: 'text-warning-dark',
        icon: 'schedule'
    },
    {
        key: 'expired',
        label: 'Süresi Dolmuş',
        value: props.summary.expiredPolicies,
        bgClass: 'bg-soft-negative',
        textClass: 'text-negative',
        icon: 'warning'
    },
    {
        key: 'totalPremium',
        label: 'Toplam Prim',
        value: formatCurrency(props.summary.totalPremium),
        bgClass: 'bg-soft-positive',
        textClass: 'text-positive',
        icon: 'payments'
    }
]);
</script>

<style scoped>
.summary-card {
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.text-warning-dark {
    color: #b98e00;
}

.policy-summary-value {
    font-size: clamp(1.2rem, 3vw, 1.75rem);
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: -0.3px;
}
</style>