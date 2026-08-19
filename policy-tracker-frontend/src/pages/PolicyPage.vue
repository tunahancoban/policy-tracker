<template>
    <q-page class="q-pa-md fade-in-up">
        <!-- Breadcrumb -->
        <div class="app-breadcrumb">
            <router-link to="/dashboard">Dashboard</router-link>
            <span class="separator">›</span>
            <span class="current">Poliçeler</span>
        </div>

        <q-card flat bordered class="my-card">
            <!-- Kart İçi Başlık ve Aksiyon Butonu -->
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold">Genel Poliçe Yönetimi</div>
                <q-space />
                <q-btn color="primary" icon="add" label="Yeni Poliçe Oluştur" @click="openCreateDialog" />
            </q-card-section>

            <q-separator class="q-mt-md" />

            <!-- Filtreleme Alanı -->
            <q-card-section class="q-pb-none">
                <div class="row q-col-gutter-sm items-center">
                    <div class="col-12 col-md-5">
                        <q-input v-model="searchQuery" outlined dense label="Poliçe No veya Müşteri ID ile Ara"
                            placeholder="Örn: TRF2026... veya CST-000002" clearable @keyup.enter="onSearch"
                            @clear="onSearch">
                            <template v-slot:append>
                                <q-icon name="search" @click="onSearch" class="cursor-pointer" />
                            </template>
                        </q-input>
                    </div>

                    <div class="col-12 col-md-3">
                        <q-select v-model="selectedType" outlined dense :options="policyTypeOptions"
                            label="Poliçe Türü Filtresi" emit-value map-options clearable />
                    </div>

                    <div class="col-12 col-md-2">
                        <q-select v-model="selectedActive" outlined dense :options="activeOptions"
                            label="Aktiflik Durumu" emit-value map-options />
                    </div>

                    <div class="col-12 col-md-2">
                        <q-btn label="Temizle" color="primary" outline @click="resetFilters" />
                    </div>
                </div>
            </q-card-section>

            <!-- Tablo Alanı -->
            <q-card-section>
                <PolicyTable :policies="policies" :loading="isLoading" :rows-number="totalElements"
                    :show-add-button="false" @row-click="goToPolicyDetail" @request="onRequest" class="clickable-table">
                    <template v-slot:row-actions="{ policy }">
                        <q-btn flat round color="primary" icon="account_circle" size="sm"
                            :to="`/customer/${policy.customerId}`" @click.stop />
                        <q-btn flat round color="blue" icon="edit" size="sm" @click.stop="openEditDialog(policy)" />
                        <q-btn flat round color="green" icon="autorenew" size="sm"
                            @click.stop="openRenewDialog(policy)">
                            <q-tooltip>Poliçeyi Yenile</q-tooltip>
                        </q-btn>
                        <q-btn flat round color="red" icon="delete" size="sm"
                            @click.stop="handlePolicyDelete(policy)" />
                    </template>
                </PolicyTable>
            </q-card-section>
        </q-card>

        <!-- Yeni Poliçe Oluşturma ve Yenileme Ortak Modalı -->
        <NewPolicyModal v-model="isCreateModalOpen" :is-renewal="isRenewal" :policy-data="selectedPolicy"
            @created="handlePolicyCreate" @renewed="handlePolicyRenew" />

        <!-- Poliçe Düzenleme Modalı -->
        <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policyData="selectedPolicy"
            @updated="handlePolicyUpdate" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import type { Policy, CreatePolicyRequest, RenewPolicyRequest } from '@/types/policy.types';
import { policyTypeOptions } from '@/types/policy.types';
import { usePolicyList } from '@/composables/usePolicyList';
import { useConfirmDialog } from '@/composables/useConfirmDialog';
import { useAuthStore } from '@/stores/auth';
import { Notify } from 'quasar';
import PolicyTable from '@/components/PolicyTable.vue';
import { useRouter } from 'vue-router';
import NewPolicyModal from '@/components/NewPolicyModal.vue';
import EditPolicyModal from '@/components/EditPolicyModal.vue';
import {
    buildQueryParams,
    resolvePolicySearchParam,
    POLICY_SORT_FIELD_MAP,
} from '@/composables/useQueryBuilder';

const authStore = useAuthStore();
const activeOptions = [
    { value: 'ACTIVE', label: 'Aktif' },
    { value: 'PASSIVE', label: 'Pasif' },
    { value: null, label: 'Tümü' }
];
const {
    policies,
    isLoading,
    totalElements,
    currentPage,
    pageSize,
    loadPolicies,
    createPolicy,
    updatePolicy,
    deletePolicy,
    renewPolicy,
} = usePolicyList();

const router = useRouter();
const searchQuery = ref<string>('');
const selectedType = ref<string | null>(null);
const selectedActive = ref<string | null>('ACTIVE');
const { confirm } = useConfirmDialog();

// Modal State'leri
const isCreateModalOpen = ref<boolean>(false);
const isEditModalOpen = ref<boolean>(false);
const isRenewal = ref<boolean>(false);
const selectedPolicy = ref<Policy | null>(null);

const sortByColumn = ref<string | null>('endDate');
const sortDescending = ref<boolean>(false);

const buildParams = (
    overridePage?: number,
    overrideSize?: number,
    sortBy?: string | null,
    descending?: boolean,
) => {
    const extra: Record<string, string> = {};
    if (!authStore.isAdmin && authStore.id) extra.responsibleUserId = authStore.id;
    if (selectedType.value) extra.type = selectedType.value;
    if (selectedActive.value) extra.active = selectedActive.value;

    return buildQueryParams(
        {
            pageSize: pageSize.value,
            currentPage: currentPage.value,
            sortFieldMap: POLICY_SORT_FIELD_MAP,
            resolveSearchParam: resolvePolicySearchParam,
            extraParams: extra,
        },
        {
            ...(overridePage !== undefined && { page: overridePage }),
            ...(overrideSize !== undefined && { size: overrideSize }),
            search: searchQuery.value,
            sortBy: sortBy ?? sortByColumn.value,
            descending: descending ?? sortDescending.value,
        },
    );
};

const onSearch = () => {
    currentPage.value = 0;
    void loadPolicies(buildParams(0));
};

const resetFilters = () => {
    searchQuery.value = '';
    selectedType.value = null;
    selectedActive.value = 'ACTIVE';
    currentPage.value = 0;
    void loadPolicies(buildParams(0));
};

watch([selectedType, selectedActive], () => {
    onSearch();
});
const onRequest = async (requestProp: {
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

    await loadPolicies(
        buildParams(targetBackendPage, rowsPerPage, sortByColumn.value, sortDescending.value)
    );
};

const openCreateDialog = () => {
    isRenewal.value = false;
    selectedPolicy.value = null;
    isCreateModalOpen.value = true;
};

const openRenewDialog = (policy: Policy) => {
    isRenewal.value = true;
    selectedPolicy.value = policy;
    isCreateModalOpen.value = true;
};

const openEditDialog = (policy: Policy) => {
    selectedPolicy.value = policy;
    isEditModalOpen.value = true;
};

const handlePolicyCreate = async (newPolicyPayload: CreatePolicyRequest) => {
    try {
        await createPolicy(newPolicyPayload);
        Notify.create({ message: 'Poliçe başarıyla oluşturuldu.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
        isCreateModalOpen.value = false;
        void loadPolicies(buildParams());
    } catch (err) {
        Notify.create({ message: 'Poliçe oluşturulurken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
        console.error('Policy Create Error:', err);
    }
};

const handlePolicyRenew = async (renewPayload: RenewPolicyRequest) => {
    try {
        await renewPolicy(renewPayload);
        Notify.create({ message: 'Poliçe başarıyla yenilendi.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
        isCreateModalOpen.value = false;
        void loadPolicies(buildParams());
    } catch (err) {
        Notify.create({ message: 'Poliçe yenilenirken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
        console.error('Policy Renew Error:', err);
    }
};

const handlePolicyUpdate = async (event: { id: string; data: Partial<Policy> }) => {
    try {
        await updatePolicy(event.id, event.data);
        Notify.create({ message: 'Poliçe başarıyla güncellendi.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
        void loadPolicies(buildParams());
    } catch (error) {
        Notify.create({ message: 'Poliçe güncellenirken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
        console.error('Policy Update Error:', error);
        throw error;
    }
};

const handlePolicyDelete = async (policy: Policy) => {
    if (!policy?.policyId) return;

    const isConfirmed = await confirm({
        title: 'Poliçe Silme Onayi',
        message: `${policy.policyId} numaralı poliçeyi silmek istediğinize emin misiniz? Bu işlem geri alınamaz.`,
        okLabel: 'Evet, Sil',
        cancelLabel: 'Vazgeç',
        color: 'negative'
    });

    if (!isConfirmed) return;

    try {
        await deletePolicy(policy.policyId);
        Notify.create({ message: 'Poliçe başarıyla silindi.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
        void loadPolicies(buildParams());
    } catch (err) {
        Notify.create({ message: 'Poliçe silinirken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
        console.error('Policy Delete Error:', err);
    }
};

const goToPolicyDetail = (evt: unknown, row: Policy) => {
    void router.push({ name: 'policy-detail', params: { id: row.policyId } });
};

onMounted(() => {
    void loadPolicies(buildParams());
});
</script>