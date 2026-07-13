<template>
    <q-page class="q-pa-md">
        <q-card flat bordered class="my-card">
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold">Müşteri Yönetimi</div>
                <q-space />

                <q-input v-model="searchQuery" dense outlined placeholder="Müşteri Ara..." class="q-mr-sm"
                    style="width: 250px;" @update:model-value="onSearch">
                    <template v-slot:append>
                        <q-icon name="search" />
                    </template>
                </q-input>

                <q-btn color="primary" icon="add" label="Yeni Müşteri" @click="openAddModal" />
            </q-card-section>

            <q-separator class="q-mt-md" />

            <q-card-section>
                <q-table :rows="customerStore.customerData" :columns="customerColumns" row-key="customerId"
                    no-data-label="Kayıtlı müşteri bulunamadı." loading-label="Veriler yükleniyor..."
                    @row-click="goToCustomerDetail" class="customer-table">
                    <template v-slot:body-cell-active="props">
                        <q-td :props="props" class="text-center" @click.stop> <q-toggle v-model="props.row.active"
                                checked-icon="check" unchecked-icon="clear" color="green"
                                @update:model-value="(val) => toggleActiveStatus(props.row, val)" />
                        </q-td>
                    </template>

                    <template v-slot:body-cell-actions="props">
                        <q-td :props="props" class="q-gutter-xs" @click.stop> <q-btn flat round dense color="warning"
                                icon="edit" @click="openEditModal(props.row)" />
                            <q-btn flat round dense color="negative" icon="delete" @click="confirmDelete(props.row)" />
                        </q-td>
                    </template>
                </q-table>
            </q-card-section>
        </q-card>

        <!-- Extracted Modal Component -->
        <CustomerModal v-model="showModal" :customer-data="editingCustomer" @saved="onCustomerSaved" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { useQuasar } from 'quasar';
import { useRouter } from 'vue-router'; // Yönlendirme için router'ı import ettik
import CustomerModal from '../components/CustomerModal.vue';

// ── Extracted modules ──────────────────────────────────────────────
import type { LocalCustomerData } from '../types/customer.types';
import { customerColumns } from '../types/customer.types';
import { useCustomerSearch } from '../composables/useCustomerSearch';

// ── Core dependencies ──────────────────────────────────────────────
const customerStore = useCustomerStore();
const $q = useQuasar();
const router = useRouter(); // Router nesnesini oluşturduk
const searchQuery = ref<string>('');

const showModal = ref(false);
const editingCustomer = ref<LocalCustomerData | undefined>(undefined);

// ── Search composable ──────────────────────────────────────────────
const { onSearch } = useCustomerSearch(searchQuery, customerStore);

// Satıra tıklandığında detay sayfasına uçuran fonksiyon
const goToCustomerDetail = (evt: unknown, row: LocalCustomerData) => {
    // routes.ts dosyasındaki name: 'customer-detail' ile eşleşiyor
    void router.push({ name: 'customer-detail', params: { id: row.customerId } });
};

onMounted(async () => {
    await customerStore.fetchCustomerData();
});

const openAddModal = () => {
    editingCustomer.value = undefined;
    showModal.value = true;
};

const openEditModal = (customer: unknown) => {
    editingCustomer.value = customer as LocalCustomerData;
    showModal.value = true;
};

const onCustomerSaved = async () => {
    await customerStore.fetchCustomerData();
};

const toggleActiveStatus = async (customer: LocalCustomerData, newStatus: boolean) => {
    try {
        await customerStore.updateCustomer({
            ...customer,
            active: newStatus
        });

        $q.notify({
            message: `Müşteri ${newStatus ? 'aktif' : 'pasif'} duruma getirildi.`,
            color: 'positive',
            timeout: 2000
        });
    } catch (error) {
        console.error('Aktiflik durumu değiştirilemedi:', error);
        customer.active = !newStatus;
        $q.notify({ message: 'Durum güncellenirken bir hata oluştu.', color: 'negative' });
    }
};

const confirmDelete = (customer: LocalCustomerData) => {
    if (!customer.customerId) return;

    $q.dialog({
        title: 'Müşteri Silme',
        message: `${customer.firstName} ${customer.lastName} isimli müşteriyi silmek istediğinize emin misiniz?`,
        cancel: { label: 'Vazgeç', flat: true, color: 'grey' },
        ok: { label: 'Evet, Sil', color: 'negative' },
        persistent: true
    }).onOk(() => {
        const executeDelete = async () => {
            try {
                await customerStore.deleteCustomer(customer.customerId);
                $q.notify({ message: 'Müşteri başarıyla silindi.', color: 'positive' });
                await customerStore.fetchCustomerData();
            } catch (err) {
                console.error('Silme esnasında hata oluştu:', err);
                $q.notify({ message: 'Müşteri silinirken bir hata oluştu.', color: 'negative' });
            }
        };

        void executeDelete();
    });
};
</script>

<style scoped>
/* Tablo satırlarının üzerine gelindiğinde tıklanabilir olduğunu belli etmek için el işareti koyuyoruz */
.customer-table :deep(.q-table tbody tr) {
    cursor: pointer;
}
</style>