<template>
    <q-card flat bordered class="col responsive-card">
        <q-card-section class="row items-center justify-between q-py-md responsive-header">
            <div class="text-h6 text-grey-8 row items-center text-weight-bold">
                <q-icon name="description" color="secondary" class="q-mr-sm" size="24px" />
                {{ title }}
            </div>
            <q-btn v-if="showAddButton" outline color="secondary" icon="add" :label="addButtonLabel" dense
                class="q-px-sm" @click="emit('add')" />
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pa-none">
            <q-table flat :rows="enrichedPolicies" :columns="policyColumns" row-key="policyId" :loading="loading"
                v-model:pagination="internalPagination" class="responsive-table clickable-table" @request="onRequest"
                @row-click="onRowClick">

                <template v-slot:no-data>
                    <div class="empty-state">
                        <div class="empty-state__icon">
                            <q-icon name="folder_open" size="32px" color="grey-5" />
                        </div>
                        <div class="empty-state__title">Kayıt bulunamadı</div>
                        <div class="empty-state__description">
                            {{ emptyStateText }}
                        </div>
                        <q-btn v-if="showAddButton" outline color="primary" :label="addButtonLabel" icon="add" no-caps
                            @click="emit('add')" />
                    </div>
                </template>

                <!-- 1. customer name (Doğrudan formatlanmış metin basılıyor) -->
                <template v-slot:body-cell-customerId="props">
                    <q-td :props="props">
                        <div class="row items-center no-wrap">
                            <span class="text-weight-medium text-grey-9">
                                {{ props.row.customerFullName }}
                            </span>
                        </div>
                    </q-td>
                </template>

                <!-- Kalan Gün Rozeti -->
                <template v-slot:body-cell-remainingDays="props">
                    <q-td :props="props" class="text-center">
                        <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white" dense
                            class="text-weight-bold compact-chip">
                            {{ calculateRemainingDays(props.row.endDate) }}
                        </q-chip>
                    </q-td>
                </template>

                <!-- Aksiyon Butonları -->
                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="q-gutter-xs text-center action-cells" @click.stop>
                        <slot name="row-actions" :policy="props.row">
                            <q-btn v-if="showViewAction" flat round color="primary" icon="visibility" size="xs"
                                @click.stop="emit('view', props.row)" />
                            <q-btn v-if="showEditAction" flat round color="secondary" icon="edit" size="xs"
                                @click.stop="emit('edit', props.row)" />
                            <q-btn v-if="showDeleteAction" flat round color="red" icon="delete" size="xs"
                                @click.stop="emit('delete', props.row)" />
                        </slot>
                    </q-td>
                </template>
            </q-table>
        </q-card-section>
    </q-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { policyColumns, type Policy } from '@/types/policy.types';
import { calculateRemainingDays, getRemainingDaysColor } from '@/utils/dateHelper';
import { useCustomerStore } from '@/stores/customer';

// Müşteri tam adı alanını satıra ekleyen genişletilmiş tip
export type EnrichedPolicy = Policy & { customerFullName: string };

interface Props {
    policies: Policy[];
    loading?: boolean;
    rowsNumber?: number;
    title?: string;
    emptyStateText?: string;
    addButtonLabel?: string;
    showAddButton?: boolean;
    showEditAction?: boolean;
    showViewAction?: boolean;
    showDeleteAction?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    loading: false,
    rowsNumber: 0,
    title: 'Poliçeler',
    emptyStateText: 'Henüz bir poliçe kaydı bulunamadı.',
    addButtonLabel: 'Yeni Poliçe Ekle',
    showAddButton: true,
    showEditAction: true,
    showViewAction: true,
    showDeleteAction: true,
});

const emit = defineEmits<{
    edit: [policy: Policy];
    view: [policy: Policy];
    delete: [policy: Policy];
    add: [];
    request: [payload: {
        pagination: {
            page: number;
            rowsPerPage: number;
            sortBy: string | null;
            descending: boolean;
        }
    }];
    'row-click': [evt: Event, row: Policy];
}>();

const customerStore = useCustomerStore();

// Zenginleştirilmiş satır listesi
const enrichedPolicies = ref<EnrichedPolicy[]>([]);

watch(
    () => props.policies,
    async (newPolicies) => {
        if (!newPolicies || newPolicies.length === 0) {
            enrichedPolicies.value = [];
            return;
        }

        enrichedPolicies.value = await Promise.all(
            newPolicies.map(async (policy) => {
                let customerFullName = policy.customerId;

                if (policy.customerId) {
                    const customer = await customerStore.getCustomerById(policy.customerId);
                    if (customer) {
                        customerFullName =
                            `${customer.firstName || ''} ${customer.lastName || ''}`.trim() ||
                            customer.email ||
                            'İsimsiz Müşteri';
                    }
                }

                return {
                    ...policy,
                    customerFullName,
                };
            })
        );
    },
    { immediate: true, deep: true }
);

const onRowClick = (evt: Event, row: Policy) => {
    emit('row-click', evt, row);
};

const internalPagination = ref({
    page: 1,
    rowsPerPage: 5,
    rowsNumber: props.rowsNumber,
    sortBy: null as string | null,
    descending: false,
});

watch(() => props.rowsNumber, (newVal) => {
    internalPagination.value.rowsNumber = newVal;
}, { immediate: true });

const onRequest = (requestProp: {
    pagination: {
        page: number;
        rowsPerPage: number;
        sortBy: string | null;
        descending: boolean;
    }
}) => {
    internalPagination.value = {
        ...internalPagination.value,
        page: requestProp.pagination.page,
        rowsPerPage: requestProp.pagination.rowsPerPage,
        sortBy: requestProp.pagination.sortBy,
        descending: requestProp.pagination.descending,
    };
    emit('request', requestProp);
};
</script>