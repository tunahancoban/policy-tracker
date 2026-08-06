<template>
    <q-card flat bordered class="col">
        <q-card-section class="row items-center justify-between q-py-md">
            <div class="text-h6 text-grey-8 row items-center">
                <q-icon name="description" color="secondary" class="q-mr-sm" size="24px" />
                {{ title }}
            </div>
            <q-btn v-if="showAddButton" outline color="secondary" icon="add" :label="addButtonLabel" dense
                class="q-px-sm" @click="emit('add')" />
        </q-card-section>

        <q-separator />

        <q-card-section class="q-pa-none">
            <q-table flat :rows="policies" :columns="policyColumns" row-key="policyId" :loading="loading"
                v-model:pagination="internalPagination" @request="onRequest" @row-click="onRowClick">
                <template v-slot:no-data>
                    <div class="full-width row flex-center text-grey-6 q-pa-xl">
                        <q-icon name="folder_open" size="64px" color="grey-4" />
                        <div class="text-subtitle1 q-mt-md full-width text-center">
                            {{ emptyStateText }}
                        </div>
                    </div>
                </template>

                <template v-slot:body-cell-remainingDays="props">
                    <q-td :props="props" class="text-center">
                        <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white" dense
                            class="text-weight-bold">
                            {{ calculateRemainingDays(props.row.endDate) }}
                        </q-chip>
                    </q-td>
                </template>

                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="q-gutter-xs text-center" @click.stop>
                        <slot name="row-actions" :policy="props.row">
                            <q-btn v-if="showViewAction" flat round color="primary" icon="visibility" size="sm"
                                @click.stop="emit('view', props.row)" />
                            <q-btn v-if="showEditAction" flat round color="secondary" icon="edit" size="sm"
                                @click.stop="emit('edit', props.row)" />
                            <q-btn v-if="showDeleteAction" flat round color="red" icon="delete" size="sm"
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
    'row-click': [evt: Event, row: Policy]; // eklendi
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