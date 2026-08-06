<template>
    <div class="row q-col-gutter-md">
        <div class="col-12 col-sm-6 col-md-3">
            <q-card class="my-card">
                <q-card-section>
                    <div class="text-subtitle1">Toplam Müşteri</div>
                    <div class="text-h5">{{ summary.totalCustomers }}</div>
                </q-card-section>
            </q-card>
        </div>
        <div class="col-12 col-sm-6 col-md-3">
            <q-card class="my-card">
                <q-card-section>
                    <div class="text-subtitle1">Aktif Poliçe</div>
                    <div class="text-h5">{{ summary.activePolicyNumber }}</div>
                </q-card-section>
            </q-card>
        </div>
        <div class="col-12 col-sm-6 col-md-3">
            <q-card class="my-card">
                <q-card-section>
                    <div class="text-subtitle1">Yakında Sona Erecek Poliçeler</div>
                    <div class="text-h5">{{ summary.expiringSoonPolicies }}</div>
                </q-card-section>
            </q-card>
        </div>
        <div class="col-12 col-sm-6 col-md-3">
            <q-card class="my-card">
                <q-card-section>
                    <div class="text-subtitle1">Süresi Dolmuş Poliçeler</div>
                    <div class="text-h5">{{ summary.expiredPolicies }}</div>
                </q-card-section>
            </q-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { DashboardSummary } from '@/types/dashboard.types';

defineProps<{ summary: DashboardSummary }>();
</script>