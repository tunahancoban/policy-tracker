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
                    no-data-label="Kayıtlı müşteri bulunamadı." loading-label="Veriler yükleniyor..."
                    @row-click="goToCustomerDetail" class="customer-table">

                    <template v-slot:body-cell-actions="props">
                        <q-td :props="props" class="q-gutter-xs" @click.stop>
                            <q-btn flat round dense color="warning" icon="edit" @click="openEditModal(props.row)" />
                            <q-btn flat round dense color="negative" icon="delete" @click="confirmDelete(props.row)" />
                        </q-td>

                    </template>
                </q-table>
            </q-card-section>

        </q-card>
        <CustomerModal v-model="showModal" :customer-data="editingCustomer" @saved="onCustomerSaved" />
    </q-page>
</template>

ts
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import CustomerModal from '../components/CustomerModal.vue';

import type { Customer } from '../types/customer.types';
import { customerColumns } from '../types/customer.types';
import { useCustomerList } from '../composables/useCustomerList';

import { useQuasar } from 'quasar';

const $q = useQuasar();
const { customers, isLoading, loadCustomers, deleteCustomer } = useCustomerList(); const router = useRouter();

const showModal = ref(false);
const editingCustomer = ref<Customer | undefined>(undefined);

const goToCustomerDetail = (evt: unknown, row: Customer) => {
    void router.push({ name: 'customer-detail', params: { id: row.customerId } });
};

onMounted(async () => {
    await loadCustomers();
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
    await loadCustomers();
};

const confirmDelete = (customer: Customer) => {
    if (!customer.customerId) return;

    $q.dialog({
        title: 'Müşteri Silme',
        message: `${customer.firstName} ${customer.lastName} isimli müşteriyi silmek istediğinize emin misiniz?`,
        cancel: { label: 'Vazgeç', flat: true, color: 'grey' },
        ok: { label: 'Evet, Sil', color: 'negative' },
    }).onOk(() => {
        void handleDelete(customer.customerId);
    });
};

const handleDelete = async (customerId: string) => {
    try {
        await deleteCustomer(customerId);
        $q.notify({ message: 'Müşteri başarıyla silindi.', color: 'positive' });
    } catch (err) {
        console.error('Silme esnasında hata oluştu:', err);
        $q.notify({ message: 'Müşteri silinirken bir hata oluştu.', color: 'negative' });
    }
};
</script>