<!-- DashboardCharts.vue -->
<template>
    <div>
        <div class="text-subtitle1 q-mb-md text-center text-weight-bold">Sistem Analiz Grafikleri</div>

        <div class="chart-grid-row">
            <!-- Gelir Grafiği ve Yıl Seçimi -->
            <q-card flat bordered class="q-pa-sm">
                <q-card-section class="row items-center justify-between q-pa-xs q-pb-none">
                    <div class="text-subtitle2 text-weight-bold">Aylık Beklenen Gelir</div>
                    <q-select v-model="selectedYear" :options="yearOptions" dense outlined options-dense
                        style="min-width: 100px;" @update:model-value="emit('year-changed', $event)">
                        <template v-slot:prepend>
                            <q-icon name="event" size="xs" />
                        </template>
                    </q-select>
                </q-card-section>
                <q-card-section class="q-pa-none">
                    <div class="chart-container-responsive">
                        <canvas ref="myBarChartCanvas"></canvas>
                    </div>
                </q-card-section>
            </q-card>

            <q-card flat bordered class="q-pa-sm">
                <div class="chart-container-responsive">
                    <canvas ref="renewalBarChartCanvas"></canvas>
                </div>
            </q-card>
        </div>

        <div class="chart-grid-row">
            <q-card flat bordered class="q-pa-sm">
                <div class="chart-container-responsive">
                    <canvas ref="myPieChartCanvas"></canvas>
                </div>
            </q-card>
            <q-card flat bordered class="q-pa-sm">
                <div class="chart-container-responsive">
                    <canvas ref="myStatusChartCanvas"></canvas>
                </div>
            </q-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onBeforeUnmount } from 'vue';
import { useQuasar } from 'quasar';
import { Chart as ChartJS } from 'chart.js';
import { createStaggeredChart } from '@/composables/useStaggeredChart';
import type { ChartResponse, DashboardSummary } from '@/types/dashboard.types';
import type { ChartData, Chart } from 'chart.js';

const $q = useQuasar();

// v-model binding için selectedYear ve emit tanımı
const selectedYear = defineModel<number>('selectedYear', { default: new Date().getFullYear() });
const emit = defineEmits<{
    (e: 'year-changed', year: number): void;
}>();

const currentYear = new Date().getFullYear() + 1;
const yearOptions = Array.from({ length: 5 }, (_, i) => currentYear - i);

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

const getCssVar = (varName: string): string => {
    return getComputedStyle(document.body).getPropertyValue(varName).trim();
};

const initCharts = () => {
    destroyCharts();

    const isDark = $q.dark.isActive;
    const textPrimary = getCssVar('--text-primary') || (isDark ? '#eeeeee' : '#222831');
    const textSecondary = getCssVar('--text-secondary') || (isDark ? '#b8c1c8' : '#31363f');
    const borderColor = getCssVar('--border-color') || (isDark ? 'rgba(238, 238, 238, 0.1)' : 'rgba(34, 40, 49, 0.12)');

    ChartJS.defaults.color = textSecondary;

    if (myBarChartCanvas.value) {
        chartInstances.push(createStaggeredChart(myBarChartCanvas.value, {
            type: 'bar',
            data: props.barChartData,
            options: {
                scales: {
                    x: {
                        ticks: { color: textSecondary },
                        grid: { color: borderColor },
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { color: textSecondary },
                        grid: { color: borderColor },
                    },
                },
                plugins: {
                    legend: {
                        position: 'top',
                        labels: { color: textPrimary, font: { size: 12 } },
                    },
                    title: { display: false },
                },
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
                    x: {
                        beginAtZero: true,
                        ticks: { stepSize: 5, precision: 0, color: textSecondary },
                        grid: { display: false },
                    },
                    y: {
                        ticks: { stepSize: 5, precision: 0, color: textSecondary },
                        grid: { display: false },
                    },
                },
                plugins: {
                    legend: { display: false },
                    title: {
                        display: true,
                        text: 'Kalan Gün Dağılımı',
                        color: textPrimary,
                        font: { size: 14, weight: 'bold' },
                    },
                    datalabels: {
                        anchor: 'end',
                        align: 'end',
                        color: textPrimary,
                        font: { weight: 'bold', size: 12 },
                        formatter: (value: number) => (value > 0 ? value : ''),
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
                    legend: {
                        position: 'top',
                        labels: { color: textPrimary, font: { size: 12 } },
                    },
                    title: {
                        display: true,
                        text: 'Poliçe Türü Dağılımı',
                        color: textPrimary,
                        font: { size: 14, weight: 'bold' },
                    },
                    datalabels: {
                        color: '#ffffff',
                        font: { weight: 'bold', size: 12 },
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
                    legend: {
                        position: 'top',
                        labels: { color: textPrimary, font: { size: 12 } },
                    },
                    title: {
                        display: true,
                        text: 'Poliçe Durum Dağılımı',
                        color: textPrimary,
                        font: { size: 14, weight: 'bold' },
                    },
                    datalabels: {
                        color: '#ffffff',
                        font: { weight: 'bold', size: 12 },
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

watch(
    () => $q.dark.isActive,
    async () => {
        await nextTick();
        initCharts();
    }
);

onBeforeUnmount(() => {
    destroyCharts();
});
</script>

<style scoped>
.chart-grid-row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    width: 100%;
}

.chart-container-responsive {
    position: relative;
    width: 100%;
    min-height: 260px;
    max-height: 360px;
    aspect-ratio: 16 / 10;
    overflow: hidden;
}

.chart-container-responsive canvas {
    display: block;
    width: 100% !important;
    height: 100% !important;
}
</style>