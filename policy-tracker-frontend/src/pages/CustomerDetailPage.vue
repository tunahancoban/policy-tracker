<template>
    <q-page class="q-pa-md fade-in-up">
        <!-- Skeleton Loading -->
        <div v-if="isInitialLoading" class="q-pa-xl">
            <div class="row q-col-gutter-md">
                <div class="col-12 col-md-4">
                    <q-card class="q-pa-md">
                        <div class="skeleton-box skeleton-card" style="height: 200px;" />
                    </q-card>
                </div>
                <div class="col-12 col-md-8">
                    <q-card class="q-pa-md">
                        <div class="skeleton-box skeleton-title" />
                        <div class="skeleton-box skeleton-text" />
                        <div class="skeleton-box skeleton-text" style="width: 70%;" />
                        <div class="skeleton-box skeleton-card q-mt-md" style="height: 120px;" />
                    </q-card>
                </div>
            </div>
        </div>

        <template v-else-if="customer">
            <!-- Breadcrumb -->
            <div class="app-breadcrumb">
                <router-link to="/dashboard">Dashboard</router-link>
                <span class="separator">›</span>
                <router-link to="/customers">Müşteriler</router-link>
                <span class="separator">›</span>
                <span class="current">{{ customer.firstName }} {{ customer.lastName }}</span>
            </div>

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
                    <PolicyTable :policies="policies" :loading="isPoliciesLoading" :rows-number="totalElements"
                        :page="currentPage + 1" :rows-per-page="pageSize" title="Müşteriye Tanımlı Poliçeler"
                        empty-state-text="Bu müşteriye ait henüz bir poliçe kaydı bulunamadı."
                        @edit="openEditPolicyDialog" @add="openCreateDialog" @request="onPolicyTableRequest" />
                </div>
            </div>

            <CustomerModal v-model="showModal" :customer-data="customer" @saved="onCustomerSaved" />
            <NewPolicyModal v-model="isCreateModalOpen" @created="handlePolicyCreate" />
            <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policy-data="selectedPolicy"
                @updated="handlePolicyUpdate" />
        </template>

        <div v-else class="empty-state">
            <div class="empty-state__icon">
                <q-icon name="person_off" size="32px" color="grey-5" />
            </div>
            <div class="empty-state__title">Müşteri bulunamadı</div>
            <div class="empty-state__description">Aradığınız müşteri kaydına erişilemiyor.</div>
            <q-btn outline color="primary" label="Müşteri Listesine Dön" to="/customers" icon="arrow_back" no-caps />
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
import PolicyTable from '@/components/PolicyTable.vue';
import { usePolicyList } from '@/composables/usePolicyList';
import NewPolicyModal from '../components/NewPolicyModal.vue';


const route = useRoute();
const customerId = route.params.id as string;

const {
    customer,
    policies,
    summary,
    isInitialLoading,
    isPoliciesLoading,
    totalElements,
    loadAllData,
    fetchPoliciesOnly,
    updatePolicy,
    currentPage,
    pageSize
} = useCustomerDetail(customerId);

const { createPolicy } = usePolicyList();

const showModal = ref(false);
const isEditModalOpen = ref(false);
const selectedPolicy = ref<Policy | null>(null);
const isCreateModalOpen = ref(false);

const sortByColumn = ref<string | null>();
const sortDescending = ref<boolean>(false);

const openEditPolicyDialog = (policy: Policy) => {
    selectedPolicy.value = policy;
    isEditModalOpen.value = true;
};

const openCreateDialog = () => {
    isCreateModalOpen.value = true;
};


const onCustomerSaved = async () => {
    await loadAllData();
};

const onPolicyTableRequest = async (requestProp: {
    pagination: {
        page: number;
        rowsPerPage: number;
        sortBy: string | null;
        descending: boolean;
    }
}) => {
    const { page, rowsPerPage, sortBy, descending } = requestProp.pagination;

    const targetBackendPage = page - 1;
    currentPage.value = targetBackendPage;
    pageSize.value = rowsPerPage;

    if (sortBy !== undefined) {
        sortByColumn.value = sortBy;
        sortDescending.value = descending;
    }

    await fetchPoliciesOnly(sortByColumn.value, sortDescending.value);
};

const handlePolicyUpdate = async (event: { id: string; data: Partial<Policy> }) => {
    try {
        const payload = formatPolicyPayload(event.data);
        await updatePolicy(event.id, payload);
        Notify.create({ message: 'Poliçe başarıyla güncellendi.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
    } catch (err) {
        Notify.create({ message: 'Poliçe güncellenirken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
        console.error('Policy Update Error:', err);
    }
};
const handlePolicyCreate = async (newPolicy: Omit<Policy, 'policyId'>) => {
    try {
        await createPolicy(newPolicy);
        Notify.create({ message: 'Poliçe başarıyla oluşturuldu.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
        isCreateModalOpen.value = false;
        await loadAllData();
    } catch (err) {
        Notify.create({ message: 'Poliçe oluşturulurken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
        console.error('Policy Create Error:', err);
    }
};

onMounted(() => {
    void loadAllData();
});
</script>