<template>
    <q-page class="q-pa-md fade-in-up">

        <!-- Breadcrumb -->
        <div class="app-breadcrumb">
            <span class="current">Dashboard</span>
        </div>

        <q-card class="my-card">
            <q-card-section>
                <div class="text-h6">Dashboard</div>
            </q-card-section>

            <q-separator />

            <q-card-section>
                <DashboardSummaryCard :summary="summary" :loading="isInitialLoading" />
            </q-card-section>

            <q-card-section>
                <DashboardCharts v-model:selected-year="year" :chart-data-from-api="chartDataFromApi" :summary="summary"
                    :renewal-chart-data="renewalChartData" :bar-chart-data="barChartData" :pie-chart-data="pieChartData"
                    :status-chart-data="statusChartData" @year-changed="refreshAllData" />
            </q-card-section>

            <q-card-section>
                <div class="text-subtitle1 q-mb-md text-center text-weight-bold text-grey-8">
                    Yenilenmesi Gereken Poliçeler
                </div>
                <PolicyTable :policies="renewalPolicies" :loading="renewalLoading" :rows-number="renewalTotalRows"
                    title="Yenilenmesi Gereken Poliçeler" :show-add-button="false" :show-edit-action="false"
                    :show-delete-action="false" @request="onRenewalTableRequest">
                    <template v-slot:row-actions="{ policy }">
                        <q-btn flat round color="primary" icon="account_circle" size="sm"
                            :to="`/customer/${policy.customerId}`" @click.stop>
                            <q-tooltip>Müşteri Detayına Git</q-tooltip>
                        </q-btn>
                    </template>
                </PolicyTable>
            </q-card-section>

            <q-card-section class="q-pt-none">
                <RecentActivitiesTimeline :activities="activities" />
            </q-card-section>
        </q-card>
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useDashboardData } from '@/composables/useDashboardData';
import DashboardSummaryCard from '@/components/DashboardSummaryCard.vue';
import DashboardCharts from '@/components/DashboardCharts.vue';
import RecentActivitiesTimeline from '@/components/RecentActivitiesTimeline.vue';
import type { ChartData } from 'chart.js';
import { getPolicyTypeColor } from '@/utils/policyHelper';
import { useWebSocket } from '@/composables/useWebSocket';
import PolicyTable from '@/components/PolicyTable.vue';
import { SORT_FIELD_MAP } from '@/types/policy.types';

const {
    summary, activities, chartDataFromApi,
    renewalPolicies, renewalLoading, renewalTotalRows, renewalCurrentPage, renewalPageSize,
    loadDashboard, loadRenewalPolicies,
} = useDashboardData();

const { connect } = useWebSocket();

const isInitialLoading = ref<boolean>(true);
const renewalSortBy = ref<string | null>('endDate');
const renewalDescending = ref<boolean>(false);
const year = ref<number>(new Date().getFullYear());

const buildRenewalSortParam = (): string | undefined => {
    if (renewalSortBy.value && SORT_FIELD_MAP[renewalSortBy.value]) {
        const direction = renewalDescending.value ? 'desc' : 'asc';
        return `${SORT_FIELD_MAP[renewalSortBy.value]},${direction}`;
    }
    return undefined;
};

const refreshAllData = async () => {
    await Promise.all([
        loadDashboard(10, year.value),
        loadRenewalPolicies(renewalCurrentPage.value, renewalPageSize.value, buildRenewalSortParam()),
    ]);
};

const onRenewalTableRequest = async (requestProp: {
    pagination: {
        page: number;
        rowsPerPage: number;
        sortBy?: string | null;
        descending?: boolean;
    }
}) => {
    const { page, rowsPerPage, sortBy, descending } = requestProp.pagination;

    if (sortBy !== undefined) {
        renewalSortBy.value = sortBy;
        renewalDescending.value = descending ?? false;
    }

    await loadRenewalPolicies(page - 1, rowsPerPage, buildRenewalSortParam());
};

const barChartData = computed<ChartData<'bar'>>(() => ({
    labels: Object.keys(chartDataFromApi.value.monthlyPremium),
    datasets: [{
        label: 'Aylık Beklenen Gelir (TL)',
        backgroundColor: '#26A69A',
        data: Object.values(chartDataFromApi.value.monthlyPremium),
    }],
}));

const pieChartData = computed<ChartData<'doughnut'>>(() => {
    const labels = Object.keys(chartDataFromApi.value.typeLabels);
    const backgroundColors = labels.map(label => getPolicyTypeColor(label) || '#607D8B');
    return {
        labels,
        datasets: [{
            label: 'Poliçe Sayısı',
            backgroundColor: backgroundColors,
            data: Object.values(chartDataFromApi.value.typeLabels),
        }],
    };
});

const statusChartData = computed<ChartData<'doughnut'>>(() => ({
    labels: ['Aktif Poliçe', 'Yakında Sona Erecek', 'Süresi Dolmuş'],
    datasets: [{
        label: 'Poliçe Durumu',
        backgroundColor: ['#21BA45', '#F2C037', '#C10015'],
        data: [
            summary.value.activePolicyNumber,
            summary.value.expiringSoonPolicies,
            summary.value.expiredPolicies,
        ],
    }],
}));

const renewalChartData = computed<ChartData<'bar'>>(() => {
    return {
        labels: ['0-7 Gün (Kritik)', '8-15 Gün (Uyarı)', '16-30 Gün (Normal)'],
        datasets: [{
            label: 'Poliçe Sayısı',
            data: [chartDataFromApi.value.numberOfCriticalPolicies, chartDataFromApi.value.numberOfWarningPolicies, chartDataFromApi.value.numberOfNormalPolicies],
            backgroundColor: ['#C10015', '#F2C037', '#26A69A'],
            borderRadius: 6,
            barThickness: 28,
        }],
    };
});

onMounted(async () => {
    try {
        await refreshAllData();
    } finally {
        isInitialLoading.value = false;
    }
    connect((_signal) => {
        console.log(_signal);
        void refreshAllData();
    });
});
</script>

<style scoped></style>