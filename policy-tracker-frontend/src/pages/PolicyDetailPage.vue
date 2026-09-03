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
                <!-- Sol Kolon: Poliçe Detayları -->
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
                                    <q-item-section avatar><q-icon name="account_circle" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Müşteri Numarası</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.customerId }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm" v-if="policy.company">
                                    <q-item-section avatar><q-icon name="apartment" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Sigorta Şirketi</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.company }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="event" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Başlangıç Tarihi</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ formatDateOnly(policy.startDate) }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="event_busy" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Bitiş Tarihi</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ formatDateOnly(policy.endDate) }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="badge" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Acenta Sorumlusu</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ selectedUser?.email || '-' }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="payments" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Prim Tutarı</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ formatCurrency(policy.premium) }}</q-item-label>
                                    </q-item-section>
                                </q-item>

                                <!-- ─── TRAFİK & KASKO ORTAK ALANLAR ─── -->
                                <template v-if="policy.type === 'TRAFIK' || policy.type === 'KASKO'">
                                    <q-separator spaced inset />
                                    <q-item-label header class="text-primary text-weight-bold">Araç Bilgileri</q-item-label>

                                    <q-item class="q-py-sm" v-if="policy.plateNumber">
                                        <q-item-section avatar><q-icon name="directions_car" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Plaka</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.plateNumber }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.chassisNumber">
                                        <q-item-section avatar><q-icon name="pin" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Şasi No</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.chassisNumber }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.engineNumber">
                                        <q-item-section avatar><q-icon name="engineering" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Motor No</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.engineNumber }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.vehicleUsageType">
                                        <q-item-section avatar><q-icon name="category" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Kullanım Tarzı</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.vehicleUsageType }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.noClaimDiscountStep !== null && policy.noClaimDiscountStep !== undefined">
                                        <q-item-section avatar><q-icon name="trending_down" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Hasarsızlık Kademesi</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.noClaimDiscountStep }}. Basamak</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.hasImm !== undefined">
                                        <q-item-section avatar><q-icon name="security" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>İMM Durumu</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.hasImm ? `Var (${policy.immLimit ? formatCurrency(policy.immLimit) : 'Limitsiz'})` : 'Yok' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>
                                </template>

                                <!-- ─── KASKO ÖZEL ALANLAR ─── -->
                                <template v-if="policy.type === 'KASKO'">
                                    <q-item class="q-py-sm" v-if="policy.vehicleBrand || policy.vehicleModel">
                                        <q-item-section avatar><q-icon name="time_to_leave" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Marka / Model / Yıl</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.vehicleBrand }} {{ policy.vehicleModel }} {{ policy.modelYear ? `(${policy.modelYear})` : '' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.vehicleValue">
                                        <q-item-section avatar><q-icon name="attach_money" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Araç Değeri</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ formatCurrency(policy.vehicleValue) }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.cascoType">
                                        <q-item-section avatar><q-icon name="shield" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Kasko Türü</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.cascoType }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.hasReplacementCar !== undefined">
                                        <q-item-section avatar><q-icon name="car_rental" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>İkame Araç</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.hasReplacementCar ? `Var (${policy.replacementCarDays || 0} Gün)` : 'Yok' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm">
                                        <q-item-section avatar><q-icon name="build" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Yetkili Servis / Cam Muafiyeti</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                Servis: {{ policy.authorizedServiceOnly ? 'Sadece Yetkili' : 'Tüm Servisler' }} | 
                                                Cam: {{ policy.glassExemption ? 'Muafiyetli' : 'Muafiyetsiz' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>
                                </template>

                                <!-- ─── DASK & KONUT ORTAK ALANLAR ─── -->
                                <template v-if="policy.type === 'DASK' || policy.type === 'KONUT'">
                                    <q-separator spaced inset />
                                    <q-item-label header class="text-primary text-weight-bold">Bina / Konut Bilgileri</q-item-label>

                                    <q-item class="q-py-sm" v-if="policy.uavtCode">
                                        <q-item-section avatar><q-icon name="home_work" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>UAVT / Adres Kodu</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.uavtCode }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.grossSquareMeters">
                                        <q-item-section avatar><q-icon name="square_foot" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Brüt Metrekare</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.grossSquareMeters }} m²</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.buildingConstructionType || policy.buildingConstructionYear">
                                        <q-item-section avatar><q-icon name="domain" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Yapı Tarzı / İnşa Yılı</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.buildingConstructionType || '-' }} ({{ policy.buildingConstructionYear || '-' }})
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.totalFloorCount !== null || policy.apartmentFloor !== null">
                                        <q-item-section avatar><q-icon name="stairs" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Kat / Toplam Kat</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.apartmentFloor ?? '-' }} / {{ policy.totalFloorCount ?? '-' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.earthquakeZone">
                                        <q-item-section avatar><q-icon name="warning" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Deprem Bölgesi</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.earthquakeZone }}. Bölge</q-item-label>
                                        </q-item-section>
                                    </q-item>
                                </template>

                                <!-- ─── KONUT ÖZEL ALANLAR ─── -->
                                <template v-if="policy.type === 'KONUT'">
                                    <q-item class="q-py-sm" v-if="policy.residenceType">
                                        <q-item-section avatar><q-icon name="person_outline" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Kullanım Durumu</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.residenceType }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.buildingCoverageLimit">
                                        <q-item-section avatar><q-icon name="apartment" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Bina Teminatı</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ formatCurrency(policy.buildingCoverageLimit) }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.contentsCoverageLimit">
                                        <q-item-section avatar><q-icon name="chair" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Eşya Teminatı</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ formatCurrency(policy.contentsCoverageLimit) }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.thirdPartyLiabilityLimit">
                                        <q-item-section avatar><q-icon name="gavel" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>3. Şahıs Mali Mesuliyet</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ formatCurrency(policy.thirdPartyLiabilityLimit) }}</q-item-label>
                                        </q-item-section>
                                    </q-item>
                                </template>

                                <!-- ─── SAĞLIK ÖZEL ALANLAR ─── -->
                                <template v-if="policy.type === 'SAGLIK'">
                                    <q-separator spaced inset />
                                    <q-item-label header class="text-primary text-weight-bold">Sigortalı / Plan Bilgileri</q-item-label>

                                    <q-item class="q-py-sm" v-if="policy.identityNumber">
                                        <q-item-section avatar><q-icon name="fingerprint" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>T.C. Kimlik No</q-item-label>
                                            <q-item-label class="text-weight-medium">{{ policy.identityNumber }}</q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.birthDate || policy.gender">
                                        <q-item-section avatar><q-icon name="cake" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Doğum Tarihi / Cinsiyet</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.birthDate ? formatDateOnly(policy.birthDate) : '-' }} ({{ policy.gender || '-' }})
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.healthPlanType || policy.networkTier">
                                        <q-item-section avatar><q-icon name="medical_services" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Plan / Network Ağı</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.healthPlanType || '-' }} - {{ policy.networkTier || '-' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.coverageScope">
                                        <q-item-section avatar><q-icon name="health_and_safety" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Kapsam</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.coverageScope }} 
                                                {{ policy.outpatientLimitCount ? `(${policy.outpatientLimitCount} Ayakta Limit)` : '' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>

                                    <q-item class="q-py-sm" v-if="policy.maternityCoverage !== undefined">
                                        <q-item-section avatar><q-icon name="pregnant_woman" color="primary" /></q-item-section>
                                        <q-item-section>
                                            <q-item-label caption>Doğum Teminatı</q-item-label>
                                            <q-item-label class="text-weight-medium">
                                                {{ policy.maternityCoverage ? 'Dahil' : 'Hariç' }}
                                            </q-item-label>
                                        </q-item-section>
                                    </q-item>
                                </template>

                                <q-separator spaced inset />

                                <!-- Notlar -->
                                <q-item class="q-py-sm">
                                    <q-item-section avatar><q-icon name="notes" color="primary" /></q-item-section>
                                    <q-item-section>
                                        <q-item-label caption>Not</q-item-label>
                                        <q-item-label class="text-weight-medium">{{ policy.note || '-' }}</q-item-label>
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
                                <div class="empty-state__description">Bu poliçeye ait henüz bir taksit tanımı yapılmamış.</div>
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

                                        <q-btn v-else flat color="negative" icon="undo"
                                            label="Geri Al" :loading="paymentLoadingNo === props.row.installmentNo"
                                            @click="handleRevertInstallment(props.row)" />
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
import { usePolicyDetail } from '../composables/usePolicyDetail';
import { usePolicyList } from '../composables/usePolicyList';
import { useInstallmentStore } from '../stores/installment';
import type { Installment } from '../types/installment.types';
import type { Policy } from '../types/policy.types';
import { useConfirmDialog } from '../composables/useConfirmDialog';
import { Notify } from 'quasar';
import EditPolicyModal from '../components/EditPolicyModal.vue';
import { formatDateOnly, formatCurrency, getStatusColor, getStatusLabel } from '../utils/policyHelper';

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


const handlePolicyUpdate = async (event: {
    id: string;
    data: Partial<Policy>;
    resolve?: () => void;
    reject?: (err: unknown) => void;
}) => {
    try {
        await updatePolicy(event.id, event.data);
        Notify.create({
            message: 'Poliçe başarıyla güncellendi.',
            color: 'positive',
            icon: 'check_circle',
            position: 'top-right',
            timeout: 4000
        });
        await loadAllData(); // güncel veriyi tekrar çek
        event.resolve?.();
    } catch (error) {
        console.error('Policy Update Error:', error);
        event.reject?.(error);
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

const handleRevertInstallment = async (installment: Installment) => {
    const isConfirmed = await confirm({
        title: 'Ödemeyi Geri Al',
        message: `${installment.installmentNo}. taksit için yapılan ödemeyi geri almak istediğinize emin misiniz?`,
        okLabel: 'Geri Al',
        cancelLabel: 'Vazgeç',
        color: 'negative'
    });

    if (!isConfirmed) return;

    paymentLoadingNo.value = installment.installmentNo;
    try {
        await setPaymentStatus(installment.id, installment.installmentNo, 'UNPAID');
        Notify.create({
            message: `${installment.installmentNo}. taksit ödemesi geri alındı.`,
            color: 'warning',
            icon: 'undo',
            position: 'top-right',
            timeout: 4000
        });
        await fetchInstallmentsOnly();
    } catch (error) {
        Notify.create({
            message: 'Ödeme geri alınırken bir hata oluştu.',
            color: 'negative',
            icon: 'error',
            position: 'top-right',
            timeout: 5000
        });
        console.error('Taksit geri alma hatası:', error);
    } finally {
        paymentLoadingNo.value = null;
    }
};

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

    await fetchInstallmentsOnly(page, rowsPerPage);

    pagination.value.page = page;
    pagination.value.rowsPerPage = rowsPerPage;
    pagination.value.rowsNumber = installmentsTotal.value;
};
onMounted(async () => {
    await loadAllData();

    if (policy.value?.responsibleUserId) {
        await getUser(policy.value.responsibleUserId);
    }

    pagination.value.rowsNumber = installmentsTotal.value;
});
</script>