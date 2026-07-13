<template>
    <q-page class="q-pa-md">
        <div class="row items-center justify-between q-mb-md">
            <q-btn flat color="primary" icon="arrow_back" label="Müşteri Listesine Dön" to="/customers" />
            <q-btn color="secondary" icon="edit" label="Müşteriyi Düzenle" @click="openEditDialog" />
        </div>
        <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
                <q-card class="my-card" flat bordered>
                    <q-card-section class="bg-primary text-white row items-center q-gap-md q-pa-lg">
                        <q-avatar size="64px" font-size="32px" color="white" text-color="primary" icon="person" />
                        <div class="q-ml-md">
                            <div class="text-h5 text-weight-bold">{{ customer.firstName }} {{ customer.lastName }}</div>
                            <div class="text-caption text-blue-2">Müşteri Detay Profili</div>
                        </div>
                    </q-card-section>

                    <q-separator />

                    <q-card-section class="q-pa-md">
                        <q-list dense>
                            23
                            <q-item class="q-py-sm">
                                <q-item-section avatar><q-icon name="account_box" color="primary" /></q-item-section>
                                <q-item-section>
                                    <q-item-label caption>Müşteri Numarası</q-item-label>
                                    <q-item-label class="text-weight-medium">{{ customer.customerId }}</q-item-label>
                                </q-item-section>
                            </q-item>


                            <q-item class="q-py-sm">
                                <q-item-section avatar><q-icon name="badge" color="primary" /></q-item-section>
                                <q-item-section>
                                    <q-item-label caption>T.C. Kimlik No </q-item-label>
                                    <q-item-label class="text-weight-medium">{{ customer.identityNumber
                                        }}</q-item-label>
                                </q-item-section>
                            </q-item>


                            <q-item class="q-py-sm">
                                <q-item-section avatar><q-icon name="email" color="primary" /></q-item-section>
                                <q-item-section>
                                    <q-item-label caption>E-Posta Adresi</q-item-label>
                                    <q-item-label class="text-weight-medium">{{ customer.email }}</q-item-label>
                                </q-item-section>
                            </q-item>

                            <q-item class="q-py-sm">
                                <q-item-section avatar><q-icon name="phone" color="primary" /></q-item-section>
                                <q-item-section>
                                    <q-item-label caption>Telefon Numarası</q-item-label>
                                    <q-item-label class="text-weight-medium">{{ customer.phoneNumber || 'Belirtilmedi'
                                        }}</q-item-label>
                                </q-item-section>
                            </q-item>

                            <q-item class="q-py-sm">
                                <q-item-section avatar><q-icon name="location_on" color="primary" /></q-item-section>
                                <q-item-section>
                                    <q-item-label caption>Adres Bilgisi</q-item-label>
                                    <q-item-label class="text-weight-medium">
                                        {{ customer.fullAddress }} {{ customer.district }}/{{ customer.city }}
                                    </q-item-label>
                                </q-item-section>
                            </q-item>

                            <q-item class="q-py-sm">
                                <q-item-section avatar><q-icon name="info" color="primary" /></q-item-section>
                                <q-item-section>
                                    <q-item-label caption>Sistem Durumu</q-item-label>
                                    <q-item-label>
                                        <q-chip :color="customer.active ? 'positive' : 'negative'" text-color="white"
                                            dense class="text-weight-bold">
                                            {{ customer.active ? 'AKTİF MÜŞTERİ' : 'PASİF' }}
                                        </q-chip>
                                    </q-item-label>
                                </q-item-section>
                            </q-item>
                        </q-list>
                    </q-card-section>
                </q-card>
            </div>

            <div class="col-12 col-md-8">
                <q-card flat bordered class="full-height">
                    <q-card-section class="row items-center justify-between q-py-md">
                        <div class="text-h6 text-grey-8 row items-center">
                            <q-icon name="description" color="secondary" class="q-mr-sm" size="24px" />
                            Müşteriye Tanımlı Poliçeler
                        </div>
                        <q-btn outline color="secondary" icon="add" label="Yeni Poliçe Ekle" dense class="q-px-sm" />
                    </q-card-section>

                    <q-separator />

                    <q-card-section>
                        <div v-if="policies.length === 0" class="text-center q-pa-xl text-grey-6">
                            <q-icon name="folder_open" size="64px" color="grey-4" />
                            <div class="text-subtitle1 q-mt-md">Bu müşteriye ait henüz bir poliçe kaydı bulunamadı.
                            </div>
                        </div>

                        <div v-else>
                            <q-table flat bordered :rows="policies" :columns="policyColumns" row-key="id"
                                :loading="isPoliciesLoading" no-data-label="Poliçe bulunamadı.">
                                <template v-slot:body-cell-status="props">
                                    <q-td :props="props" class="text-center">
                                        <q-chip :color="props.row.status === 'AKTİF' ? 'positive' : 'warning'"
                                            text-color="white" dense>
                                            {{ props.row.status }}
                                        </q-chip>
                                    </q-td>
                                </template>
                            </q-table>
                        </div>
                    </q-card-section>
                </q-card>
            </div>
        </div>

        <!-- Extracted Modal Component -->
        <CustomerModal v-model="showModal" :customer-data="customer" @saved="onCustomerSaved" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { api } from '../boot/axios';
import { useCustomerStore } from '../stores/customer';
import CustomerModal from '../components/CustomerModal.vue';

// Müşteri Tipi
interface Customer {
    customerId?: string;
    firstName?: string;
    lastName?: string;
    identityNumber?: string;
    email?: string;
    phoneNumber?: string;
    fullAddress?: string;
    district?: string;
    city?: string;
    active?: boolean;
}

// Poliçe Tipi
interface Policy {
    policyId: string;
    type: string;
    startDate: string;
    endDate: string;
    premium: number;
    customerId: string;
}

const route = useRoute();
const customerId = route.params.id as string;

const customerStore = useCustomerStore();

const customer = ref<Customer>({});
const policies = ref<Policy[]>([]);
const isPoliciesLoading = ref<boolean>(false);
const showModal = ref(false);

const policyColumns = [
    { name: 'policyId', label: 'Poliçe No', field: 'policyId', align: 'left' as const, sortable: true },
    { name: 'type', label: 'Poliçe Türü', field: 'type', align: 'left' as const, sortable: true },
    { name: 'startDate', label: 'Başlangıç Tarihi', field: 'startDate', align: 'center' as const },
    { name: 'endDate', label: 'Bitiş Tarihi', field: 'endDate', align: 'center' as const },
    { name: 'premium', label: 'Prim Tutarı', field: (row: Policy) => `${row.premium} TL`, align: 'right' as const },
];

const fetchCustomerDetails = async () => {
    try {
        const response = await api.get('/rest/api/customer/with-params', {
            params: { customerId: customerId }
        });
        if (response.data.success && response.data.data) {
            if (Array.isArray(response.data.data) && response.data.data.length > 0) {
                customer.value = response.data.data[0];
            } else {
                customer.value = response.data.data;
            }
        }
    } catch (error) {
        console.error('Müşteri detayları yüklenirken hata oluştu:', error);
    }
};

const fetchCustomerPolicies = async () => {
    isPoliciesLoading.value = true;
    try {
        console.log('Poliçe isteği tetiklendi, ID:', customerId);

        const response = await api.get('/rest/api/policy/with-params', {
            params: { customerId: customerId }
        });

        if (response.data.success && response.data.data) {
            if (Array.isArray(response.data.data)) {
                policies.value = response.data.data;
            } else {
                policies.value = [response.data.data];
            }
        } else {
            policies.value = [];
        }
    } catch (error) {
        console.error('Poliçeler yüklenirken hata oluştu:', error);
        policies.value = [];
    } finally {
        isPoliciesLoading.value = false;
    }
};

const openEditDialog = () => {
    showModal.value = true;
};

const onCustomerSaved = async () => {
    await fetchCustomerDetails();
    await customerStore.fetchCustomerData();
};

onMounted(() => {
    console.log('Sayfa yüklendi, istekler izole edilerek bağımsız tetikleniyor...');

    void fetchCustomerDetails();
    void fetchCustomerPolicies();
});
</script>