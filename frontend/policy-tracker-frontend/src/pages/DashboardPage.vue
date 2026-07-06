<template>
    <q-layout view="lHh Lpr lFf">
        <q-page-container>

            <q-page class="q-pa-md">
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

                    <q-card-section class="q-pt-none">
                        <div class="text-subtitle1 text-weight-bold q-mb-sm">Son İşlemler</div>
                        <q-list separator>
                            <q-item v-for="(activity, index) in activities" :key="index">
                                <q-item-section>
                                    <q-item-label class="text-weight-medium">{{ activity.title }}</q-item-label>
                                    <q-item-label caption>{{ activity.description }}</q-item-label>
                                </q-item-section>
                                <q-item-section side top>
                                    <q-item-label caption>{{ formatDate(activity.date) }}</q-item-label>
                                </q-item-section>
                            </q-item>
                        </q-list>
                    </q-card-section>

                </q-card>
            </q-page>

        </q-page-container>
    </q-layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { api } from '../boot/axios';
import { Notify } from 'quasar';

interface DashboardData {
    totalCustomers: number;
    activePolicyNumber: number;
    expiringSoonPolicies: number;
    expiredPolicies: number;
}

interface RecentActivity {
    title: string;
    description: string;
    date: string;
}

const summary = ref<DashboardData>({
    totalCustomers: 0,
    activePolicyNumber: 0,
    expiringSoonPolicies: 0,
    expiredPolicies: 0
});

const activities = ref<RecentActivity[]>([]);

const fetchDashboardData = async () => {
    try {
        const [summaryResponse, activitiesResponse] = await Promise.all([
            api.get('/rest/api/dashboard/get-summary'),
            api.get('/rest/api/dashboard/get-recent-activities/5')
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
            showErrorNotify('Son işlemler alınamadı: ' + (activitiesResult.message || 'Bilinmeyen hata'));
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

onMounted(async () => {
    await fetchDashboardData();
});
</script>