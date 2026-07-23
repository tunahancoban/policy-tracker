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

        <q-card-section>
            <div v-if="policies.length === 0" class="text-center q-pa-xl text-grey-6">
                <q-icon name="folder_open" size="64px" color="grey-4" />
                <div class="text-subtitle1 q-mt-md">{{ emptyStateText }}</div>
            </div>

            <q-table v-else flat bordered :rows="policies" :columns="policyColumns" row-key="policyId"
                no-data-label="Poliçe bulunamadı.">
                <template v-slot:body-cell-statu="props">
                    <q-td :props="props" class="text-center">
                        <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white" dense
                            class="text-weight-bold">
                            {{ calculateRemainingDays(props.row.endDate) }}
                        </q-chip>
                    </q-td>
                </template>
                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="q-gutter-xs text-center">
                        <q-btn v-if="showViewAction" flat round color="primary" icon="visibility" size="sm"
                            @click="emit('view', props.row)">
                            <q-tooltip>Poliçe Detayına Git</q-tooltip>
                        </q-btn>
                        <q-btn v-if="showEditAction" flat round color="secondary" icon="edit" size="sm"
                            @click="emit('edit', props.row)" />
                    </q-td>
                </template>
            </q-table>
        </q-card-section>
    </q-card>
</template>

<script setup lang="ts">
import { policyColumns, type Policy } from '@/types/policy.types';
import { calculateRemainingDays, getRemainingDaysColor } from '@/utils/dateHelper';

interface Props {
    policies: Policy[];
    title?: string;
    emptyStateText?: string;
    addButtonLabel?: string;
    showAddButton?: boolean;
    showEditAction?: boolean;
    showViewAction?: boolean;
}

withDefaults(defineProps<Props>(), {
    title: 'Poliçeler',
    emptyStateText: 'Henüz bir poliçe kaydı bulunamadı.',
    addButtonLabel: 'Yeni Poliçe Ekle',
    showAddButton: true,
    showEditAction: true,
    showViewAction: true,
});

const emit = defineEmits<{
    edit: [policy: Policy];
    view: [policy: Policy];
    add: [];
}>();
</script>