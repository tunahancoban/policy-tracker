<template>
    <q-dialog v-model="isOpen" @show="onModalShow">
        <q-card class="modal-card">

            <!-- Modal Title -->
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold text-grey-8">
                    {{ isRenewal ? 'Poliçeyi Yenile' : 'Yeni Poliçe Ekle' }}
                </div>
                <q-space />
                <q-btn icon="close" flat round dense v-close-popup />
            </q-card-section>

            <q-separator class="q-my-sm" />

            <!-- Form -->
            <q-form @submit="onSubmit">
                <q-card-section class="q-gutter-md scroll" style="max-height: 72vh;">

                    <!-- Customer Select -->
                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterCustomerFn" label="Müşteri Ara *"
                        outlined dense :disable="isRenewal" :loading="customerStore.isLoading"
                        :error="!!fieldErrors.customerId" :error-message="fieldErrors.customerId"
                        @update:model-value="clearFieldError('customerId')"
                        :rules="[val => !!val || 'Müşteri seçimi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">
                                    Aradığınız kriterde (en az 2 karakter) müşteri bulunamadı.
                                </q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Responsible User -->
                    <q-select v-model="form.responsibleUserId" :options="filteredUserOptions" option-value="userId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterUserFn" label="Sorumlu Acente Personeli *" outlined
                        dense :error="!!fieldErrors.responsibleUserId" :error-message="fieldErrors.responsibleUserId"
                        @update:model-value="clearFieldError('responsibleUserId')"
                        :rules="[val => !!val || 'Sorumlu personel seçimi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">
                                    Aradığınız kriterde (en az 2 karakter) personel bulunamadı.
                                </q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Policy Type -->
                    <q-select v-model="form.type" :options="policyTypeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Poliçe Türü *" outlined dense :disable="isRenewal"
                        :error="!!fieldErrors.type" :error-message="fieldErrors.type"
                        @update:model-value="clearFieldError('type')"
                        :rules="[val => !!val || 'Poliçe türü zorunludur']" />

                    <!-- Sigorta Şirketi -->
                    <q-select v-model="form.company" :options="filteredInsuranceCompanyOptions" option-value="value"
                        option-label="label" emit-value map-options use-input fill-input hide-selected
                        input-debounce="200" @filter="filterInsuranceCompanyFn" label="Sigorta Şirketi *" outlined dense
                        :error="!!fieldErrors.insuranceCompany" :error-message="fieldErrors.insuranceCompany"
                        @update:model-value="clearFieldError('insuranceCompany')"
                        :rules="[val => !!val || 'Sigorta şirketi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">Eşleşen sigorta şirketi bulunamadı.</q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Installment -->
                    <q-select v-model="form.installment" :options="installmentOptions" label="Taksit Sayısı *" outlined
                        dense emit-value map-options
                        :error="!!fieldErrors.installment" :error-message="fieldErrors.installment"
                        @update:model-value="clearFieldError('installment')" :rules="[
                            val => (val !== null && val !== undefined) || 'Taksit Sayısı Zorunludur'
                        ]" />

                    <!-- Premium -->
                    <q-input v-model.number="form.premium" type="number" label="Prim Tutarı (TL) *" outlined dense
                        prefix="₺"
                        :error="!!fieldErrors.premium" :error-message="fieldErrors.premium"
                        @update:model-value="clearFieldError('premium')" :rules="[
                            val => (val !== null && val !== undefined) || 'Prim tutarı zorunludur',
                            val => val > 0 || 'Prim tutarı 0\'dan büyük olmalıdır'
                        ]" />

                    <!-- Start Date -->
                    <q-input :model-value="formatDate(form.startDate)" label="Başlangıç Tarihi *" outlined dense
                        stack-label readonly lazy-rules
                        :error="!!fieldErrors.startDate" :error-message="fieldErrors.startDate" :rules="[
                            () => !!form.startDate || 'Başlangıç tarihi zorunludur'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy ref="startDateProxy" cover transition-show="scale" transition-hide="scale">
                                    <q-date v-model="form.startDate" mask="YYYY-MM-DD"
                                        @update:model-value="closeDateProxy('startDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- End Date -->
                    <q-input :model-value="formatDate(form.endDate)" label="Bitiş Tarihi *" outlined dense stack-label
                        readonly lazy-rules
                        :error="!!fieldErrors.endDate" :error-message="fieldErrors.endDate" :rules="[
                            () => !!form.endDate || 'Bitiş tarihi zorunludur',
                            () => !form.startDate || !form.endDate || form.endDate >= form.startDate || 'Bitiş tarihi başlangıç tarihinden önce olamaz'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy ref="endDateProxy" cover transition-show="scale" transition-hide="scale">
                                    <q-date v-model="form.endDate" mask="YYYY-MM-DD"
                                        :options="date => !form.startDate || date >= form.startDate.replace(/-/g, '/')"
                                        @update:model-value="closeDateProxy('endDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- Note -->
                    <q-input v-model="form.note" label="Not" outlined dense
                        :error="!!fieldErrors.note" :error-message="fieldErrors.note"
                        @update:model-value="clearFieldError('note')" />

                    <!-- ── Trafik Sigortası Alanları ───────────────────────── -->
                    <template v-if="form.type === 'TRAFIK'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="directions_car" class="q-mr-xs" />Trafik Sigortası
                        </div>
                        <q-input v-model="trafficFields.plateNumber" label="Plaka *" outlined dense
                            :error="!!fieldErrors.plateNumber" :error-message="fieldErrors.plateNumber"
                            @update:model-value="clearFieldError('plateNumber')"
                            :rules="[val => !!val || 'Plaka zorunludur']" />
                        <q-input v-model="trafficFields.chassisNumber" label="Şasi Numarası" outlined dense
                            :error="!!fieldErrors.chassisNumber" :error-message="fieldErrors.chassisNumber"
                            @update:model-value="clearFieldError('chassisNumber')" />
                        <q-input v-model="trafficFields.engineNumber" label="Motor Numarası" outlined dense
                            :error="!!fieldErrors.engineNumber" :error-message="fieldErrors.engineNumber"
                            @update:model-value="clearFieldError('engineNumber')" />
                        <q-select v-model="trafficFields.vehicleUsageType" :options="vehicleUsageTypeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="Araç Kullanım Tipi" outlined dense
                            :error="!!fieldErrors.vehicleUsageType" :error-message="fieldErrors.vehicleUsageType"
                            @update:model-value="clearFieldError('vehicleUsageType')" />
                        <q-input v-model.number="trafficFields.noClaimDiscountStep" type="number"
                            label="Hasarsızlık Basamağı" outlined dense
                            :error="!!fieldErrors.noClaimDiscountStep" :error-message="fieldErrors.noClaimDiscountStep"
                            @update:model-value="clearFieldError('noClaimDiscountStep')" />
                            <q-toggle v-model="trafficFields.hasImm" label="İMM Teminatı Var" />
                            <q-input v-if="trafficFields.hasImm" v-model.number="trafficFields.immLimit"
                                type="number" label="İMM Limiti (TL)" outlined dense prefix="₺" class="col"
                                :error="!!fieldErrors.immLimit" :error-message="fieldErrors.immLimit"
                                @update:model-value="clearFieldError('immLimit')" />
                    </template>

                    <!-- ── Kasko Sigortası Alanları ────────────────────────── -->
                    <template v-if="form.type === 'KASKO'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="car_repair" class="q-mr-xs" />Kasko Sigortası Alanları
                        </div>
                            <q-input v-model="cascoFields.plateNumber" label="Plaka *" outlined dense class="col"
                                :error="!!fieldErrors.plateNumber" :error-message="fieldErrors.plateNumber"
                                @update:model-value="clearFieldError('plateNumber')"
                                :rules="[val => !!val || 'Plaka zorunludur']" />
                            <q-input v-model="cascoFields.chassisNumber" label="Şasi No" outlined dense class="col"
                                :error="!!fieldErrors.chassisNumber" :error-message="fieldErrors.chassisNumber"
                                @update:model-value="clearFieldError('chassisNumber')" />
                            <q-input v-model="cascoFields.vehicleBrand" label="Araç Markası *" outlined dense class="col"
                                :error="!!fieldErrors.vehicleBrand" :error-message="fieldErrors.vehicleBrand"
                                @update:model-value="clearFieldError('vehicleBrand')"
                                :rules="[val => !!val || 'Marka zorunludur']" />
                            <q-input v-model="cascoFields.vehicleModel" label="Araç Modeli *" outlined dense class="col"
                                :error="!!fieldErrors.vehicleModel" :error-message="fieldErrors.vehicleModel"
                                @update:model-value="clearFieldError('vehicleModel')"
                                :rules="[val => !!val || 'Model zorunludur']" />
                            <q-input v-model.number="cascoFields.modelYear" type="number" label="Model Yılı *"
                                outlined dense class="col"
                                :error="!!fieldErrors.modelYear" :error-message="fieldErrors.modelYear"
                                @update:model-value="clearFieldError('modelYear')"
                                :rules="[val => !!val || 'Model yılı zorunludur']" />
                            <q-input v-model.number="cascoFields.vehicleValue" type="number" label="Araç Değeri (TL)"
                                outlined dense class="col" prefix="₺"
                                :error="!!fieldErrors.vehicleValue" :error-message="fieldErrors.vehicleValue"
                                @update:model-value="clearFieldError('vehicleValue')" />
                        <q-select v-model="cascoFields.cascoType" :options="cascoTypeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="Kasko Tipi" outlined dense
                            :error="!!fieldErrors.cascoType" :error-message="fieldErrors.cascoType"
                            @update:model-value="clearFieldError('cascoType')" />
                            <q-toggle v-model="cascoFields.hasReplacementCar" label="İkame Araç" />
                            <q-input v-if="cascoFields.hasReplacementCar" v-model.number="cascoFields.replacementCarDays"
                                type="number" label="İkame Araç Gün Sayısı" outlined dense class="col"
                                :error="!!fieldErrors.replacementCarDays" :error-message="fieldErrors.replacementCarDays"
                                @update:model-value="clearFieldError('replacementCarDays')" />
                            <q-toggle v-model="cascoFields.authorizedServiceOnly" label="Yetkili Servis" />
                            <q-toggle v-model="cascoFields.glassExemption" label="Cam Muafiyeti" />
                    </template>

                    <!-- ── DASK Alanları ───────────────────────────────────── -->
                    <template v-if="form.type === 'DASK'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="domain" class="q-mr-xs" />DASK (Afet Sigortası) Alanları
                        </div>
                        <q-input v-model="daskFields.uavtCode" label="UAVT Kodu *" outlined dense
                            :error="!!fieldErrors.uavtCode" :error-message="fieldErrors.uavtCode"
                            @update:model-value="clearFieldError('uavtCode')"
                            :rules="[val => !!val || 'UAVT kodu zorunludur']" />
                        <q-input v-model.number="daskFields.grossSquareMeters" type="number"
                                label="Brüt Alan (m²)" outlined dense class="col"
                                :error="!!fieldErrors.grossSquareMeters" :error-message="fieldErrors.grossSquareMeters"
                                @update:model-value="clearFieldError('grossSquareMeters')" />
                        <q-input v-model.number="daskFields.buildingConstructionYear" type="number"
                                label="İnşaat Yılı" outlined dense class="col"
                                :error="!!fieldErrors.buildingConstructionYear" :error-message="fieldErrors.buildingConstructionYear"
                                @update:model-value="clearFieldError('buildingConstructionYear')" />

                        <q-select v-model="daskFields.buildingConstructionType" :options="buildingConstructionTypeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="Yapı Tipi" outlined dense
                            :error="!!fieldErrors.buildingConstructionType" :error-message="fieldErrors.buildingConstructionType"
                            @update:model-value="clearFieldError('buildingConstructionType')" />
                            <q-input v-model.number="daskFields.totalFloorCount" type="number"
                                label="Toplam Kat Sayısı" outlined dense class="col"
                                :error="!!fieldErrors.totalFloorCount" :error-message="fieldErrors.totalFloorCount"
                                @update:model-value="clearFieldError('totalFloorCount')" />
                            <q-input v-model.number="daskFields.apartmentFloor" type="number"
                                label="Dairenin Bulunduğu Kat" outlined dense class="col"
                                :error="!!fieldErrors.apartmentFloor" :error-message="fieldErrors.apartmentFloor"
                                @update:model-value="clearFieldError('apartmentFloor')" />
                        <q-input v-model.number="daskFields.earthquakeZone" type="number"
                            label="Deprem Bölgesi (1-5)" outlined dense
                            :error="!!fieldErrors.earthquakeZone" :error-message="fieldErrors.earthquakeZone"
                            @update:model-value="clearFieldError('earthquakeZone')"
                            :rules="[val => !val || (val >= 1 && val <= 5) || 'Deprem bölgesi 1-5 arasında olmalıdır']" />
                    </template>

                    <!-- ── Konut Sigortası Alanları ───────────────────────── -->
                    <template v-if="form.type === 'KONUT'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="home" class="q-mr-xs" />Konut Sigortası Alanları
                        </div>
                        <q-input v-model="houseFields.uavtCode" label="UAVT Kodu *" outlined dense
                            :error="!!fieldErrors.uavtCode" :error-message="fieldErrors.uavtCode"
                            @update:model-value="clearFieldError('uavtCode')"
                            :rules="[val => !!val || 'UAVT kodu zorunludur']" />
                        <q-select v-model="houseFields.residenceType" :options="residenceTypeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="İkamet Tipi" outlined dense
                            :error="!!fieldErrors.residenceType" :error-message="fieldErrors.residenceType"
                            @update:model-value="clearFieldError('residenceType')" />
                        <q-input v-model.number="houseFields.buildingCoverageLimit" type="number"
                                label="Bina Teminat Bedeli (TL)" outlined dense class="col" prefix="₺"
                                :error="!!fieldErrors.buildingCoverageLimit" :error-message="fieldErrors.buildingCoverageLimit"
                                @update:model-value="clearFieldError('buildingCoverageLimit')" />
                        <q-input v-model.number="houseFields.contentsCoverageLimit" type="number"
                                label="Eşya Teminat Bedeli (TL)" outlined dense class="col" prefix="₺"
                                :error="!!fieldErrors.contentsCoverageLimit" :error-message="fieldErrors.contentsCoverageLimit"
                                @update:model-value="clearFieldError('contentsCoverageLimit')" />
                        <q-input v-model.number="houseFields.thirdPartyLiabilityLimit" type="number"
                            label="Komşuluk Sorumluluk Limiti (TL)" outlined dense prefix="₺"
                            :error="!!fieldErrors.thirdPartyLiabilityLimit" :error-message="fieldErrors.thirdPartyLiabilityLimit"
                            @update:model-value="clearFieldError('thirdPartyLiabilityLimit')" />
                        <div class="row q-gutter-md">
                            <q-toggle v-model="houseFields.theftCoverage" label="Hırsızlık Teminatı" />
                            <q-toggle v-model="houseFields.waterDamageCoverage" label="Su Hasarı Teminatı" />
                            <q-toggle v-model="houseFields.glassBreakageCoverage" label="Cam Kırılması" />
                        </div>
                    </template>

                    <!-- ── Sağlık Sigortası Alanları ──────────────────────── -->
                    <template v-if="form.type === 'SAGLIK'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="health_and_safety" class="q-mr-xs" />Sağlık Sigortası Alanları
                        </div>
                            <q-input v-model="healthFields.identityNumber" label="TC Kimlik No" outlined dense class="col"
                                :error="!!fieldErrors.identityNumber" :error-message="fieldErrors.identityNumber"
                                @update:model-value="clearFieldError('identityNumber')" />
                            <!-- Birth Date (date-picker, aynı format Başlangıç/Bitiş tarihi ile) -->
                            <q-input :model-value="formatDate(healthFields.birthDate)" label="Doğum Tarihi" outlined dense
                                stack-label readonly class="col"
                                :error="!!fieldErrors.birthDate" :error-message="fieldErrors.birthDate">
                                <template v-slot:append>
                                    <q-icon name="event" class="cursor-pointer">
                                        <q-popup-proxy ref="birthDateProxy" cover transition-show="scale" transition-hide="scale">
                                            <q-date v-model="healthFields.birthDate" mask="YYYY-MM-DD"
                                                @update:model-value="closeDateProxy('birthDate')" />
                                        </q-popup-proxy>
                                    </q-icon>
                                </template>
                            </q-input>
                            <q-select v-model="healthFields.gender" :options="genderOptions"
                                option-value="value" option-label="label" emit-value map-options
                                label="Cinsiyet" outlined dense class="col"
                                :error="!!fieldErrors.gender" :error-message="fieldErrors.gender"
                                @update:model-value="clearFieldError('gender')" />
                            <q-select v-model="healthFields.healthPlanType" :options="healthPlanTypeOptions"
                                option-value="value" option-label="label" emit-value map-options
                                label="Plan Tipi" outlined dense class="col"
                                :error="!!fieldErrors.healthPlanType" :error-message="fieldErrors.healthPlanType"
                                @update:model-value="clearFieldError('healthPlanType')" />
                        <q-select v-model="healthFields.coverageScope" :options="coverageScopeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="Kapsam" outlined dense
                            :error="!!fieldErrors.coverageScope" :error-message="fieldErrors.coverageScope"
                            @update:model-value="clearFieldError('coverageScope')" />
                            <q-input v-model.number="healthFields.outpatientLimitCount" type="number"
                                label="Yıllık Ayakta Muayene Adedi" outlined dense class="col"
                                :error="!!fieldErrors.outpatientLimitCount" :error-message="fieldErrors.outpatientLimitCount"
                                @update:model-value="clearFieldError('outpatientLimitCount')" />
                            <q-select v-model="healthFields.networkTier" :options="networkTierOptions"
                                option-value="value" option-label="label" emit-value map-options
                                label="Network" outlined dense class="col"
                                :error="!!fieldErrors.networkTier" :error-message="fieldErrors.networkTier"
                                @update:model-value="clearFieldError('networkTier')" />
                        <q-toggle v-model="healthFields.maternityCoverage" label="Doğum Teminatı" />
                    </template>

                </q-card-section>

                <q-separator />

                <!-- Action Buttons -->
                <q-card-actions align="right" class="q-pa-md">
                    <q-btn flat label="İptal" color="grey-7" v-close-popup :disable="loading" />
                    <q-btn type="submit" :label="isRenewal ? 'Poliçeyi Yenile' : 'Kaydet'"
                        :color="isRenewal ? 'secondary' : 'primary'" :loading="loading" />
                </q-card-actions>
            </q-form>

        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">

import { ref, computed } from 'vue';
import { QPopupProxy } from 'quasar';
import { type Policy, type CreatePolicyRequest, type RenewPolicyRequest } from '../types/policy.types';
import {
    type TrafficFields, type CascoFields, type DaskFields, type HouseFields, type HealthFields,
    vehicleUsageTypeOptions, cascoTypeOptions, buildingConstructionTypeOptions,
    residenceTypeOptions, genderOptions, healthPlanTypeOptions, coverageScopeOptions, networkTierOptions,
} from '../types/policy.types';
import { usePolicyForm } from '../composables/usePolicyForm';
import { installmentOptions } from '../types/installment.types';
import { formatDate } from '../utils/dateHelper';
import { ValidationError } from '../error/errors';

// ── Props & Emits ─────────────────────────────────────────────────────────────

interface Props {
    modelValue: boolean;
    isRenewal?: boolean;
    policyData?: Policy | null;
}

const props = withDefaults(defineProps<Props>(), {
    isRenewal: false,
    policyData: null,
});

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (e: 'created', payload: CreatePolicyRequest): void;
    (e: 'renewed', payload: RenewPolicyRequest): void;
}>();

// ── v-model bridge ────────────────────────────────────────────────────────────

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value),
});

// ── Date-picker proxy refs ────────────────────────────────────────────────────

const startDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const endDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const birthDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);

const closeDateProxy = (type: 'startDate' | 'endDate' | 'birthDate') => {
    if (type === 'startDate') startDateProxy.value?.hide();
    else if (type === 'endDate') endDateProxy.value?.hide();
    else birthDateProxy.value?.hide();
};

// ── Field-level errors ────────────────────────────────────────────────────────

const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};

// ── Composable ────────────────────────────────────────────────────────────────

const {
    form,
    loading,
    typeSpecificFields,
    filteredCustomerOptions,
    filteredUserOptions,
    filteredInsuranceCompanyOptions,
    customerStore,
    policyTypeOptions,
    onModalShow: onModalShowBase,
    filterCustomerFn,
    filterUserFn,
    filterInsuranceCompanyFn,
    buildSubmitPayload,
    submitWithLoading,
} = usePolicyForm(props);

// ── Type-specific field proxies ──────────────────────────────────────────────

function asType<T>(val: unknown): T { return val as T; }

const trafficFields = computed(() => asType<TrafficFields>(typeSpecificFields.value));
const cascoFields   = computed(() => asType<CascoFields>(typeSpecificFields.value));
const daskFields    = computed(() => asType<DaskFields>(typeSpecificFields.value));
const houseFields   = computed(() => asType<HouseFields>(typeSpecificFields.value));
const healthFields  = computed(() => asType<HealthFields>(typeSpecificFields.value));

// ── Modal lifecycle ───────────────────────────────────────────────────────────

const onModalShow = () => {
    fieldErrors.value = {};
    void onModalShowBase();
};

// ── Submit ────────────────────────────────────────────────────────────────────

const onSubmit = async () => {
    fieldErrors.value = {};

    try {
        await submitWithLoading(() => {
            const result = buildSubmitPayload();
            if (!result) return Promise.resolve();

            if (result.mode === 'renewed') {
                emit('renewed', result.payload);
            } else {
                emit('created', result.payload);
            }

            isOpen.value = false;
            return Promise.resolve();
        });
    } catch (error: unknown) {
        if (error instanceof ValidationError && error.errors) {
            fieldErrors.value = error.errors;
            return;
        }

        console.error('Poliçe kaydetme hatası:', error);
    }
};
</script>