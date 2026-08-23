<template>
    <q-dialog v-model="isOpen" @show="onModalShow">
        <q-card class="modal-card">

            <!-- Modal Title (dynamic based on mode) -->
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
                <q-card-section class="q-gutter-md">

                    <!-- Customer Select (locked in renewal mode) -->
                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterCustomerFn" label="Müşteri Ara (İsim, TC veya ID yazın) *"
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

                    <!-- Responsible Agent Employee -->
                    <q-select v-model="form.responsibleUserId" :options="filteredUserOptions" option-value="userId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterUserFn" label="Sorumlu Acente Personeli Seçin *" outlined
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

                    <!-- Policy Type (locked in renewal mode) -->
                    <q-select v-model="form.type" :options="policyTypeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Poliçe Türü *" outlined dense :disable="isRenewal"
                        :error="!!fieldErrors.type" :error-message="fieldErrors.type"
                        @update:model-value="clearFieldError('type')"
                        :rules="[val => !!val || 'Poliçe türü zorunludur']" />

                    <!-- Instalment Count -->
                    <q-select v-model="form.installment" :options="installmentOptions" label="Taksit Sayısı *" outlined
                        dense emit-value map-options
                        :error="!!fieldErrors.installment" :error-message="fieldErrors.installment"
                        @update:model-value="clearFieldError('installment')" :rules="[
                            val => (val !== null && val !== undefined) || 'Taksit Sayısı Zorunludur'
                        ]" />
                    <!-- Premium Amount -->
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
                                <q-popup-proxy ref="startDateProxy" cover transition-show="scale"
                                    transition-hide="scale">
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



                    <q-input v-model="form.note" label="Not giriniz" outlined dense
                        :error="!!fieldErrors.note" :error-message="fieldErrors.note"
                        @update:model-value="clearFieldError('note')" />

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
import { usePolicyForm } from '../composables/usePolicyForm';
import { installmentOptions } from '../types/installment.types'
import { formatDate } from '../utils/dateHelper';
import { ValidationError } from '../error/errors';

// ── Props & Emits ────────────────────────────────────────────────────────────

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

// ── v-model bridge ───────────────────────────────────────────────────────────

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value),
});

// ── Date-picker proxy refs (pure UI; NOT business logic) ─────────────────────

const startDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const endDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);

const closeDateProxy = (type: 'startDate' | 'endDate') => {
    if (type === 'startDate') startDateProxy.value?.hide();
    else endDateProxy.value?.hide();
};

// ── Field-level backend errors ────────────────────────────────────────────────

const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};


const {
    form,
    loading,
    filteredCustomerOptions,
    filteredUserOptions,
    customerStore,
    policyTypeOptions,
    onModalShow: onModalShowBase,
    filterCustomerFn,
    filterUserFn,
    buildSubmitPayload,
    submitWithLoading,
} = usePolicyForm(props);

const onModalShow = () => {
    fieldErrors.value = {}; 
    void onModalShowBase();
};

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