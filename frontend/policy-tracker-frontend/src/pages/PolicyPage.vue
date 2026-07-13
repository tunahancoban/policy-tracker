<template>
    <q-page class="q-pa-md">
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
                        <q-input v-model="searchQuery" outlined dense label="Poliçe No veya Müşteri ID ile Ara..."
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
                        <q-btn label="Temizle" color="grey-6" flat dense @click="resetFilters" />
                        <q-btn label="Filtrele" color="primary" icon="filter_alt" class="q-px-md" @click="onSearch" />
                    </div>
                </div>
            </q-card-section>
        </q-card>

        <q-card flat bordered>
            <q-table flat :rows="policies" :columns="columns" row-key="policyId" :loading="isLoading"
                no-data-label="Kriterlere uygun poliçe kaydı bulunamadı." loading-label="Veriler getiriliyor...">
                <template v-slot:body-cell-type="props">
                    <q-td :props="props">
                        <q-chip :color="props.row.type === 'TRAFİK' ? 'teal-2' : 'purple-2'"
                            :text-color="props.row.type === 'TRAFİK' ? 'teal-9' : 'purple-9'" dense square
                            class="text-weight-bold text-caption">
                            {{ props.row.type }}
                        </q-chip>
                    </q-td>
                </template>

                <template v-slot:body-cell-premium="props">
                    <q-td :props="props" class="text-weight-medium text-right text-subtitle2">
                        {{ props.row.premium != null ? `${props.row.premium} TL` : '0.0 TL' }}
                    </q-td>
                </template>

                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="q-gutter-xs text-center">
                        <q-btn flat round color="primary" icon="visibility" size="sm"
                            :to="`/customers/${props.row.customerId}`">
                            <q-tooltip>Müşteri Detayına Git</q-tooltip>
                        </q-btn>
                        <q-btn flat round color="secondary" icon="edit" size="sm" @click="editPolicy(props.row)" />
                    </q-td>
                </template>
            </q-table>
        </q-card>
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { api } from '../boot/axios';

// Poliçe Veri Tipi
interface Policy {
    policyId: string;
    type: string;
    startDate: string;
    endDate: string;
    premium: number | null;
    customerId: string;
}

// Reaktif Değişkenler
const policies = ref<Policy[]>([]);
const isLoading = ref<boolean>(false);
const searchQuery = ref<string>('');
const selectedType = ref<string | null>(null);

// Poliçe Türü Seçenekleri (Backend Enum Değerleri)
const policyTypeOptions = [
    { label: 'Trafik Sigortası', value: 'TRAFİK' },
    { label: 'Kasko Sigortası', value: 'KASKO' }
];

// Tablo Sütun Yapısı
const columns = [
    { name: 'policyId', label: 'Poliçe No', field: 'policyId', align: 'left' as const, sortable: true },
    { name: 'customerId', label: 'Müşteri ID', field: 'customerId', align: 'left' as const, sortable: true },
    { name: 'type', label: 'Poliçe Türü', field: 'type', align: 'left' as const, sortable: true },
    { name: 'startDate', label: 'Başlangıç Tarihi', field: 'startDate', align: 'center' as const, sortable: true },
    { name: 'endDate', label: 'Bitiş Tarihi', field: 'endDate', align: 'center' as const },
    { name: 'premium', label: 'Prim Tutarı', field: 'premium', align: 'right' as const, sortable: true },
    { name: 'actions', label: 'İşlemler', field: 'actions', align: 'center' as const }
];

// Backend Esnek Sorgusunu Tetikleyen Metot
const fetchPolicies = async (params: Record<string, string | null>) => {
    isLoading.value = true;
    try {
        const response = await api.get('/rest/api/policy/with-params', { params });
        if (response.data.success && response.data.data) {
            policies.value = Array.isArray(response.data.data)
                ? response.data.data
                : [response.data.data];
        } else {
            policies.value = [];
        }
    } catch (error) {
        console.error('Poliçeler yüklenirken hata oluştu:', error);
        policies.value = [];
    } finally {
        isLoading.value = false;
    }
};

// Arama Mantığı (Müşteri arama sayfanla aynı esneklikte)
const onSearch = () => {
    const query = searchQuery.value ? searchQuery.value.trim() : '';
    const searchParams: Record<string, string | null> = {
        policyId: null,
        customerId: null,
        type: selectedType.value
    };

    if (query) {
        // Eğer girilen metin CST ile başlıyorsa customerId filtresine ata
        if (query.toUpperCase().startsWith('CST')) {
            searchParams.customerId = query;
        } else {
            // Aksi durumda poliçe numarası olarak değerlendir
            searchParams.policyId = query;
        }
    }

    void fetchPolicies(searchParams);
};

// Filtreleri Sıfırlama
const resetFilters = () => {
    searchQuery.value = '';
    selectedType.value = null;
    void fetchPolicies({});
};

watch(selectedType, () => {
    onSearch();
});

const openCreateDialog = () => {
    console.log('Yeni poliçe oluşturma modalı açılıyor...');
};

const editPolicy = (policy: Policy) => {
    console.log('Poliçe düzenleniyor:', policy.policyId);
};

// Sayfa ilk yüklendiğinde tüm listeyi getir
onMounted(() => {
    void fetchPolicies({});
});
</script>