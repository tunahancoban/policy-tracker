<template>
    <q-page class="q-pa-md">
        <!-- Üst Butonlar -->
        <div class="row items-center justify-between q-mb-md">
            <q-btn flat color="primary" icon="arrow_back" label="Müşteri Listesine Dön" to="/customers" />
            <q-btn color="secondary" icon="edit" label="Müşteriyi Düzenle" @click="openEditDialog" />
        </div>

        <!-- Ana Izgara (Grid) -->
        <div class="row q-col-gutter-md">

            <!-- Sol Kolon: Müşteri Profili (4 Birim) -->
            <div class="col-12 col-md-4">
                <q-card class="my-card full-height" flat bordered>
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
                                    <q-item-label caption>T.C. Kimlik No</q-item-label>
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
                                            {{ customer.active ? 'AKTİF' : 'PASİF' }}
                                        </q-chip>
                                    </q-item-label>
                                </q-item-section>
                            </q-item>
                        </q-list>
                    </q-card-section>
                </q-card>
            </div>

            <!-- Sağ Kolon: Özet Kartlar + Tablo (8 Birim) -->
            <div class="col-12 col-md-8 column q-gutter-y-md">

                <!-- 1. Özet Kartlar -->
                <div class="row q-col-gutter-sm">
                    <div class="col-12 col-sm-3">
                        <q-card flat bordered class="full-height">
                            <q-card-section class="column justify-between full-height">
                                <div class="text-subtitle2 text-grey-7">Aktif Poliçe</div>
                                <div class="text-h4 text-weight-bold text-primary q-mt-sm">{{ summary.activePolicyNumber
                                    }}</div>
                            </q-card-section>
                        </q-card>
                    </div>

                    <div class="col-12 col-sm-3">
                        <q-card flat bordered class="full-height">
                            <q-card-section class="column justify-between full-height">
                                <div class="text-subtitle2 text-grey-7">Yakında Sona Erecek</div>
                                <div class="text-h4 text-weight-bold text-warning q-mt-sm">{{
                                    summary.expiringSoonPolicies }}</div>
                            </q-card-section>
                        </q-card>
                    </div>

                    <div class="col-12 col-sm-3">
                        <q-card flat bordered class="full-height">
                            <q-card-section class="column justify-between full-height">
                                <div class="text-subtitle2 text-grey-7">Süresi Dolmuş</div>
                                <div class="text-h4 text-weight-bold text-negative q-mt-sm">{{ summary.expiredPolicies
                                    }}</div>
                            </q-card-section>
                        </q-card>
                    </div>

                    <div class="col-12 col-sm-3">
                        <q-card flat bordered class="full-height">
                            <q-card-section class="column justify-between full-height">
                                <div class="text-subtitle2 text-grey-7">Toplam Prim</div>
                                <div class="text-h4 text-weight-bold text-positive q-mt-sm">{{ summary.totalPremium
                                    }} TL</div>
                            </q-card-section>
                        </q-card>
                    </div>
                </div>


                <!-- 2. Poliçe Tablosu -->
                <q-card flat bordered class="col">
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
                            <q-table flat bordered :rows="policies" :columns="policyColumns" row-key="policyId"
                                :loading="isPoliciesLoading" no-data-label="Poliçe bulunamadı.">
                                <template v-slot:body-cell-statu="props">
                                    <q-td :props="props" class="text-center">
                                        <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white"
                                            dense class="text-weight-bold">
                                            {{ calculateRemainingDays(props.row.endDate) }}
                                        </q-chip>
                                    </q-td>
                                </template>
                                <template v-slot:body-cell-actions="props">
                                    <q-td :props="props" class="q-gutter-xs text-center">
                                        <q-btn flat round color="primary" icon="visibility" size="sm">
                                            <q-tooltip>Poliçe Detayına Git</q-tooltip>
                                        </q-btn>
                                        <q-btn flat round color="secondary" icon="edit" size="sm"
                                            @click="openEditPolicyDialog(props.row)" />
                                    </q-td>
                                </template>
                            </q-table>
                        </div>

                    </q-card-section>
                </q-card>

            </div>
        </div>

        <!-- Modal -->
        <CustomerModal v-model="showModal" :customer-data="customer" @saved="onCustomerSaved" />
        <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policyData="selectedPolicy"
            @updated="handlePolicyUpdate" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { api } from '../boot/axios';
import { useCustomerStore } from '../stores/customer';
import CustomerModal from '../components/CustomerModal.vue';
import type { Customer } from '../types/customer.types';
import type { Policy } from '../types/policy.types';
import { policyColumns } from '../types/policy.types';
import { calculateRemainingDays, getRemainingDaysColor } from '../utils/dateHelper'
import { Notify } from 'quasar';
import EditPolicyModal from '@/components/EditPolicyModal.vue';
import { usePolicyStore } from '@/stores/policy';

const route = useRoute();
const customerId = route.params.id as string;

const customerStore = useCustomerStore();
const policyStore = usePolicyStore();

const customer = ref<Partial<Customer>>({});
const policies = ref<Policy[]>([]);
const isPoliciesLoading = ref<boolean>(false);
const showModal = ref(false);
const isEditModalOpen = ref<boolean>(false);
const selectedPolicy = ref<Policy | null>(null);

interface PolicyData {
    totalPremium: number;
    activePolicyNumber: number;
    expiringSoonPolicies: number;
    expiredPolicies: number;
}

const summary = ref<PolicyData>({
    totalPremium: 0,
    activePolicyNumber: 0,
    expiringSoonPolicies: 0,
    expiredPolicies: 0
});

const openEditPolicyDialog = (policy: Policy) => {
    selectedPolicy.value = policy;
    isEditModalOpen.value = true;
};

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
const fetchPolicyData = async (): Promise<void> => {
    try {
        const summaryResponse = await api.get(`/rest/api/dashboard/get-summary/${customer.value.customerId}`);
        console.log('Backend Gelen Yanıt:', summaryResponse.data);

        const summaryResult = summaryResponse.data;
        if (summaryResult.success && summaryResult.data) {
            summary.value = summaryResult.data;
        } else {
            Notify.create({
                type: 'negative',
                message: 'Özet verileri alınamadı: ' + (summaryResult.message || 'Bilinmeyen hata')
            });
        }
    } catch (error) {
        console.error('An error occurred while fetching dashboard data:', error);
        Notify.create({
            type: 'negative',
            message: 'Beklenmeyen bir ağ hatası oluştu. Lütfen bağlantınızı kontrol edin.'
        });
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


interface ModalTypePayload {
    type: string | { label: string; value: string };
}

const handlePolicyUpdate = async (event: { id: string; data: Partial<Omit<Policy, 'policyId' | 'type'>> & Partial<ModalTypePayload> }) => {
    try {
        const payload: Partial<Omit<Policy, 'policyId'>> = {};

        if (event.data.customerId) payload.customerId = event.data.customerId;
        if (event.data.premium !== undefined) payload.premium = event.data.premium;

        if (event.data.startDate) payload.startDate = `${event.data.startDate.replace(/\//g, '-')}T00:00:00`;
        if (event.data.endDate) payload.endDate = `${event.data.endDate.replace(/\//g, '-')}T00:00:00`;

        if (event.data.type) {
            payload.type = typeof event.data.type === 'object' ? event.data.type.value : event.data.type;
        }

        await policyStore.updatePolicy(event.id, payload);
        Notify.create({ message: 'Poliçe başarıyla güncellendi.', color: 'positive', icon: 'check' });
        void policyStore.fetchPolicies({});
    } catch (error) {
        Notify.create({ message: 'Poliçe güncellenirken bir hata oluştu.', color: 'negative' });
        console.log(error);
    }
};

watch(
    () => customer.value?.customerId,
    (newCustomerId) => {
        if (newCustomerId) {
            void fetchPolicyData();
        }
    },
    { immediate: true }
);

onMounted(() => {
    console.log('Sayfa yüklendi');

    void fetchCustomerDetails();
    void fetchCustomerPolicies();
});
</script>