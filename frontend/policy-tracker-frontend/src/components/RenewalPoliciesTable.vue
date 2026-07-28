<!-- RenewalPoliciesTable.vue -->
<template>
    <q-card flat bordered>
        <q-table flat dense :rows="policies" :columns="renewalColumns" row-key="policyId" :loading="loading"
            :pagination="pagination" no-data-label="Yenilenmesi gereken poliçe bulunmuyor."
            loading-label="Veriler getiriliyor..." style="max-height: 320px;" virtual-scroll>

            <template v-slot:body-cell-type="props">
                <q-td :props="props">
                    <q-chip :color="getPolicyTypeColor(props.row.type)"
                        :text-color="getPolicyTypeTextColor(props.row.type)" dense square
                        class="text-weight-bold text-caption">
                        {{ props.row.type }}
                    </q-chip>
                </q-td>
            </template>

            <template v-slot:body-cell-remainingDays="props">
                <q-td :props="props" class="text-center">
                    <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white" dense
                        class="text-weight-bold">
                        {{ calculateRemainingDays(props.row.endDate) }}
                    </q-chip>
                </q-td>
            </template>

            <template v-slot:body-cell-premium="props">
                <q-td :props="props" class="text-weight-medium text-right text-subtitle2">
                    {{ props.row.premium != null ? `${props.row.premium.toLocaleString('tr-TR')} TL` : '0 TL' }}
                </q-td>
            </template>

            <template v-slot:body-cell-actions="props">
                <q-td :props="props" class="text-center">
                    <q-btn flat round color="primary" icon="account_circle" size="sm"
                        :to="`/customer/${props.row.customerId}`">
                        <q-tooltip>Müşteri Detayına Git</q-tooltip>
                    </q-btn>
                </q-td>
            </template>
        </q-table>
    </q-card>
</template>

<script setup lang="ts">
import type { Policy } from '@/types/policy.types';
import { calculateRemainingDays, getRemainingDaysColor } from '@/utils/dateHelper';
import { getPolicyTypeColor, getPolicyTypeTextColor } from '@/utils/policyHelper';

defineProps<{
    policies: Policy[];
    loading: boolean;
}>();

const pagination = { rowsPerPage: 5 };

const renewalColumns = [
    { name: 'policyId', label: 'Poliçe No', field: 'policyId', align: 'left' as const, sortable: true },
    { name: 'type', label: 'Tür', field: 'type', align: 'center' as const, sortable: true },
    { name: 'customerId', label: 'Müşteri ID', field: 'customerId', align: 'left' as const, sortable: true },
    { name: 'endDate', label: 'Bitiş Tarihi', field: 'endDate', align: 'center' as const, sortable: true },
    { name: 'remainingDays', label: 'Kalan Gün', field: 'endDate', align: 'center' as const, sortable: true },
    { name: 'premium', label: 'Prim', field: 'premium', align: 'right' as const, sortable: true },
    { name: 'actions', label: '', field: 'actions', align: 'center' as const },
];
</script>