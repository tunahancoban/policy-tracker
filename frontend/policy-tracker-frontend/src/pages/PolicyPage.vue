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

        <PolicyTable :policies="policies" :loading="isLoading" title="Genel Poliçe Yönetimi" :show-add-button="false">
            <template v-slot:row-actions="{ policy }">
                <q-btn flat round color="primary" icon="account_circle" size="sm"
                    :to="`/customer/${policy.customerId}`" />
                <q-btn flat round color="primary" icon="visibility" size="sm" />
                <q-btn flat round color="secondary" icon="edit" size="sm" @click="openEditDialog(policy)" />
            </template>
        </PolicyTable>

        <!-- YENİ POLİÇE EKLEME MODALI -->
        <NewPolicyModal v-model="isCreateModalOpen" @created="handlePolicyCreate" />

        <!-- POLİÇE DÜZENLEME (PATCH) MODALI -->
        <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policyData="selectedPolicy"
            @updated="handlePolicyUpdate" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import type { Policy } from '../types/policy.types';
import { policyTypeOptions } from '../types/policy.types';
import { usePolicyList } from '@/composables/usePolicyList';
import { Notify } from 'quasar';
import PolicyTable from '@/components/PolicyTable.vue';

import NewPolicyModal from '../components/NewPolicyModal.vue';
import EditPolicyModal from '../components/EditPolicyModal.vue';

const { policies, isLoading, loadPolicies, createPolicy, updatePolicy } = usePolicyList();

const searchQuery = ref<string>('');
const selectedType = ref<string | null>(null);

const isCreateModalOpen = ref<boolean>(false);
const isEditModalOpen = ref<boolean>(false);
const selectedPolicy = ref<Policy | null>(null);

const onSearch = () => {
    const query = searchQuery.value?.trim() ?? '';
    const params: Record<string, string> = {};

    if (selectedType.value) {
        params.type = selectedType.value;
    }
    if (query) {
        if (query.toUpperCase().startsWith('CST')) {
            params.customerId = query;
        } else {
            params.policyId = query;
        }
    }

    void loadPolicies(params);
};

const resetFilters = () => {
    searchQuery.value = '';
    selectedType.value = null;
    void loadPolicies({});
};

watch(selectedType, () => {
    onSearch();
});

const openCreateDialog = () => {
    isCreateModalOpen.value = true;
};

const openEditDialog = (policy: Policy) => {
    selectedPolicy.value = policy;
    isEditModalOpen.value = true;
};

const handlePolicyCreate = async (newPolicy: Omit<Policy, 'policyId'>) => {
    try {
        await createPolicy(newPolicy);
        Notify.create({ message: 'Poliçe başarıyla oluşturuldu.', color: 'positive' });
        isCreateModalOpen.value = false;
    } catch (err) {
        Notify.create({ message: 'Poliçe oluşturulurken bir hata oluştu.', color: 'negative' });
        console.error('Policy Create Error:', err);
    }
};

const handlePolicyUpdate = async (event: { id: string; data: Partial<Policy> }) => {
    try {
        await updatePolicy(event.id, event.data);
        Notify.create({ message: 'Poliçe başarıyla güncellendi.', color: 'positive' });
    } catch (err) {
        Notify.create({ message: 'Poliçe güncellenirken bir hata oluştu.', color: 'negative' });
        console.error('Policy Update Error:', err);
    }
};

onMounted(() => {
    void loadPolicies({});
});
</script>