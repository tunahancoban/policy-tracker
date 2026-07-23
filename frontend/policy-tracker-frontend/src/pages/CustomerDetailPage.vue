<template>
    <q-page class="q-pa-md">
        <div v-if="isLoading" class="row justify-center items-center q-pa-xl">
            <q-spinner-dots color="primary" size="60px" />
        </div>

        <template v-else-if="customer">
            <div class="row items-center justify-between q-mb-md">
                <q-btn flat color="primary" icon="arrow_back" label="Müşteri Listesine Dön" to="/customers" />
                <q-btn color="secondary" icon="edit" label="Müşteriyi Düzenle" @click="showModal = true" />
            </div>

            <div class="row q-col-gutter-md">
                <div class="col-12 col-md-4">
                    <CustomerProfileCard :customer="customer" />
                </div>

                <div class="col-12 col-md-8 column q-gutter-y-md">
                    <PolicySummaryCards :summary="summary" />
                    <CustomerPolicyTable :policies="policies" title="Müşteriye Tanımlı Poliçeler"
                        empty-state-text="Bu müşteriye ait henüz bir poliçe kaydı bulunamadı."
                        @edit="openEditPolicyDialog" @add="onAddPolicyRequested" />
                </div>
            </div>

            <CustomerModal v-model="showModal" :customer-data="customer" @saved="onCustomerSaved" />
            <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policy-data="selectedPolicy"
                @updated="handlePolicyUpdate" />
        </template>

        <div v-else class="text-center q-pa-xl text-grey-6">
            <q-icon name="person_off" size="64px" color="grey-4" />
            <div class="text-subtitle1 q-mt-md">Müşteri bulunamadı.</div>
        </div>
    </q-page>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Notify } from 'quasar';

import { useCustomerDetail } from '@/composables/useCustomerDetail';

import type { Policy } from '@/types/policy.types';
import { formatPolicyPayload } from '@/utils/policyHelper';

import CustomerModal from '@/components/CustomerModal.vue';
import EditPolicyModal from '@/components/EditPolicyModal.vue';
import CustomerProfileCard from '@/components/CustomerProfileCard.vue';
import PolicySummaryCards from '@/components/PolicySummaryCard.vue';
import CustomerPolicyTable from '@/components/CustomerPolicyTable.vue';

const route = useRoute();
const customerId = route.params.id as string;

// Composable artık store state'ini expose ediyor (single source of truth)
const { customer, policies, summary, isLoading, loadAllData, updatePolicy } = useCustomerDetail(customerId);
// Poliçe güncelleme işlemi için store'a ihtiyaç var — composable bunu expose etmiyor

const showModal = ref(false);
const isEditModalOpen = ref(false);
const selectedPolicy = ref<Policy | null>(null);

const openEditPolicyDialog = (policy: Policy) => {
    selectedPolicy.value = policy;
    isEditModalOpen.value = true;
};

const onAddPolicyRequested = () => {
    // TODO: yeni poliçe ekleme akışı
};

const onCustomerSaved = async () => {
    await loadAllData();
};

const handlePolicyUpdate = async (event: { id: string; data: Partial<Policy> }) => {
    try {
        const payload = formatPolicyPayload(event.data);
        await updatePolicy(event.id, payload);
        Notify.create({ message: 'Poliçe başarıyla güncellendi.', color: 'positive' });
        await loadAllData();
    } catch (err) {
        Notify.create({ message: 'Poliçe güncellenirken bir hata oluştu.', color: 'negative' });
        console.error('Policy Update Error:', err);
    }
};

onMounted(() => {
    void loadAllData();
});
</script>