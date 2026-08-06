<template>
    <q-page class="q-pa-md">
        <div v-if="isLoading" class="row justify-center items-center q-pa-xl">
            <q-spinner-dots color="primary" size="60px" />
        </div>

        <template v-else-if="policy">
            <div class="row items-center justify-between q-mb-md">
                <q-btn flat color="primary" icon="arrow_back" label="Poliçe Listesine Dön" to="/policy" />
            </div>

            <div class="row q-col-gutter-md">
                <!-- Sol Kolon: Poliçe Bilgisi -->
                <div class="col-12 col-md-4">
                    <q-card flat bordered class="full-height">
                        <q-card-section class="bg-primary text-white row items-center q-pa-lg">
                            <q-icon name="description" size="40px" class="q-mr-md" />
                            <div>
                                <div class="text-h6 text-weight-bold">{{ policy.policyId }}</div>
                                <div class="text-caption text-blue-2">{{ policy.type }}</div>
                            </div>
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
                                        <q-item-label class="text-weight-medium">{{ policy.startDate }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="event_busy" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Bitiş Tarihi</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.endDate }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="payments" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Prim Tutarı</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.premium }} TL</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item v-if="policy.note" class="q-py-sm">
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
                            <div v-if="installments.length === 0 && !isInstallmentsLoading"
                                class="text-center q-pa-xl text-grey-6">
                                <q-icon name="folder_open" size="64px" color="grey-4" />
                                <div class="text-subtitle1 q-mt-md">Bu poliçeye ait taksit kaydı bulunamadı.</div>
                            </div>

                            <q-table v-else flat bordered :rows="installments" :columns="installmentColumns"
                                row-key="installmentNo" :loading="isInstallmentsLoading" v-model:pagination="pagination"
                                :rows-number="installmentsTotal" no-data-label="Taksit bulunamadı."
                                @request="onInstallmentsRequest">
                                <template v-slot:body-cell-status="props">
                                    <q-td :props="props" class="text-center">
                                        <q-chip :color="props.row.status === 'PAID' ? 'positive' : 'warning'"
                                            text-color="white" dense class="text-weight-bold">
                                            {{ props.row.status === 'PAID' ? 'Ödendi' : 'Ödenmedi' }}
                                        </q-chip>
                                    </q-td>
                                </template>
                            </q-table>
                        </q-card-section>
                    </q-card>
                </div>
            </div>
        </template>

        <div v-else class="text-center q-pa-xl text-grey-6">
            <q-icon name="description" size="64px" color="grey-4" />
            <div class="text-subtitle1 q-mt-md">Poliçe bulunamadı.</div>
        </div>
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { usePolicyDetail } from '@/composables/usePolicyDetail';

const route = useRoute();
const policyId = route.params.id as string;

const {
    policy,
    isLoading,
    installments,
    isInstallmentsLoading,
    installmentsTotal,
    fetchInstallmentsOnly,
    loadAllData,

} = usePolicyDetail(policyId);

// Taksit tipine göre kolonlar — Installment.types.ts'e göre kesinleştirilmeli
const installmentColumns = [
    { name: 'installmentNo', label: 'Taksit No', field: 'installmentNo', align: 'center' as const },
    { name: 'amount', label: 'Tutar', field: 'amount', align: 'right' as const, format: (val: number) => `${val} TL` },
    { name: 'dueDate', label: 'Vade Tarihi', field: 'dueDate', align: 'center' as const },
    { name: 'status', label: 'Durum', field: 'status', align: 'center' as const },
];

const pagination = ref({
    page: 1, // Quasar 1-indexed
    rowsPerPage: 5,
    rowsNumber: 0,
});

const onInstallmentsRequest = async (requestProp: { pagination: { page: number; rowsPerPage: number } }) => {
    const { page, rowsPerPage } = requestProp.pagination;

    pagination.value.page = page;
    pagination.value.rowsPerPage = rowsPerPage;
    pagination.value.rowsNumber = installmentsTotal.value;

    await fetchInstallmentsOnly();
};


onMounted(async () => {
    await loadAllData();
    pagination.value.rowsNumber = installmentsTotal.value;
});
</script>