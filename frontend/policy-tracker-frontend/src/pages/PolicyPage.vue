<template>
    <q-page class="q-pa-md fade-in-up">
        <div class="row items-center justify-between q-mb-md">
            <div class="text-h5 text-weight-bold text-grey-8 row items-center">
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
                        <q-input v-model="searchQuery" outlined dense label="Poliçe No veya Müşteri ID ile Ara"
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

        <!-- POLICY TABLE -->
        <PolicyTable :policies="policies" :loading="isLoading" :rows-number="totalElements"
            title="Genel Poliçe Yönetimi" :show-add-button="false" @row-click="goToPolicyDetail" @request="onRequest">
            <template v-slot:row-actions="{ policy }">
                <q-btn flat round color="primary" icon="account_circle" size="sm" :to="`/customer/${policy.customerId}`"
                    @click.stop />
                <q-btn flat round color="primary" icon="visibility" size="sm" @click.stop />
                <q-btn flat round color="secondary" icon="edit" size="sm" @click.stop="openEditDialog(policy)" />
                <q-btn flat round color="red" icon="delete" size="sm" @click.stop="handlePolicyDelete(policy)" />
            </template>
        </PolicyTable>

        <NewPolicyModal v-model="isCreateModalOpen" @created="handlePolicyCreate" />

        <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policyData="selectedPolicy"
            @updated="handlePolicyUpdate" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import type { Policy } from '@/types/policy.types';
import { policyTypeOptions, SORT_FIELD_MAP } from '@/types/policy.types';
import { usePolicyList } from '@/composables/usePolicyList';
import { useConfirmDialog } from '@/composables/useConfirmDialog';
import { Notify } from 'quasar';
import PolicyTable from '@/components/PolicyTable.vue';
import { useRouter } from 'vue-router';

import NewPolicyModal from '@/components/NewPolicyModal.vue';
import EditPolicyModal from '@/components/EditPolicyModal.vue';

const {
    policies,
    isLoading,
    totalElements,
    currentPage,
    pageSize,
    loadPolicies,
    createPolicy,
    updatePolicy,
    deletePolicy
} = usePolicyList();

const router = useRouter();
const searchQuery = ref<string>('');
const selectedType = ref<string | null>(null);
const { confirm } = useConfirmDialog();

const isCreateModalOpen = ref<boolean>(false);
const isEditModalOpen = ref<boolean>(false);
const selectedPolicy = ref<Policy | null>(null);


const sortByColumn = ref<string | null>('endDate');
const sortDescending = ref<boolean>(false);

const buildQueryParams = (
    overridePage?: number,
    overrideSize?: number,
    sortBy?: string | null,
    descending?: boolean
) => {
    const query = searchQuery.value?.trim() ?? '';

    const params: Record<string, string> = {
        page: String(overridePage ?? currentPage.value),
        size: String(overrideSize ?? pageSize.value),
    };

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

    const effectiveSortBy = sortBy ?? sortByColumn.value;
    const effectiveDescending = descending ?? sortDescending.value;

    if (effectiveSortBy && SORT_FIELD_MAP[effectiveSortBy]) {
        const direction = effectiveDescending ? 'desc' : 'asc';
        params.sort = `${SORT_FIELD_MAP[effectiveSortBy]},${direction}`;
    }

    return params;
};

const onSearch = () => {
    currentPage.value = 0;
    void loadPolicies(buildQueryParams(0));
};

const resetFilters = () => {
    searchQuery.value = '';
    selectedType.value = null;
    currentPage.value = 0;
    void loadPolicies(buildQueryParams(0));
};

watch(selectedType, () => {
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
        buildQueryParams(targetBackendPage, rowsPerPage, sortByColumn.value, sortDescending.value)
    );
};

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
        void loadPolicies(buildQueryParams());
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

const handlePolicyDelete = async (policy: Policy) => {
    if (!policy?.policyId) return;

    const isConfirmed = await confirm({
        title: 'Poliçe Silme Onayı',
        message: `${policy.policyId} numaralı poliçeyi silmek istediğinize emin misiniz? Bu işlem geri alınamaz.`,
        okLabel: 'Evet, Sil',
        cancelLabel: 'Vazgeç',
        color: 'negative'
    });

    if (!isConfirmed) return;

    try {
        await deletePolicy(policy.policyId);
        Notify.create({ message: 'Poliçe başarıyla silindi.', color: 'positive', icon: 'check' });
        void loadPolicies(buildQueryParams());
    } catch (err) {
        Notify.create({ message: 'Poliçe silinirken bir hata oluştu.', color: 'negative' });
        console.error('Policy Delete Error:', err);
    }
};

const goToPolicyDetail = (evt: unknown, row: Policy) => {
    void router.push({ name: 'policy-detail', params: { id: row.policyId } });
};



onMounted(() => {
    void loadPolicies(buildQueryParams());
});
</script>