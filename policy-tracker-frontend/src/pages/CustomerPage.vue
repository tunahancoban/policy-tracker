<template>
    <q-page class="q-pa-md fade-in-up">
        <!-- Breadcrumb -->
        <div class="app-breadcrumb">
            <router-link to="/dashboard">Dashboard</router-link>
            <span class="separator">›</span>
            <span class="current">Müşteriler</span>
        </div>

        <q-card flat bordered class="my-card">
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold">Müşteri Yönetimi</div>
                <q-space />
                <q-btn color="primary" icon="add" label="Yeni Müşteri" @click="openAddModal" />
            </q-card-section>

            <q-separator class="q-mt-md" />

            <q-card-section class="q-pb-none">
                <div class="row q-col-gutter-sm items-center">
                    <div class="col-12 col-md-5">
                        <q-input v-model="searchQuery" outlined dense
                            label="Ad, Soyad, E-posta, Telefon, TC Kimlik No veya Müşteri ID ile Ara."
                            placeholder="Örn: Ahmet Yılmaz, ahmet@mail.com, 05321234567 veya CST-000002" clearable
                            @keyup.enter="onSearch" @clear="onSearch">
                            <template v-slot:append>
                                <q-icon name="search" @click="onSearch" class="cursor-pointer" />
                            </template>
                        </q-input>
                    </div>

                    <div class="col-12 col-md-3">
                        <q-select v-model="selectedActive" outlined dense :options="activeOptions"
                            label="Durum Filtresi" emit-value map-options />
                    </div>

                    <div class="col-12 col-md-3">
                        <q-btn label="Temizle" color="primary" outline @click="resetFilters" />
                    </div>
                </div>
            </q-card-section>

            <q-card-section>
                <q-table :rows="customers" :columns="customerColumns" :loading="isLoading" row-key="customerId"
                    v-model:pagination="pagination" :rows-number="totalElements"
                    no-data-label="Kayıtlı müşteri bulunamadı." loading-label="Veriler yükleniyor."
                    @row-click="goToCustomerDetail" @request="onTableRequest" class="clickable-table">
                    <template v-slot:body-cell-active="props">
                        <q-td :props="props" class="text-center">
                            <q-chip :color="props.row.active ? 'positive' : 'grey-5'" text-color="white" dense
                                class="status-chip">
                                {{ props.row.active ? 'Aktif' : 'Pasif' }}
                            </q-chip>
                        </q-td>
                    </template>
                    <template v-slot:body-cell-actions="props">
                        <q-td :props="props" class="q-gutter-xs" @click.stop>
                            <q-btn flat round dense color="warning" icon="edit"
                                @click.stop="openEditModal(props.row)" />
                            <q-btn flat round dense color="negative" icon="delete"
                                @click.stop="handleDelete(props.row)" />
                        </q-td>
                    </template>
                </q-table>
            </q-card-section>
        </q-card>

        <CustomerModal v-model="showModal" :customer-data="editingCustomer" @saved="onCustomerSaved" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import CustomerModal from '../components/CustomerModal.vue';
import { useConfirmDialog } from '@/composables/useConfirmDialog';

import type { Customer } from '../types/customer.types';
import { customerColumns, CUSTOMER_SORT_FIELD_MAP } from '../types/customer.types';
import { useCustomerList } from '../composables/useCustomerList';

import { useQuasar } from 'quasar';

const { confirm } = useConfirmDialog();
const $q = useQuasar();
const { customers, isLoading, totalElements, loadCustomers, deleteCustomer } = useCustomerList();
const router = useRouter();

const showModal = ref(false);
const editingCustomer = ref<Customer | undefined>(undefined);

const searchQuery = ref<string>('');
const selectedActive = ref<string | null>('true');
const sortByColumn = ref<string | null>(null);
const sortDescending = ref<boolean>(false);

const activeOptions = [
    { label: 'Aktif', value: 'true' },
    { label: 'Pasif', value: 'false' },
    { label: 'Tümü', value: null },
];

const pagination = ref({
    page: 1,
    rowsPerPage: 5,
    rowsNumber: 0,
    sortBy: null as string | null,
    descending: false,
});

const goToCustomerDetail = (evt: unknown, row: Customer) => {
    void router.push({ name: 'customer-detail', params: { id: row.customerId } });
};

const buildQueryParams = (
    overridePage?: number,
    overrideSize?: number,
    sortBy?: string | null,
    descending?: boolean
) => {
    const query = searchQuery.value?.trim() ?? '';

    const params: Record<string, string> = {
        page: String(overridePage ?? (pagination.value.page - 1)),
        size: String(overrideSize ?? pagination.value.rowsPerPage),
    };

    if (selectedActive.value !== null) {
        params.active = selectedActive.value;
    }

    if (query) {
        if (query.toUpperCase().startsWith('CST')) {
            params.customerId = query;
        } else if (query.includes('@')) {
            params.email = query;
        } else if (/^0\d{10}$/.test(query) || /^5\d{9}$/.test(query)) {
            params.phoneNumber = query;
        } else if (/^\d{11}$/.test(query)) {
            params.identityNumber = query;
        } else {
            params.firstName = query;
        }
    }

    const effectiveSortBy = sortBy ?? sortByColumn.value;
    const effectiveDescending = descending ?? sortDescending.value;

    if (effectiveSortBy && CUSTOMER_SORT_FIELD_MAP[effectiveSortBy]) {
        const direction = effectiveDescending ? 'desc' : 'asc';
        params.sort = `${CUSTOMER_SORT_FIELD_MAP[effectiveSortBy]},${direction}`;
    }

    return params;
};

const onTableRequest = async (requestProp: {
    pagination: {
        page: number;
        rowsPerPage: number;
        sortBy?: string | null;
        descending?: boolean;
    }
}) => {
    const { page, rowsPerPage, sortBy, descending } = requestProp.pagination;

    if (sortBy !== undefined) {
        sortByColumn.value = sortBy;
        sortDescending.value = descending ?? false;
    }

    await loadCustomers(buildQueryParams(page - 1, rowsPerPage, sortByColumn.value, sortDescending.value));

    pagination.value.page = page;
    pagination.value.rowsPerPage = rowsPerPage;
    pagination.value.rowsNumber = totalElements.value;
    pagination.value.sortBy = sortByColumn.value;
    pagination.value.descending = sortDescending.value;
};

const onSearch = () => {
    pagination.value.page = 1;
    void onTableRequest({ pagination: pagination.value });
};

const resetFilters = () => {
    searchQuery.value = '';
    selectedActive.value = null;
    pagination.value.page = 1;
    void onTableRequest({ pagination: pagination.value });
};

watch(selectedActive, () => {
    onSearch();
});

const openAddModal = () => {
    editingCustomer.value = undefined;
    showModal.value = true;
};

const openEditModal = (customer: Customer) => {
    editingCustomer.value = customer;
    showModal.value = true;
};

const onCustomerSaved = async () => {
    await onTableRequest({ pagination: pagination.value });
};

const handleDelete = async (customer: Customer) => {
    if (!customer?.customerId) return;

    const isConfirmed = await confirm({
        title: 'Müşteri Silme Onayı',
        message: `${customer.firstName} ${customer.lastName} isimli müşteriyi silmek istediğinize emin misiniz? Bu müşteriye tanımlı poliçeler de etkilenecektir. Bu işlem geri alınamaz.`,
        okLabel: 'Evet, Sil',
        cancelLabel: 'Vazgeç',
        color: 'negative',
    });

    if (!isConfirmed) return;

    try {
        await deleteCustomer(customer.customerId);
        $q.notify({ message: 'Müşteri başarıyla silindi.', color: 'positive', icon: 'check_circle', position: 'top-right', timeout: 4000 });
        await onTableRequest({ pagination: pagination.value });
    } catch (err) {
        console.error('Silme esnasında hata oluştu:', err);
        $q.notify({ message: 'Müşteri silinirken bir hata oluştu.', color: 'negative', icon: 'error', position: 'top-right', timeout: 5000 });
    }
};

onMounted(() => {
    void onTableRequest({ pagination: pagination.value });
});
</script>