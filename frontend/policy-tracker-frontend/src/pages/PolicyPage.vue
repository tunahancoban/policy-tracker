<template>
    <q-page class="q-pa-md">
        <div class="row items-center justify-between q-mb-md">
            <div class="text-h5 text-weight-bold text-grey-8 =row items-center">
                <q-icon name="description" color="primary" class="q-mr-sm" size="32px" />
                Genel Poliçe Yönetimi
            </div>
            <q-btn color="primary" icon="add" label="Yeni Poliçe Oluştur" @click="openCreateDialog" />
        </div>

        <q-card flat bordered class="q-mb-md">
            <q-card-section class="q-pb-none">
                <div class="text-subtitle2 text-grey-7 q-mb-sm">Esnek Filtreleme Seçenekleri</div>
                <div class="row q-col-gutter-sm items-center">
                    <div class="col-12 col-md-5">
                        <q-input v-model="searchQuery" outlined dense label="Poliçe No veya Müşteri ID ile Ara..."
                            placeholder="Örn: TRF2026... veya CST-000002" clearable @keyup.enter="onSearch">
                            <template v-slot:append>
                                <q-icon name="search" />
                            </template>
                        </q-input>
                    </div>

                    <div class="col-12 col-md-4">
                        <q-select v-model="selectedType" outlined dense :options="policyTypeOptions"
                            label="Poliçe Türü Filtresi" emit-value map-options clearable />
                    </div>

                    <div class="col-12 col-md-3 row q-gutter-x-sm justify-end">
                        <q-btn label="Temizle" color="primary" @click="resetFilters" />
                    </div>
                </div>
            </q-card-section>
        </q-card>

        <q-card flat bordered>
            <q-table flat :rows="policyStore.policies" :columns="policyColumns" row-key="policyId"
                :loading="policyStore.isLoading" no-data-label="Kriterlere uygun poliçe kaydı bulunamadı."
                loading-label="Veriler getiriliyor...">
                <template v-slot:body-cell-type="props">
                    <q-td :props="props">
                        <q-chip :color="props.row.type === 'TRAFİK' ? 'teal-2' : 'purple-2'"
                            :text-color="props.row.type === 'TRAFİK' ? 'teal-9' : 'purple-9'" dense square
                            class="text-weight-bold text-caption">
                            {{ props.row.type }}
                        </q-chip>
                    </q-td>
                </template>

                <template v-slot:body-cell-premium="props">
                    <q-td :props="props" class="text-weight-medium text-right text-subtitle2">
                        {{ props.row.premium != null ? `${props.row.premium} TL` : '0.0 TL' }}
                    </q-td>
                </template>

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
                        <q-btn flat round color="primary" icon="account_circle" size="sm"
                            :to="`/customer/${props.row.customerId}`">
                            <q-tooltip>Müşteri Detayına Git</q-tooltip>
                        </q-btn>
                        <q-btn flat round color="primary" icon="visibility" size="sm">
                            <q-tooltip>Poliçe Detayına Git</q-tooltip>
                        </q-btn>
                        <q-btn flat round color="secondary" icon="edit" size="sm" @click="openEditDialog(props.row)" />
                    </q-td>
                </template>
            </q-table>
        </q-card>

        <!-- YENİ POLİÇE EKLEME MODALI -->
        <NewPolicyModal v-model="isCreateModalOpen" @created="handlePolicyCreate" />

        <!-- POLİÇE DÜZENLEME (PATCH) MODALI -->
        <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policyData="selectedPolicy"
            @updated="handlePolicyUpdate" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import type { Policy, PolicyForm } from '../types/policy.types';
import { policyColumns, policyTypeOptions } from '../types/policy.types';
import { calculateRemainingDays, getRemainingDaysColor } from '../utils/dateHelper';
import { usePolicyStore } from '../stores/policy';
import { Notify } from 'quasar';

import NewPolicyModal from '../components/NewPolicyModal.vue';
import EditPolicyModal from '../components/EditPolicyModal.vue';

const searchQuery = ref<string>('');
const selectedType = ref<string | null>(null);
const policyStore = usePolicyStore();

const isCreateModalOpen = ref<boolean>(false);
const isEditModalOpen = ref<boolean>(false);
const selectedPolicy = ref<Policy | null>(null);

const onSearch = () => {
    const query = searchQuery.value ? searchQuery.value.trim() : '';
    const searchParams: Record<string, string | null> = {
        policyId: null,
        customerId: null,
        type: selectedType.value
    };

    if (query) {
        if (query.toUpperCase().startsWith('CST')) {
            searchParams.customerId = query;
        } else {
            searchParams.policyId = query;
        }
    }

    void policyStore.fetchPolicies(searchParams);
};

const resetFilters = () => {
    searchQuery.value = '';
    selectedType.value = null;
    void policyStore.fetchPolicies({});
};

watch(selectedType, () => {
    onSearch();
});

// Ekleme Modalı Açma
const openCreateDialog = () => {
    isCreateModalOpen.value = true;
};

// Düzenleme Modalı Açma
const openEditDialog = (policy: Policy) => {
    selectedPolicy.value = policy;
    isEditModalOpen.value = true;
};

interface ModalTypePayload {
    type: string | { label: string; value: string };
}

// 1. Yeni Poliçe Ekleme (POST)
const handlePolicyCreate = async (event: { data: Omit<PolicyForm, 'type'> & ModalTypePayload }) => {
    try {
        const payload: PolicyForm = {
            ...event.data,
            type: typeof event.data.type === 'object' ? event.data.type.value : event.data.type,
            // Slasları tireye çevirip zaman damgası ekliyoruz
            startDate: event.data.startDate ? `${event.data.startDate.replace(/\//g, '-')}T00:00:00` : '',
            endDate: event.data.endDate ? `${event.data.endDate.replace(/\//g, '-')}T00:00:00` : ''
        };

        await policyStore.addPolicy(payload);
        Notify.create({ message: 'Poliçe başarıyla oluşturuldu.', color: 'positive', icon: 'check' });
        void policyStore.fetchPolicies({});
    } catch (error) {
        Notify.create({ message: 'Poliçe eklenirken bir hata oluştu.', color: 'negative' });
        console.log(error);
    }
};

// 2. Poliçe Güncelleme (PATCH)
const handlePolicyUpdate = async (event: { id: string; data: Partial<Omit<Policy, 'policyId' | 'type'>> & Partial<ModalTypePayload> }) => {
    try {
        const payload: Partial<Omit<Policy, 'policyId'>> = {};

        if (event.data.customerId) payload.customerId = event.data.customerId;
        if (event.data.premium !== undefined) payload.premium = event.data.premium;

        // Slasları tireye çevirerek ekliyoruz
        if (event.data.startDate) payload.startDate = `${event.data.startDate.replace(/\//g, '-')}T00:00:00`;
        if (event.data.endDate) payload.endDate = `${event.data.endDate.replace(/\//g, '-')}T00:00:00`;

        if (event.data.type) {
            payload.type = typeof event.data.type === 'object' ? event.data.type.value : event.data.type;
        }

        await policyStore.updatePolicy(event.id, payload);
        Notify.create({ message: 'Poliçe başarıyla güncellendi.', color: 'positive', icon: 'check' });
        void policyStore.fetchPolicies({});
    } catch (error) {
        Notify.create({ message: 'Poliçe güncellenirken bir hata oluştu.', color: 'negative' });
        console.log(error);
    }
};
onMounted(() => {
    void policyStore.fetchPolicies({});
});
</script>