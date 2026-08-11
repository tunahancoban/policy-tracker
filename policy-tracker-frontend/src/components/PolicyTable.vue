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
            <!-- responsive-table sınıfı eklendi -->
            <q-table flat :rows="policies" :columns="policyColumns" row-key="policyId" :loading="loading"
                v-model:pagination="internalPagination" class="responsive-table" @request="onRequest"
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

                <template v-slot:body-cell-remainingDays="props">
                    <q-td :props="props" class="text-center">
                        <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white" dense
                            class="text-weight-bold compact-chip">
                            {{ calculateRemainingDays(props.row.endDate) }}
                        </q-chip>
                    </q-td>
                </template>

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

<style scoped>
.responsive-table {
    width: 100%;
    table-layout: fixed;
}

.responsive-table :deep(th),
.responsive-table :deep(td) {
    padding: 6px 4px !important;
    font-size: 0.78rem !important;
    white-space: normal !important;
    word-break: break-word;
}

.responsive-table :deep(th) {
    font-weight: 700;
    line-height: 1.1;
}

.compact-chip {
    font-size: 0.7rem !important;
    height: 20px !important;
    padding: 0 6px !important;
    margin: 0 !important;
}

.action-cells {
    padding: 2px !important;
}

@media (max-width: 599px) {
    .responsive-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }
}
</style>