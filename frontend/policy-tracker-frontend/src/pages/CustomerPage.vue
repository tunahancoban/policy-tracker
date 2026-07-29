<template>
    <q-page class="q-pa-md">
        <q-card flat bordered class="my-card">
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold">Müşteri Yönetimi</div>
                <q-space />
                <q-btn color="primary" icon="add" label="Yeni Müşteri" @click="openAddModal" />
            </q-card-section>

            <q-separator class="q-mt-md" />

            <q-card-section>
                <q-table :rows="customers" :columns="customerColumns" :loading="isLoading" row-key="customerId"
                    v-model:pagination="pagination" :rows-number="totalElements"
                    no-data-label="Kayıtlı müşteri bulunamadı." loading-label="Veriler yükleniyor..."
                    @row-click="goToCustomerDetail" @request="onTableRequest" class="customer-table">
                    <template v-slot:body-cell-actions="props">
                        <q-td :props="props" class="q-gutter-xs" @click.stop>
                            <q-btn flat round dense color="warning" icon="edit" @click="openEditModal(props.row)" />
                            <q-btn flat round dense color="negative" icon="delete" @click="handleDelete(props.row)" />
                        </q-td>
                    </template>
                </q-table>
            </q-card-section>
        </q-card>

        <CustomerModal v-model="showModal" :customer-data="editingCustomer" @saved="onCustomerSaved" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import CustomerModal from '../components/CustomerModal.vue';
import { useConfirmDialog } from '@/composables/useConfirmDialog';

import type { Customer } from '../types/customer.types';
import { customerColumns } from '../types/customer.types';
import { useCustomerList } from '../composables/useCustomerList';

import { useQuasar } from 'quasar';

const { confirm } = useConfirmDialog();
const $q = useQuasar();
const { customers, isLoading, totalElements, loadCustomers, deleteCustomer } = useCustomerList();
const router = useRouter();

const showModal = ref(false);
const editingCustomer = ref<Customer | undefined>(undefined);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0,
});

const goToCustomerDetail = (evt: unknown, row: Customer) => {
    void router.push({ name: 'customer-detail', params: { id: row.customerId } });
};

const onTableRequest = async (requestProp: { pagination: { page: number; rowsPerPage: number } }) => {
    const { page, rowsPerPage } = requestProp.pagination;

    await loadCustomers({ page: String(page - 1), size: String(rowsPerPage) });

    pagination.value.page = page;
    pagination.value.rowsPerPage = rowsPerPage;
    pagination.value.rowsNumber = totalElements.value;
};

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
        message: `${customer.firstName}, ${customer.lastName} isimli kişiyi silmek istediğinize emin misiniz? Bu işlem geri alınamaz.`,
        okLabel: 'Evet, Sil',
        cancelLabel: 'Vazgeç',
        color: 'negative',
    });

    if (!isConfirmed) return;

    try {
        await deleteCustomer(customer.customerId);
        $q.notify({ message: 'Müşteri başarıyla silindi.', color: 'positive' });
        await onTableRequest({ pagination: pagination.value }); // liste yenilensin
    } catch (err) {
        console.error('Silme esnasında hata oluştu:', err);
        $q.notify({ message: 'Müşteri silinirken bir hata oluştu.', color: 'negative' });
    }
};

onMounted(() => {
    void onTableRequest({ pagination: pagination.value });
});
</script>

<style scoped>
.customer-table :deep(.q-table tbody tr) {
    cursor: pointer;
}
</style>