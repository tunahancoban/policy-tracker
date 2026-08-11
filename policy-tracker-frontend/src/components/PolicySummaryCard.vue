<template>
    <div class="row q-col-gutter-sm">
        <div class="col-12 col-sm-3">
            <q-card class="full-height summary-card shadow-1">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Aktif Poliçe</div>
                    <div class="policy-summary-value text-primary q-mt-sm">
                        {{ summary.activePolicyNumber }}
                    </div>
                </q-card-section>
            </q-card>
        </div>

        <div class="col-12 col-sm-3">
            <q-card class="full-height summary-card shadow-1">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Yakında Sona Erecek</div>
                    <div class="policy-summary-value text-warning q-mt-sm">
                        {{ summary.expiringSoonPolicies }}
                    </div>
                </q-card-section>
            </q-card>
        </div>

        <div class="col-12 col-sm-3">
            <q-card class="full-height summary-card shadow-1">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Süresi Dolmuş</div>
                    <div class="policy-summary-value text-negative q-mt-sm">
                        {{ summary.expiredPolicies }}
                    </div>
                </q-card-section>
            </q-card>
        </div>

        <div class="col-12 col-sm-3">
            <q-card class="full-height summary-card shadow-1">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Toplam Prim</div>
                    <div class="policy-summary-value text-positive q-mt-sm">
                        {{ formatCurrency(summary.totalPremium) }}
                    </div>
                </q-card-section>
            </q-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { CustomerSummary } from '@/types/dashboard.types';

defineProps<{ summary: CustomerSummary }>();

const formatCurrency = (value: number | null | undefined): string => {
    if (value === null || value === undefined) return '—';
    return value.toLocaleString('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' TL';
};
</script>

<style scoped>
.summary-card {
    background-color: #ffffff;
}

.policy-summary-value {
    font-size: clamp(1.2rem, 3vw, 1.75rem);
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: -0.3px;
}
</style>