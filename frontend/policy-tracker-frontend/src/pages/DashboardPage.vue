<template>
    <q-page class="q-pa-md fade-in-up">
        <q-card class="my-card">
            <q-card-section>
                <div class="text-h6">Dashboard</div>
            </q-card-section>

            <q-separator />

            <!-- Üst İstatistik Kartları -->
            <q-card-section>
                <div class="row q-col-gutter-md">
                    <div class="col-12 col-sm-6 col-md-3">
                        <q-card class="my-card">
                            <q-card-section>
                                <div class="text-subtitle1">Toplam Müşteri</div>
                                <div class="text-h5">{{ summary.totalCustomers }}</div>
                            </q-card-section>
                        </q-card>
                    </div>
                    <div class="col-12 col-sm-6 col-md-3">
                        <q-card class="my-card">
                            <q-card-section>
                                <div class="text-subtitle1">Aktif Poliçe</div>
                                <div class="text-h5">{{ summary.activePolicyNumber }}</div>
                            </q-card-section>
                        </q-card>
                    </div>
                    <div class="col-12 col-sm-6 col-md-3">
                        <q-card class="my-card">
                            <q-card-section>
                                <div class="text-subtitle1">Yakında Sona Erecek Poliçeler</div>
                                <div class="text-h5">{{ summary.expiringSoonPolicies }}</div>
                            </q-card-section>
                        </q-card>
                    </div>
                    <div class="col-12 col-sm-6 col-md-3">
                        <q-card class="my-card">
                            <q-card-section>
                                <div class="text-subtitle1">Süresi Dolmuş Poliçeler</div>
                                <div class="text-h5">{{ summary.expiredPolicies }}</div>
                            </q-card-section>
                        </q-card>
                    </div>
                </div>
            </q-card-section>

            <!-- Grafik Alanı (Kesin Çözüm İçin row İçine Alındı) -->
            <q-card-section>
                <div class="text-subtitle1 q-mb-md text-center text-weight-bold text-grey-8">Sistem Analiz Grafikleri
                </div>

                <!-- 1. SIRA: BAR GRAFİKLERİ YAN YANA -->
                <div class="chart-grid-row">
                    <q-card flat bordered class="q-pa-sm">
                        <div class="chart-container">
                            <canvas ref="myBarChartCanvas"></canvas>
                        </div>
                    </q-card>

                    <q-card flat bordered class="q-pa-sm">
                        <div class="chart-container">
                            <canvas ref="renewalBarChartCanvas"></canvas>
                        </div>
                    </q-card>
                </div>

                <!-- 2. SIRA: DOUGHNUT GRAFİKLERİ YAN YANA -->
                <div class="chart-grid-row">
                    <q-card flat bordered class="q-pa-sm">
                        <div class="chart-container">
                            <canvas ref="myPieChartCanvas"></canvas>
                        </div>
                    </q-card>

                    <q-card flat bordered class="q-pa-sm">
                        <div class="chart-container">
                            <canvas ref="myStatusChartCanvas"></canvas>
                        </div>
                    </q-card>
                </div>
            </q-card-section>

            <!-- Yenilenmesi Gereken Poliçeler Bölümü -->
            <q-card-section>
                <div class="text-subtitle1 q-mb-md text-center text-weight-bold text-grey-8">Yenilenmesi Gereken
                    Poliçeler</div>
                <div class="row q-col-gutter-md">
                    <div class="col-12">
                        <q-card flat bordered>
                            <q-table flat dense :rows="renewalPolicies" :columns="renewalColumns" row-key="policyId"
                                :loading="renewalLoading" :pagination="renewalPagination"
                                no-data-label="Yenilenmesi gereken poliçe bulunmuyor."
                                loading-label="Veriler getiriliyor..." style="max-height: 320px;" virtual-scroll>

                                <template v-slot:body-cell-type="props">
                                    <q-td :props="props">
                                        <q-chip :color="getPolicyTypeColor(props.row.type)"
                                            :text-color="getPolicyTypeTextColor(props.row.type)" dense square
                                            class="text-weight-bold text-caption">
                                            {{ props.row.type }}
                                        </q-chip>
                                    </q-td>
                                </template>

                                <template v-slot:body-cell-remainingDays="props">
                                    <q-td :props="props" class="text-center">
                                        <q-chip :color="getRemainingDaysColor(props.row.endDate)" text-color="white"
                                            dense class="text-weight-bold">
                                            {{ calculateRemainingDays(props.row.endDate) }}
                                        </q-chip>
                                    </q-td>
                                </template>

                                <template v-slot:body-cell-premium="props">
                                    <q-td :props="props" class="text-weight-medium text-right text-subtitle2">
                                        {{ props.row.premium != null ? `${props.row.premium.toLocaleString('tr-TR')} TL`
                                            : '0 TL' }}
                                    </q-td>
                                </template>

                                <template v-slot:body-cell-actions="props">
                                    <q-td :props="props" class="text-center">
                                        <q-btn flat round color="primary" icon="account_circle" size="sm"
                                            :to="`/customer/${props.row.customerId}`">
                                            <q-tooltip>Müşteri Detayına Git</q-tooltip>
                                        </q-btn>
                                    </q-td>
                                </template>
                            </q-table>
                        </q-card>
                    </div>
                </div>
            </q-card-section>

            <!-- Son İşlemler Zaman Çizelgesi -->
            <q-card-section class="q-pt-none">
                <div class="q-px-md q-py-sm scroll" style="max-height: 400px; overflow-y: auto;">
                    <q-timeline color="secondary">
                        <q-timeline-entry heading> Son İşlemler </q-timeline-entry>

                        <q-timeline-entry v-for="(activity, index) in activities" :key="index" :title="activity.type"
                            :subtitle="formatDate(activity.dateTime)">
                            <div class="text-body2 text-grey-8">
                                {{ activity.detail }}
                            </div>
                        </q-timeline-entry>
                    </q-timeline>
                </div>
            </q-card-section>

        </q-card>
    </q-page>
</template>


<style scoped>
.chart-grid-row {
    display: grid;
    grid-template-columns: minmax(0, 1fr);
    gap: 16px;
    margin-bottom: 16px;
}

/* Tablet ve üstü ekranlarda 2 sütun */
@media (min-width: 1024px) {
    .chart-grid-row {
        grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
    }
}

.chart-container {
    position: relative;
    width: 100%;
    height: 320px;
    overflow: hidden;
}

.chart-container canvas {
    display: block;
    width: 100% !important;
    height: 100% !important;
}
</style>

<script setup lang="ts">
// nextTick Vue paketinden import edildi
import { ref, onMounted, computed, nextTick } from 'vue';
import { api } from '../boot/axios';
import { Notify } from 'quasar';
import Chart from 'chart.js/auto';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import { calculateRemainingDays, getRemainingDaysColor } from '../utils/dateHelper';
import type { Policy } from '../types/policy.types';
import type { ApiResponse } from '../types/api.types';

interface DashboardData {
    totalCustomers: number;
    activePolicyNumber: number;
    expiringSoonPolicies: number;
    expiredPolicies: number;
}

interface RecentActivity {
    type: string;
    detail: string;
    dateTime: string;
}

interface ChartResponseData {
    typeLabels: Record<string, number>;
    monthlyPremium: Record<string, number>;
}

const summary = ref<DashboardData>({
    totalCustomers: 0,
    activePolicyNumber: 0,
    expiringSoonPolicies: 0,
    expiredPolicies: 0
});

const activities = ref<RecentActivity[]>([]);

const chartDataFromApi = ref<ChartResponseData>({
    typeLabels: {},
    monthlyPremium: {}
});

const myPieChartCanvas = ref<HTMLCanvasElement | null>(null);
const myBarChartCanvas = ref<HTMLCanvasElement | null>(null);
const myStatusChartCanvas = ref<HTMLCanvasElement | null>(null);
const renewalBarChartCanvas = ref<HTMLCanvasElement | null>(null);

const renewalPolicies = ref<Policy[]>([]);
const renewalLoading = ref<boolean>(false);
const renewalPagination = ref({ rowsPerPage: 10 });

const renewalColumns = [
    { name: 'policyId', label: 'Poliçe No', field: 'policyId', align: 'left' as const, sortable: true },
    { name: 'type', label: 'Tür', field: 'type', align: 'center' as const, sortable: true },
    { name: 'customerId', label: 'Müşteri ID', field: 'customerId', align: 'left' as const, sortable: true },
    { name: 'endDate', label: 'Bitiş Tarihi', field: 'endDate', align: 'center' as const, sortable: true },
    { name: 'remainingDays', label: 'Kalan Gün', field: 'endDate', align: 'center' as const, sortable: true },
    { name: 'premium', label: 'Prim', field: 'premium', align: 'right' as const, sortable: true },
    { name: 'actions', label: '', field: 'actions', align: 'center' as const },
];

const getPolicyTypeColor = (type: string): string => {
    const map: Record<string, string> = {
        'KASKO': 'blue-2', 'TRAFİK': 'teal-2', 'TRAFIK': 'teal-2',
        'SAGLIK': 'green-2', 'KONUT': 'amber-2', 'DASK': 'purple-2'
    };
    return map[type] || 'grey-3';
};

const getPolicyTypeTextColor = (type: string): string => {
    const map: Record<string, string> = {
        'KASKO': 'blue-9', 'TRAFİK': 'teal-9', 'TRAFIK': 'teal-9',
        'SAGLIK': 'green-9', 'KONUT': 'amber-9', 'DASK': 'purple-9'
    };
    return map[type] || 'grey-9';
};

const fetchRenewalPolicies = async () => {
    renewalLoading.value = true;
    try {
        const response = await api.get<ApiResponse<Policy[]>>('/rest/api/policy/with-params');
        if (response.data.success && response.data.data) {
            const allPolicies = Array.isArray(response.data.data) ? response.data.data : [response.data.data];

            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const thirtyDaysLater = new Date(today);
            thirtyDaysLater.setDate(thirtyDaysLater.getDate() + 30);

            renewalPolicies.value = allPolicies
                .filter((p: Policy) => {
                    const end = new Date(p.endDate);
                    end.setHours(0, 0, 0, 0);
                    return end >= today && end <= thirtyDaysLater;
                })
                .sort((a: Policy, b: Policy) => new Date(a.endDate).getTime() - new Date(b.endDate).getTime());
        }
    } catch (error) {
        console.error('Yenilenmesi gereken poliçeler yüklenirken hata:', error);
    } finally {
        renewalLoading.value = false;
    }
};

const renewalChartData = computed(() => {
    let critical = 0;
    let warning = 0;
    let normal = 0;

    renewalPolicies.value.forEach((p: Policy) => {
        const end = new Date(p.endDate);
        const today = new Date();
        end.setHours(0, 0, 0, 0);
        today.setHours(0, 0, 0, 0);
        const diff = Math.ceil((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

        if (diff <= 7) critical++;
        else if (diff <= 15) warning++;
        else normal++;
    });

    return {
        labels: ['0-7 Gün (Kritik)', '8-15 Gün (Uyarı)', '16-30 Gün (Normal)'],
        datasets: [{
            label: 'Poliçe Sayısı',
            data: [critical, warning, normal],
            backgroundColor: ['#C10015', '#F2C037', '#26A69A'],
            borderRadius: 6,
            barThickness: 28,
        }]
    };
});

const statusChartData = computed(() => {
    return {
        labels: ['Aktif Poliçe', 'Yakında Sona Erecek', 'Süresi Dolmuş'],
        datasets: [{
            label: 'Poliçe Durumu',
            backgroundColor: ['#21BA45', '#F2C037', '#C10015'],
            data: [
                summary.value.activePolicyNumber,
                summary.value.expiringSoonPolicies,
                summary.value.expiredPolicies
            ]
        }]
    };
});

const fetchDashboardData = async () => {
    try {
        const [summaryResponse, activitiesResponse, chartsResponse] = await Promise.all([
            api.get('/rest/api/dashboard/get-summary'),
            api.get('/rest/api/dashboard/get-recent-activities/5'),
            api.get('/rest/api/dashboard/get-charts')
        ]);

        const summaryResult = summaryResponse.data;
        if (summaryResult.success && summaryResult.data) {
            summary.value = summaryResult.data;
        } else {
            showErrorNotify('Özet verileri alınamadı: ' + (summaryResult.message || 'Bilinmeyen hata'));
        }

        const activitiesResult = activitiesResponse.data;
        if (activitiesResult.success && activitiesResult.data) {
            activities.value = activitiesResult.data;
        } else {
            showErrorNotify('Son işlemler verileri alınamadı: ' + (activitiesResult.message || 'Bilinmeyen hata'));
        }

        const chartsResult = chartsResponse.data;
        if (chartsResult.success && chartsResult.data) {
            chartDataFromApi.value = chartsResult.data;
        } else {
            showErrorNotify('Grafik verileri alınamadı: ' + (chartsResult.message || 'Bilinmeyen hata'));
        }

    } catch (error: unknown) {
        console.error('An error occurred while fetching dashboard data:', error);
        showErrorNotify('Beklenmeyen bir ağ hatası oluştu. Lütfen bağlantınızı kontrol edin.');
    }
};

const showErrorNotify = (msg: string) => {
    Notify.create({
        actions: [{ label: 'Kapat', color: 'white' }],
        message: msg,
        color: 'red',
        position: 'bottom'
    });
};

const formatDate = (dateString: string): string => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR') + ' ' + date.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' });
};

const policyColorMap: Record<string, string> = {
    'KASKO': '#1976D2',
    'TRAFIK': '#26A69A',
    'SAGLIK': '#21BA45',
    'KONUT': '#F2C037',
    'DASK': '#9C27B0',
};

const pieChartData = computed(() => {
    const labels = Object.keys(chartDataFromApi.value.typeLabels);
    const backgroundColors = labels.map(label => policyColorMap[label] || '#607D8B');
    return {
        labels: labels,
        datasets: [{
            label: 'Poliçe Sayısı',
            backgroundColor: backgroundColors,
            data: Object.values(chartDataFromApi.value.typeLabels)
        }]
    };
});

const barChartData = computed(() => ({
    labels: Object.keys(chartDataFromApi.value.monthlyPremium),
    datasets: [{
        label: 'Aylık Toplam Prim (TL)',
        backgroundColor: '#26A69A',
        data: Object.values(chartDataFromApi.value.monthlyPremium)
    }]
}));

let pieDelayed = false;
let statusDelayed = false;
let barDelayed = false;

onMounted(async () => {
    await Promise.all([fetchDashboardData(), fetchRenewalPolicies()]);

    await nextTick();

    const commonOptions = {
        responsive: true,
        maintainAspectRatio: false
    };

    if (myBarChartCanvas.value) {
        new Chart(myBarChartCanvas.value, {
            type: 'bar',
            data: barChartData.value,
            options: {
                ...commonOptions,
                animation: {
                    onComplete: () => { barDelayed = true; },
                    delay: (context) => {
                        let delay = 0;
                        if (context.type === 'data' && context.mode === 'default' && !barDelayed) {
                            delay = context.dataIndex * 300 + context.datasetIndex * 100;
                        }
                        return delay;
                    }
                },
                scales: {
                    y: { beginAtZero: true }
                },
                plugins: {
                    legend: { position: 'top' },
                    title: { display: true, text: 'Aylık Prim Gelişimi' }
                }
            }
        });
    }

    if (renewalBarChartCanvas.value) {
        const renewalChart = new Chart(renewalBarChartCanvas.value, {
            type: 'bar',
            data: renewalChartData.value,
            plugins: [ChartDataLabels],
            options: {
                ...commonOptions,
                indexAxis: 'y',
                animation: {
                    delay: (context) => context.dataIndex * 200
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: { stepSize: 1, precision: 0 },
                        grid: { display: false }
                    },
                    y: {
                        grid: { display: false }
                    }
                },
                plugins: {
                    legend: { display: false },
                    title: { display: true, text: 'Kalan Gün Dağılımı', font: { size: 14 } },
                    datalabels: {
                        anchor: 'end',
                        align: 'end',
                        color: '#333',
                        font: { weight: 'bold', size: 12 },
                        formatter: (value: number) => value > 0 ? value : ''
                    }
                }
            }
        });
        renewalChart.resize();
    }

    if (myPieChartCanvas.value) {
        new Chart(myPieChartCanvas.value, {
            type: 'doughnut',
            data: pieChartData.value,
            plugins: [ChartDataLabels],
            options: {
                ...commonOptions,
                animation: {
                    onComplete: () => { pieDelayed = true; },
                    delay: (context) => {
                        let delay = 0;
                        if (context.type === 'data' && context.mode === 'default' && !pieDelayed) {
                            delay = context.dataIndex * 300 + context.datasetIndex * 100;
                        }
                        return delay;
                    }
                },
                plugins: {
                    legend: { position: 'top' },
                    title: { display: true, text: 'Poliçe Türü Dağılımı' },
                    datalabels: {
                        color: '#fff',
                        font: { weight: 'bold', size: 12 },
                        formatter: (value, ctx) => {
                            const datasets = ctx.chart.data?.datasets;
                            const datapoints = (datasets && datasets[0]?.data ? datasets[0].data : []) as number[];
                            const total = datapoints.reduce((total, num) => total + num, 0);
                            return total > 0 ? `%${Math.round((value / total) * 100)}` : '%0';
                        }
                    }
                }
            }
        });
    }

    if (myStatusChartCanvas.value) {
        new Chart(myStatusChartCanvas.value, {
            type: 'doughnut',
            data: statusChartData.value,
            plugins: [ChartDataLabels],
            options: {
                ...commonOptions,
                animation: {
                    onComplete: () => { statusDelayed = true; },
                    delay: (context) => {
                        let delay = 0;
                        if (context.type === 'data' && context.mode === 'default' && !statusDelayed) {
                            delay = context.dataIndex * 300 + context.datasetIndex * 100;
                        }
                        return delay;
                    }
                },
                plugins: {
                    legend: { position: 'top' },
                    title: { display: true, text: 'Poliçe Durum Dağılımı' },
                    datalabels: {
                        color: '#fff',
                        font: { weight: 'bold', size: 12 },
                        formatter: (value, ctx) => {
                            const datasets = ctx.chart.data?.datasets;
                            const datapoints = (datasets && datasets[0]?.data ? datasets[0].data : []) as number[];
                            const total = datapoints.reduce((total, num) => total + num, 0);
                            return total > 0 ? `%${Math.round((value / total) * 100)}` : '%0';
                        }
                    }
                }
            }
        });
    }
});
</script>