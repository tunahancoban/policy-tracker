<!-- DashboardCharts.vue -->
<template>
    <div>
        <div class="text-subtitle1 q-mb-md text-center text-weight-bold text-grey-8">Sistem Analiz Grafikleri</div>

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
    </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onBeforeUnmount } from 'vue';
import { createStaggeredChart } from '@/composables/useStaggeredChart';
import type { ChartResponse } from '@/types/dashboard.types';
import type { DashboardSummary } from '@/types/dashboard.types';

import type { ChartData, Chart } from 'chart.js';


const chartInstances: Chart[] = [];

const destroyCharts = () => {
    chartInstances.forEach((chart) => chart.destroy());
    chartInstances.length = 0;
};

const props = defineProps<{
    chartDataFromApi: ChartResponse;
    summary: DashboardSummary;
    renewalChartData: ChartData<'bar'>;
    barChartData: ChartData<'bar'>;
    pieChartData: ChartData<'doughnut'>;
    statusChartData: ChartData<'doughnut'>;
}>();

const myPieChartCanvas = ref<HTMLCanvasElement | null>(null);
const myBarChartCanvas = ref<HTMLCanvasElement | null>(null);
const myStatusChartCanvas = ref<HTMLCanvasElement | null>(null);
const renewalBarChartCanvas = ref<HTMLCanvasElement | null>(null);

const initCharts = () => {
    destroyCharts();

    if (myBarChartCanvas.value) {
        chartInstances.push(createStaggeredChart(myBarChartCanvas.value, {
            type: 'bar',
            data: props.barChartData,
            options: {
                scales: { y: { beginAtZero: true } },
                plugins: { legend: { position: 'top' }, title: { display: true, text: 'Aylık  Beklenen Gelir' } },
            },
        }));
    }
    if (renewalBarChartCanvas.value) {
        chartInstances.push(createStaggeredChart(renewalBarChartCanvas.value, {
            type: 'bar',
            data: props.renewalChartData,
            options: {
                indexAxis: 'x',
                scales: {
                    x: { beginAtZero: true, ticks: { stepSize: 1, precision: 0 }, grid: { display: false } },
                    y: { ticks: { stepSize: 5, precision: 0 }, grid: { display: false } },
                },
                plugins: {
                    legend: { display: false },
                    title: { display: true, text: 'Kalan Gün Dağılımı', font: { size: 14 } },
                    datalabels: {
                        anchor: 'end', align: 'end', color: '#333',
                        font: { weight: 'bold', size: 12 },
                        formatter: (value: number) => value > 0 ? value : '',
                    },
                },
            },
        }, true));
    }
    if (myPieChartCanvas.value) {
        chartInstances.push(createStaggeredChart(myPieChartCanvas.value, {
            type: 'doughnut',
            data: props.pieChartData,
            options: {
                plugins: {
                    legend: { position: 'top' },
                    title: { display: true, text: 'Poliçe Türü Dağılımı' },
                    datalabels: {
                        color: '#fff', font: { weight: 'bold', size: 12 },
                        formatter: (value: number, ctx) => {
                            const datapoints = (ctx.chart.data?.datasets?.[0]?.data ?? []) as number[];
                            const total = datapoints.reduce((t, n) => t + n, 0);
                            return total > 0 ? `%${Math.round((value / total) * 100)}` : '%0';
                        },
                    },
                },
            },
        }, true));
    }
    if (myStatusChartCanvas.value) {
        chartInstances.push(createStaggeredChart(myStatusChartCanvas.value, {
            type: 'doughnut',
            data: props.statusChartData,
            options: {
                plugins: {
                    legend: { position: 'top' },
                    title: { display: true, text: 'Poliçe Durum Dağılımı' },
                    datalabels: {
                        color: '#fff', font: { weight: 'bold', size: 12 },
                        formatter: (value: number, ctx) => {
                            const datapoints = (ctx.chart.data?.datasets?.[0]?.data ?? []) as number[];
                            const total = datapoints.reduce((t, n) => t + n, 0);
                            return total > 0 ? `%${Math.round((value / total) * 100)}` : '%0';
                        },
                    },
                },
            },
        }, true));
    }
};

watch(
    [() => props.chartDataFromApi, () => props.renewalChartData],
    async () => {
        await nextTick();
        initCharts();
    },
    { deep: true }
);

onBeforeUnmount(() => {
    destroyCharts();
},
);
</script>

<style scoped>
.chart-grid-row {
    display: grid;
    grid-template-columns: minmax(0, 1fr);
    gap: 16px;
    margin-bottom: 16px;
}

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