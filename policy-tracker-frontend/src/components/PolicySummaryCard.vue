<template>
    <div class="row q-col-gutter-sm">
        <div class="col-12 col-sm-3">
            <q-card flat bordered class="full-height">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Aktif Poliçe</div>
                    <div class="text-h4 text-weight-bold text-primary q-mt-sm">
                        {{ summary.activePolicyNumber }}
                    </div>
                </q-card-section>
            </q-card>
        </div>

        <div class="col-12 col-sm-3">
            <q-card flat bordered class="full-height">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Yakında Sona Erecek</div>
                    <div class="text-h4 text-weight-bold text-warning q-mt-sm">
                        {{ summary.expiringSoonPolicies }}
                    </div>
                </q-card-section>
            </q-card>
        </div>

        <div class="col-12 col-sm-3">
            <q-card flat bordered class="full-height">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Süresi Dolmuş</div>
                    <div class="text-h4 text-weight-bold text-negative q-mt-sm">
                        {{ summary.expiredPolicies }}
                    </div>
                </q-card-section>
            </q-card>
        </div>

        <div class="col-12 col-sm-3">
            <q-card flat bordered class="full-height">
                <q-card-section class="column justify-between full-height">
                    <div class="text-subtitle2 text-grey-7">Toplam Prim</div>
                    <div class="text-h4 text-weight-bold text-positive q-mt-sm">
                        {{ summary.totalPremium }} TL
                    </div>
                </q-card-section>
            </q-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { CustomerSummary } from '@/types/dashboard.types';

defineProps<{ summary: CustomerSummary }>();
</script>