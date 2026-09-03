<template>
    <q-dialog v-model="isOpen" persistent @show="onModalShow">
        <q-card class="modal-card">

            <!-- Modal Başlığı -->
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold text-grey-8">
                    Poliçe Düzenle
                </div>
                <q-space />
                <q-btn icon="close" flat round dense v-close-popup />
            </q-card-section>

            <q-separator class="q-my-sm" />

            <!-- Form Alanı -->
            <q-form @submit="onSubmit">
                <q-card-section class="q-gutter-md scroll" style="max-height: 72vh;">

                    <!-- ── Ortak Alanlar ──────────────────────────────────── -->

                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options label="Müşteri *" outlined dense disable
                        :error="!!fieldErrors.customerId" :error-message="fieldErrors.customerId"
                        @update:model-value="clearFieldError('customerId')"
                        :rules="[(val: unknown) => !!val || 'Müşteri seçimi zorunludur']" />

                    <!-- Sorumlu User -->
                    <q-select v-model="form.responsibleUserId" :options="filteredUserOptions" option-value="userId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterUserFn" label="Sorumlu Acente Personeli Seçin *" outlined
                        dense lazy-rules :error="!!fieldErrors.responsibleUserId"
                        :error-message="fieldErrors.responsibleUserId"
                        @update:model-value="clearFieldError('responsibleUserId')"
                        :rules="[(val: unknown) => !!val || 'Sorumlu personel seçimi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">
                                    Aradığınız kriterde (en az 2 karakter) personel bulunamadı.
                                </q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Poliçe Türü -->
                    <q-select v-model="form.type" :options="policyTypeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Poliçe Türü *" outlined dense disable
                        :error="!!fieldErrors.type" :error-message="fieldErrors.type"
                        @update:model-value="clearFieldError('type')"
                        :rules="[(val: unknown) => !!val || 'Poliçe türü zorunludur']" />

                    <!-- Sigorta Şirketi -->
                    <q-select v-model="form.company" :options="filteredInsuranceCompanyOptions" option-value="value"
                        option-label="label" emit-value map-options use-input fill-input hide-selected
                        input-debounce="200" @filter="filterInsuranceCompanyFn" label="Sigorta Şirketi *" outlined dense
                        :error="!!fieldErrors.company" :error-message="fieldErrors.company"
                        @update:model-value="clearFieldError('company')"
                        :rules="[(val: unknown) => !!val || 'Sigorta şirketi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">Eşleşen sigorta şirketi bulunamadı.</q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Aktiflik Durumu -->
                    <q-select v-model="form.isActive" :options="activeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Aktiflik Durumu *" outlined dense lazy-rules
                        :error="!!fieldErrors.isActive" :error-message="fieldErrors.isActive"
                        @update:model-value="clearFieldError('isActive')"
                        :rules="[(val: unknown) => (val !== null && val !== undefined) || 'Aktiflik durumu zorunludur']" />

                    <!-- Prim Tutarı -->
                    <q-input v-model.number="form.premium" type="number" label="Prim Tutarı (TL) *" outlined dense
                        prefix="₺" lazy-rules
                        :error="!!fieldErrors.premium" :error-message="fieldErrors.premium"
                        @update:model-value="clearFieldError('premium')" :rules="[
                            (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Prim tutarı zorunludur',
                            (val: number | string | null) => Number(val) > 0 || 'Prim tutarı 0\'dan büyük olmalıdır'
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
                                        :options="(date: string) => !form.startDate || date >= form.startDate.replace(/-/g, '/')"
                                        @update:model-value="closeDateProxy('endDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- Note -->
                    <q-input v-model="form.note" label="Not" outlined dense
                        :error="!!fieldErrors.note" :error-message="fieldErrors.note"
                        @update:model-value="clearFieldError('note')" />

                    <!-- ── Dinamik Poliçe Türü Alt Formu (OCP) ──────────────────── -->
                    <component
                        :is="policyFormComponent"
                        v-if="policyFormComponent"
                        v-model:fields="typeSpecificFields"
                        :field-errors="fieldErrors"
                        @clear-error="clearFieldError"
                    />

                </q-card-section>

                <q-separator />

                <q-card-actions align="right" class="q-pa-md">
                    <q-btn flat label="İptal" color="grey-7" v-close-popup :disable="loading" />
                    <q-btn type="submit" label="Güncelle" color="primary" :loading="loading" :disable="!hasChanges" />
                </q-card-actions>
            </q-form>
        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue';
import { QPopupProxy } from 'quasar';
import { usePolicyForm } from '../composables/usePolicyForm';
import { useUserStore } from '../stores/user';
import { useCustomerStore } from '../stores/customer';
import type { CustomerOption } from '../composables/usePolicyForm';
import {
    type Policy, type PolicyForm, policyTypeOptions, fillTypeFieldsFromPolicy,
} from '../types/policy.types';
import { formatDate } from '../utils/dateHelper';
import { ValidationError } from '../error/errors';

import TrafficPolicyForm from './policy-forms/TrafficPolicyForm.vue';
import CascoPolicyForm from './policy-forms/CascoPolicyForm.vue';
import DaskPolicyForm from './policy-forms/DaskPolicyForm.vue';
import HousePolicyForm from './policy-forms/HousePolicyForm.vue';
import HealthPolicyForm from './policy-forms/HealthPolicyForm.vue';

const userStore = useUserStore();
const customerStore = useCustomerStore();

interface Props {
    modelValue: boolean;
    policyData: Policy;
}

const activeOptions = [
    { value: true, label: 'Aktif' },
    { value: false, label: 'Pasif' }
];

const props = defineProps<Props>();

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (
        e: 'updated',
        event: {
            id: string;
            data: Record<string, unknown>;
            resolve: () => void;
            reject: (err: unknown) => void;
        }
    ): void;
}>();

const isOpen = computed({
    get: () => props.modelValue,
    set: (value: boolean) => emit('update:modelValue', value)
});

// ── Dinamik Bileşen Haritası (OCP) ───────────────────────────────────────────

const policyFormMap: Record<string, unknown> = {
    TRAFIK: TrafficPolicyForm,
    KASKO:  CascoPolicyForm,
    DASK:   DaskPolicyForm,
    KONUT:  HousePolicyForm,
    SAGLIK: HealthPolicyForm,
};

const {
    form,
    loading,
    typeSpecificFields,
    suppressTypeWatch,
    filteredUserOptions,
    filteredInsuranceCompanyOptions,
    filterUserFn,
    filterInsuranceCompanyFn,
    submitWithLoading,
} = usePolicyForm({ isRenewal: false, policyData: null });

const filteredCustomerOptions = ref<CustomerOption[]>([]);
const originalForm = ref<PolicyForm>({ ...form.value });
const originalTypeFields = ref<Record<string, unknown>>({});

const startDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const endDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);

const closeDateProxy = (type: 'startDate' | 'endDate') => {
    clearFieldError(type);
    if (type === 'startDate') startDateProxy.value?.hide();
    else endDateProxy.value?.hide();
};

const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};

// ── Dinamik bileşen seçici ────────────────────────────────────────────────────

const policyFormComponent = computed(() => policyFormMap[form.value.type] ?? null);

// ── Değişiklik takibi ─────────────────────────────────────────────────────────

const trackedFields: (keyof PolicyForm)[] = [
    'customerId', 'premium', 'note', 'responsibleUserId', 'startDate', 'endDate', 'isActive', 'company'
];

function assignIfChanged<K extends keyof PolicyForm>(
    target: Partial<PolicyForm>,
    key: K,
    current: PolicyForm,
    original: PolicyForm
) {
    if (current[key] !== original[key]) {
        target[key] = current[key];
    }
}

const getChangedTypeFields = (): Record<string, unknown> => {
    const current = typeSpecificFields.value as Record<string, unknown>;
    const original = originalTypeFields.value;
    const changed: Record<string, unknown> = {};
    for (const key of Object.keys(current)) {
        if (current[key] !== original[key]) changed[key] = current[key];
    }
    return changed;
};

const getChangedFields = (): Record<string, unknown> => {
    const changed: Record<string, unknown> = {};
    trackedFields.forEach((key) => {
        assignIfChanged(changed as Partial<PolicyForm>, key, form.value, originalForm.value);
    });
    Object.assign(changed, getChangedTypeFields());
    return changed;
};

const hasChanges = computed(() => Object.keys(getChangedFields()).length > 0);

// ── Modal lifecycle ───────────────────────────────────────────────────────────

const onModalShow = async () => {
    fieldErrors.value = {};
    loading.value = false;

    if (!customerStore.customerData?.length) await customerStore.fetchCustomerData();

    const responsibleUser = await userStore.fetchUserById(props.policyData.responsibleUserId);

    suppressTypeWatch.value = true;

    const resolveBooleanActive = (val: unknown): boolean => {
        if (val === true || val === 'ACTIVE') return true;
        if (val === false || val === 'PASSIVE') return false;
        return Boolean(val);
    };

    form.value = {
        customerId: props.policyData.customerId || '',
        type: props.policyData.type || '',
        premium: props.policyData.premium || 0,
        installment: props.policyData.installment || 0,
        responsibleUserId: props.policyData.responsibleUserId || '',
        note: props.policyData.note || '',
        startDate: props.policyData.startDate || '',
        endDate: props.policyData.endDate || '',
        isActive: resolveBooleanActive(props.policyData.isActive),
        company: props.policyData.company || '',
    };

    originalForm.value = { ...form.value };

    await nextTick();

    typeSpecificFields.value = fillTypeFieldsFromPolicy(props.policyData);
    originalTypeFields.value = { ...(typeSpecificFields.value as Record<string, unknown>) };

    suppressTypeWatch.value = false;

    const matchedCustomer = customerStore.customerData.find(
        (c) => String(c.customerId) === String(form.value.customerId)
    );
    filteredCustomerOptions.value = matchedCustomer
        ? [{
            customerId: String(matchedCustomer.customerId),
            identityNumber: matchedCustomer.identityNumber || '',
            fullName: `${matchedCustomer.fullName || ''}`.trim(),
        }]
        : [];

    filteredUserOptions.value = responsibleUser
        ? [{
            userId: responsibleUser.id,
            email: responsibleUser.email || '',
            fullName: `${(responsibleUser.fullName || '').trim()}${responsibleUser.email ? ` (${responsibleUser.email})` : ''}`,
        }]
        : [];
};

// ── Submit ────────────────────────────────────────────────────────────────────

const onSubmit = async () => {
    const changedFields = getChangedFields();

    if (Object.keys(changedFields).length === 0) {
        isOpen.value = false;
        return;
    }

    const patchData: Record<string, unknown> = {
        type: props.policyData.type,
        ...changedFields
    };

    fieldErrors.value = {};

    try {
        await submitWithLoading(async () => {
            await new Promise<void>((resolve, reject) => {
                emit('updated', {
                    id: props.policyData.policyId,
                    data: patchData,
                    resolve,
                    reject,
                });
            });
        });

        isOpen.value = false;
    } catch (error: unknown) {
        if (error instanceof ValidationError && error.errors) {
            fieldErrors.value = error.errors;
            return;
        } else if (error && typeof error === 'object' && error !== null && 'errors' in error) {
            fieldErrors.value = (error as { errors: Record<string, string> }).errors;
            return;
        }

        console.error('Poliçe güncelleme hatası:', error);
    }
};
</script>