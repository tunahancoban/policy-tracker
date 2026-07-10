<template>
    <q-layout view="lHh Lpr lFf">
        <q-page-container>

            <q-page class="q-pa-md">
                <q-card flat bordered class="my-card">
                    <q-card-section class="row items-center q-pb-none">
                        <div class="text-h6">Müşteri Yönetimi</div>
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
                        <q-table :rows="customerStore.customerData" :columns="columns" row-key="customerID"
                            no-data-label="Kayıtlı müşteri bulunamadı." loading-label="Veriler yükleniyor...">

                            <template v-slot:body-cell-active="props">
                                <q-td :props="props" class="text-center">
                                    <q-toggle v-model="props.row.active" checked-icon="check" unchecked-icon="clear"
                                        color="green"
                                        @update:model-value="(val) => toggleActiveStatus(props.row, val)" />
                                </q-td>
                            </template>

                            <template v-slot:body-cell-actions="props">
                                <q-td :props="props" class="q-gutter-xs">
                                    <q-btn flat round dense color="warning" icon="edit"
                                        @click="openEditModal(props.row)" />
                                    <q-btn flat round dense color="negative" icon="delete"
                                        @click="confirmDelete(props.row)" />
                                </q-td>
                            </template>
                        </q-table>
                    </q-card-section>
                    <q-dialog v-model="showModal" persistent>
                        <q-card style="min-width: 450px;">
                            <q-card-section class="row items-center q-pb-none">
                                <div class="text-h6">{{ isEditMode ? 'Müşteri Güncelle' : 'Yeni Müşteri Ekle' }}</div>
                                <q-space />
                                <q-btn icon="close" flat round dense v-close-popup />
                            </q-card-section>

                            <q-card-section class="q-gutter-sm">
                                <q-input v-model="form.firstName" label="Ad *" outlined dense
                                    :rules="[val => !!val || 'Ad zorunludur']" />
                                <q-input v-model="form.lastName" label="Soyad *" outlined dense
                                    :rules="[val => !!val || 'Soyad zorunludur']" />
                                <q-input v-model="form.identityNumber" label="T.C. / Vergi No *" outlined dense
                                    :rules="[val => !!val || 'T.C. zorunludur']" />
                                <q-input v-model="form.email" label="E-posta" outlined dense type="email" />
                                <q-input v-model="form.phoneNumber" label="Telefon" outlined dense
                                    mask="(###) ### ## ##" unmasked-value />
                                <q-input v-model="form.city" label="Şehir" outlined dense />
                                <q-input v-model="form.district" label="İlçe" outlined dense />
                                <q-input v-model="form.fullAddress" label="Adres" outlined dense type="textarea"
                                    rows="2" />
                            </q-card-section>

                            <q-card-actions align="right" class="text-primary q-pt-none">
                                <q-btn flat label="Vazgeç" v-close-popup color="grey" />
                                <q-btn flat :label="isEditMode ? 'Güncelle' : 'Kaydet'" color="primary"
                                    @click="saveCustomer" :loading="customerStore.isLoading" />
                            </q-card-actions>
                        </q-card>
                    </q-dialog>
                </q-card>
            </q-page>

        </q-page-container>
    </q-layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { useQuasar } from 'quasar'; // 🌟 Dialog ve Notify yerine en güvenli yol

interface LocalCustomerData {
    customerID: string;
    firstName: string;
    lastName: string;
    identityNumber: string;
    email: string;
    phoneNumber: string;
    city: string;
    district: string;
    address: string;
    createdAt: string;
    updatedAt: string;
    active: boolean;
}

const customerStore = useCustomerStore();
const $q = useQuasar(); // 🌟 Quasar instance'ını başlattık
const searchQuery = ref<string>('');

const columns = [
    { name: 'customerID', label: 'Müşteri ID', field: 'customerID', align: 'left' as const, sortable: true },
    { name: 'fullName', label: 'Ad Soyad', field: (row: LocalCustomerData) => `${row.firstName} ${row.lastName}`, align: 'left' as const, sortable: true },
    { name: 'identityNumber', label: 'T.C. / Vergi No', field: 'identityNumber', align: 'left' as const },
    { name: 'email', label: 'E-posta', field: 'email', align: 'left' as const },
    { name: 'phoneNumber', label: 'Telefon', field: 'phoneNumber', align: 'left' as const },
    { name: 'city', label: 'Şehir', field: 'city', align: 'left' as const, sortable: true },
    { name: 'active', label: 'Durum', field: 'active', align: 'center' as const }, // 🌟 Yeni Eklenen Kolon
    { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const }
];

onMounted(async () => {
    await customerStore.fetchCustomerData();
});

const onSearch = async () => {
    await customerStore.fetchCustomerData();
};

const showModal = ref(false);
const isEditMode = ref(false);

// Boş form şablonu
const initialForm = {
    customerID: '',
    firstName: '',
    lastName: '',
    identityNumber: '',
    email: '',
    phoneNumber: '',
    city: '',
    district: '',
    fullAddress: '',
    active: true,
    createdAt: '',
    updatedAt: ''
};

const form = ref({ ...initialForm });

const openAddModal = () => {
    isEditMode.value = false;
    form.value = { ...initialForm };
    showModal.value = true;
};

const openEditModal = (customer: unknown) => {
    isEditMode.value = true;
    form.value = { ...(customer as typeof form.value) };
    showModal.value = true;
};

const saveCustomer = async () => {
    if (!form.value.firstName || !form.value.lastName || !form.value.identityNumber) {
        $q.notify({ message: 'Lütfen zorunlu alanları doldurun.', color: 'warning' });
        return;
    }

    try {
        if (isEditMode.value) {
            await customerStore.updateCustomer(form.value);
            $q.notify({ message: 'Müşteri başarıyla güncellendi.', color: 'positive' });
        } else {
            await customerStore.addCustomer(form.value);
            $q.notify({ message: 'Müşteri başarıyla eklendi.', color: 'positive' });
        }

        showModal.value = false;
        await customerStore.fetchCustomerData();

    } catch (error) {
        console.error('Müşteri güncelleme/kayıt hatası:', error);
        const serverMessage = 'İşlem sırasında bir hata oluştu';
        $q.notify({ message: serverMessage, color: 'negative' });
    }
};

const toggleActiveStatus = async (customer: LocalCustomerData, newStatus: boolean) => {
    try {
        await customerStore.updateCustomer({
            ...customer,
            fullAddress: customer.address,
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
    if (!customer.customerID) return;

    $q.dialog({
        title: 'Müşteri Silme',
        message: `${customer.firstName} ${customer.lastName} isimli müşteriyi silmek istediğinize emin misiniz?`,
        cancel: { label: 'Vazgeç', flat: true, color: 'grey' },
        ok: { label: 'Evet, Sil', color: 'negative' },
        persistent: true
    }).onOk(() => {
        const executeDelete = async () => {
            try {
                await customerStore.deleteCustomer(customer.customerID);
                $q.notify({ message: 'Müşteri başarıyla silindi.', color: 'positive' });
                await customerStore.fetchCustomerData();
            } catch (err) {
                console.error('Silme esnasında hata oluştu:', err);
                const serverMessage = 'Müşteri silinirken bir hata oluştu.';
                $q.notify({ message: serverMessage, color: 'negative' });
            }
        };

        void executeDelete();
    });
};
</script>