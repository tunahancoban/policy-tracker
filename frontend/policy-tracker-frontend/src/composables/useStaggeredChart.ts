// composables/useStaggeredChart.ts
import { Chart, type ChartConfiguration } from 'chart.js/auto';
import ChartDataLabels from 'chartjs-plugin-datalabels';

export function createStaggeredChart(
  canvas: HTMLCanvasElement,
  config: ChartConfiguration,
  withDataLabels = false,
): Chart {
  let delayed = false;

  const mergedConfig: ChartConfiguration = {
    ...config,
    plugins: withDataLabels ? [ChartDataLabels, ...(config.plugins ?? [])] : (config.plugins ?? []),
    options: {
      responsive: true,
      maintainAspectRatio: false,
      ...config.options,
      animation: {
        onComplete: () => {
          delayed = true;
        },
        delay: (context) => {
          if (context.type === 'data' && context.mode === 'default' && !delayed) {
            return context.dataIndex * 300 + context.datasetIndex * 100;
          }
          return 0;
        },
        ...(config.options?.animation as object),
      },
    },
  };

  return new Chart(canvas, mergedConfig);
}
