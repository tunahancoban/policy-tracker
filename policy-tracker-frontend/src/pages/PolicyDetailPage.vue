<template>
    <q-page class="q-pa-md fade-in-up">

        <!-- Skeleton Loading -->
        <div v-if="isLoading" class="q-pa-xl">
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

        <template v-else-if="policy">
            <!-- Breadcrumb -->
            <div class="app-breadcrumb">
                <router-link to="/dashboard">Dashboard</router-link>
                <span class="separator">›</span>
                <router-link to="/policy">Poliçeler</router-link>
                <span class="separator">›</span>
                <span class="current">{{ policy.policyId }}</span>
            </div>

            <div class="row q-col-gutter-md">

                <div class="col-12 col-md-4">
                    <q-card flat bordered class="full-height">
                        <q-card-section class="bg-primary text-white row items-center justify-between q-pa-lg">
                            <div class="row items-center">
                                <q-icon name="description" size="40px" class="q-mr-md" />
                                <div>
                                    <div class="text-h6 text-weight-bold">{{ policy.policyId }}</div>
                                    <div class="text-caption text-blue-2">{{ policy.type }}</div>
                                </div>
                            </div>

                            <q-btn flat round dense icon="edit" color="white" @click="openEditDialog(policy)">
                                <q-tooltip>Poliçeyi Düzenle</q-tooltip>
                            </q-btn>
                        </q-card-section>

                        <q-separator />

                        <q-card-section class="q-pa-md">
                            <q-list dense>
                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="account_circle"
                                            color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Müşteri Numarası</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.customerId }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="event" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Başlangıç Tarihi</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ formatDateOnly(policy.startDate)
                                            }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="event_busy" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Bitiş Tarihi</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ formatDateOnly(policy.endDate)
                                            }}</q-item-label>
                                    </q-item-section>
                                </q-item>
                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="account_circle"
                                            color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Acenta Sorumlusu</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ selectedUser?.email
                                            }}</q-item-label>
                                    </q-item-section>
                                </q-item>


                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="payments" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Prim Tutarı</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ formatCurrency(policy.premium)
                                            }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="notes" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Not</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.note }}</q-item-label>
                                    </q-item-section>
                                </q-item>
                            </q-list>
                        </q-card-section>
                    </q-card>
                </div>

                <!-- Sağ Kolon: Taksit Tablosu -->
                <div class="col-12 col-md-8">
                    <q-card flat bordered>
                        <q-card-section class="row items-center q-py-md">
                            <div class="text-h6 text-grey-8 row items-center">
                                <q-icon name="payment" color="secondary" class="q-mr-sm" size="24px" />
                                Taksit Bilgileri
                            </div>
                        </q-card-section>

                        <q-separator />

                        <q-card-section>
                            <div v-if="installments.length === 0 && !isInstallmentsLoading" class="empty-state">
                                <div class="empty-state__icon">
                                    <q-icon name="folder_open" size="32px" color="grey-5" />
                                </div>
                                <div class="empty-state__title">Taksit kaydı bulunamadı</div>
                                <div class="empty-state__description">Bu poliçeye ait henüz bir taksit tanımı
                                    yapılmamış.
                                </div>
                            </div>

                            <q-table v-else flat bordered :rows="installments" :columns="installmentColumns"
                                row-key="installmentNo" :loading="isInstallmentsLoading" v-model:pagination="pagination"
                                :rows-number="installmentsTotal" no-data-label="Taksit bulunamadı."
                                @request="onInstallmentsRequest">
                                <template v-slot:body-cell-amount="props">
                                    <q-td :props="props" class="text-right">
                                        {{ formatCurrency(props.row.amount) }}
                                    </q-td>
                                </template>
                                <template v-slot:body-cell-dueDate="props">
                                    <q-td :props="props" class="text-center">
                                        {{ formatDateOnly(props.row.dueDate) }}
                                    </q-td>
                                </template>
                                <template v-slot:body-cell-paymentStatus="props">
                                    <q-td :props="props" class="text-center">
                                        <q-chip :color="getStatusColor(props.row.status)" text-color="white" dense
                                            class="status-chip">
                                            {{ getStatusLabel(props.row.status) }}
                                        </q-chip>
                                    </q-td>
                                </template>
                                <template v-slot:body-cell-operations="props">
                                    <q-td :props="props" class="text-center">
                                        <q-btn v-if="props.row.status !== 'PAID'" flat color="primary" icon="payment"
                                            label="Ödeme Yap" :loading="paymentLoadingNo === props.row.installmentNo"
                                            @click="handlePayInstallment(props.row)" />
                                    </q-td>
                                </template>
                            </q-table>
                        </q-card-section>
                    </q-card>
                </div>
            </div>
        </template>

        <div v-else class="empty-state">
            <div class="empty-state__icon">
                <q-icon name="description" size="32px" color="grey-5" />
            </div>
            <div class="empty-state__title">Poliçe bulunamadı</div>
            <div class="empty-state__description">Aradığınız poliçe kaydına erişilemiyor.</div>
        </div>

        <!-- Poliçe Düzenleme Modalı -->
        <EditPolicyModal v-if="selectedPolicy" v-model="isEditModalOpen" :policyData="selectedPolicy"
            @updated="handlePolicyUpdate" />
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { usePolicyDetail } from '@/composables/usePolicyDetail';
import { usePolicyList } from '@/composables/usePolicyList';
import { useInstallmentStore } from '@/stores/installment';
import type { Installment } from '@/types/installment.types';
import type { Policy } from '@/types/policy.types';
import { useConfirmDialog } from '@/composables/useConfirmDialog';
import { Notify } from 'quasar';
import EditPolicyModal from '@/components/EditPolicyModal.vue';
import { formatDateOnly, formatCurrency, getStatusColor, getStatusLabel } from '@/utils/policyHelper';

const route = useRoute();
const policyId = route.params.id as string;
const installmentStore = useInstallmentStore();
const { confirm } = useConfirmDialog();

const { setPaymentStatus } = installmentStore;

const {
    policy,
    isLoading,
    installments,
    isInstallmentsLoading,
    installmentsTotal,
    selectedUser,
    getUser,
    fetchInstallmentsOnly,
    loadAllData,
} = usePolicyDetail(policyId);

const { updatePolicy } = usePolicyList();

const paymentLoadingNo = ref<number | null>(null);

// Düzenleme Modalı State'i
const isEditModalOpen = ref<boolean>(false);
const selectedPolicy = ref<Policy | null>(null);

const openEditDialog = (targetPolicy: Policy) => {
    selectedPolicy.value = targetPolicy;
    isEditModalOpen.value = true;
};


const handlePolicyUpdate = async (event: { id: string; data: Partial<Policy> }) => {
    try {
        await updatePolicy(event.id, event.data);
        Notify.create({
            message: 'Poliçe başarıyla güncellendi.',
            color: 'positive',
            icon: 'check_circle',
            position: 'top-right',
            timeout: 4000
        });
        isEditModalOpen.value = false;
        await loadAllData(); // güncel veriyi tekrar çek
    } catch (error) {
        Notify.create({
            message: 'Poliçe güncellenirken bir hata oluştu.',
            color: 'negative',
            icon: 'error',
            position: 'top-right',
            timeout: 5000
        });
        console.error('Policy Update Error:', error);
        throw error;
    }
};



const installmentColumns = [
    { name: 'installmentNo', label: 'Taksit No', field: 'installmentNo', align: 'center' as const },
    { name: 'amount', label: 'Tutar', field: 'amount', align: 'right' as const },
    { name: 'dueDate', label: 'Vade Tarihi', field: 'dueDate', align: 'center' as const },
    { name: 'paymentStatus', label: 'Durum', field: 'paymentStatus', align: 'center' as const },
    { name: 'operations', label: 'İşlemler', field: 'operations', align: 'center' as const },
];

const pagination = ref({
    page: 1,
    rowsPerPage: 5,
    rowsNumber: 0,
});

const handlePayInstallment = async (installment: Installment) => {
    const isConfirmed = await confirm({
        title: 'Ödeme Onayı',
        message: `${installment.installmentNo}. taksit için ${formatCurrency(installment.amount)} tutarındaki ödemeyi onaylıyor musunuz?`,
        okLabel: 'Öde',
        cancelLabel: 'Vazgeç',
        color: 'primary'
    });

    if (!isConfirmed) return;

    paymentLoadingNo.value = installment.installmentNo;
    try {
        await setPaymentStatus(installment.id, installment.installmentNo, 'PAID');
        Notify.create({
            message: `${installment.installmentNo}. taksit ödemesi başarıyla gerçekleştirildi.`,
            color: 'positive',
            icon: 'check_circle',
            position: 'top-right',
            timeout: 4000
        });
        await fetchInstallmentsOnly();
    } catch (error) {
        Notify.create({
            message: 'Taksit ödemesi alınırken bir hata oluştu.',
            color: 'negative',
            icon: 'error',
            position: 'top-right',
            timeout: 5000
        });
        console.error('Taksit ödeme hatası:', error);
    } finally {
        paymentLoadingNo.value = null;
    }
};

const onInstallmentsRequest = async (requestProp: { pagination: { page: number; rowsPerPage: number } }) => {
    const { page, rowsPerPage } = requestProp.pagination;

    pagination.value.page = page;
    pagination.value.rowsPerPage = rowsPerPage;
    pagination.value.rowsNumber = installmentsTotal.value;

    await fetchInstallmentsOnly();
};

onMounted(async () => {
    await loadAllData();

    if (policy.value?.responsibleUserId) {
        await getUser(policy.value.responsibleUserId);
    }

    pagination.value.rowsNumber = installmentsTotal.value;
});
</script>