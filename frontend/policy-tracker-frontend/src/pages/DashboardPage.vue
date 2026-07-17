<template>
    <q-page class="q-pa-md fade-in-up">
                <q-card class="my-card">
                    <q-card-section>
                        <div class="text-h6">Dashboard</div>
                    </q-card-section>

                    <q-separator />

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


                    <q-card-section>
                        <div class="text-subtitle1 q-mb-md text-center">Sistem Analiz Grafikleri</div>
                        <div class="row q-col-gutter-md">

                            <div class="col-12 col-md-6">
                                <q-card flat bordered class="q-pa-sm">
                                    <q-card-section style="position: relative; height: 320px; width: 100%;">
                                        <canvas ref="myPieChartCanvas"></canvas>
                                    </q-card-section>
                                </q-card>
                            </div>

                            <div class="col-12 col-md-6">
                                <q-card flat bordered class="q-pa-sm">
                                    <q-card-section style="position: relative; height: 320px; width: 100%;">
                                        <canvas ref="myBarChartCanvas"></canvas>
                                    </q-card-section>
                                </q-card>
                            </div>

                        </div>
                    </q-card-section>

                    <q-card-section class="q-pt-none">
                        <div class="q-px-md q-py-sm scroll" style="max-height: 400px; overflow-y: auto;">
                            <q-timeline color="secondary">
                                <q-timeline-entry heading> Son İşlemler </q-timeline-entry>

                                <q-timeline-entry v-for="(activity, index) in activities" :key="index"
                                    :title="activity.type" :subtitle="formatDate(activity.dateTime)">
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

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { api } from '../boot/axios';
import { Notify } from 'quasar';
import Chart from 'chart.js/auto';
import ChartDataLabels from 'chartjs-plugin-datalabels';

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
        datasets: [
            {
                label: 'Poliçe Sayısı',
                backgroundColor: backgroundColors,
                data: Object.values(chartDataFromApi.value.typeLabels)
            }
        ]
    };
});

// 2. Bar Grafiği İçin Dinamik Veri
const barChartData = computed(() => ({
    labels: Object.keys(chartDataFromApi.value.monthlyPremium),
    datasets: [
        {
            label: 'Aylık Toplam Prim (TL)',
            backgroundColor: '#26A69A',
            data: Object.values(chartDataFromApi.value.monthlyPremium)
        }
    ]
}));

let pieDelayed = false;
let barDelayed = false;

onMounted(async () => {
    await fetchDashboardData();

    if (myPieChartCanvas.value) {
        new Chart(myPieChartCanvas.value, {
            type: 'doughnut',
            data: pieChartData.value,
            plugins: [ChartDataLabels],
            options: {
                responsive: true,
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

    if (myBarChartCanvas.value) {
        new Chart(myBarChartCanvas.value, {
            type: 'bar',
            data: barChartData.value,
            options: {
                responsive: true,
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
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: { position: 'top' },
                    title: { display: true, text: 'Aylık Prim Gelişimi' }
                }
            }
        });
    }
});
</script>