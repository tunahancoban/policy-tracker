<template>
    <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)">
        <q-card class="modal-card">
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6">{{ isEditMode ? 'Müşteri Güncelle' : 'Yeni Müşteri Ekle' }}</div>
                <q-space />
                <q-btn icon="close" flat round dense v-close-popup />
            </q-card-section>

            <q-form @submit.prevent="saveCustomer">
                <q-card-section class="q-gutter-sm">
                    <!-- Ad -->
                    <q-input v-model="form.fullName" label="Ad Soyad*" outlined dense :error="!!fieldErrors.fullName"
                        :error-message="fieldErrors.fullName" @update:model-value="clearFieldError('fullName')"
                        :rules="[
                            (val) => !!val?.trim() || 'Ad zorunludur',
                            (val) => val?.trim().length >= 2 || 'Ad en az 2 karakter olmalıdır',
                        ]" />

                    <!-- T.C. Kimlik No -->
                    <q-input v-model="form.identityNumber" label="T.C. Kimlik No *" outlined dense maxlength="11"
                        :error="!!fieldErrors.identityNumber" :error-message="fieldErrors.identityNumber"
                        @update:model-value="clearFieldError('identityNumber')" :rules="[
                            (val) => !!val || 'T.C. Kimlik No zorunludur',
                            (val) => /^[1-9][0-9]{10}$/.test(val) || 'Geçerli bir 11 haneli T.C. Kimlik No giriniz',
                        ]" />

                    <!-- Sistem Durumu -->
                    <q-select v-model="form.isActive" :options="stateOptions" label="Sistem Durumu" emit-value
                        map-options outlined dense />

                    <!-- E-posta -->
                    <q-input v-model="form.email" label="E-posta" outlined dense type="email"
                        :error="!!fieldErrors.email" :error-message="fieldErrors.email"
                        @update:model-value="clearFieldError('email')" :rules="[
                            (val) => !val || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) || 'Geçerli bir e-posta adresi giriniz',
                        ]" />

                    <!-- Telefon -->
                    <q-input v-model="form.phoneNumber" label="Telefon" outlined dense mask="(###) ### ## ##"
                        unmasked-value :error="!!fieldErrors.phoneNumber" :error-message="fieldErrors.phoneNumber"
                        @update:model-value="clearFieldError('phoneNumber')" :rules="[
                            (val) => !val || val.length === 10 || 'Telefon numarası 10 haneli olmalıdır',
                        ]" />

                    <!-- Şehir, İlçe, Adres -->
                    <q-input v-model="form.city" label="Şehir" outlined dense />
                    <q-input v-model="form.district" label="İlçe" outlined dense />
                    <q-input v-model="form.fullAddress" label="Adres" outlined dense type="textarea" rows="2" />
                </q-card-section>

                <q-card-actions align="right" class="text-primary q-pt-none">
                    <q-btn flat label="Vazgeç" v-close-popup color="grey" />
                    <q-btn flat :label="isEditMode ? 'Güncelle' : 'Kaydet'" color="primary" type="submit"
                        :loading="customerStore.isLoading" />
                </q-card-actions>
            </q-form>
        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { useQuasar } from 'quasar';
import type { Customer } from '../types/customer.types';
import { initialForm, stateOptions } from '../types/customer.types';
import { ValidationError } from '../error/errors';

const props = defineProps<{
    modelValue: boolean;
    customerData?: Customer | Partial<Customer> | undefined;
}>();

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (e: 'saved'): void;
}>();

const customerStore = useCustomerStore();
const $q = useQuasar();

const isEditMode = ref(false);
const form = ref({ ...initialForm });

const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};

watch(
    () => props.modelValue,
    (newVal) => {
        fieldErrors.value = {}; 
        if (newVal) {
            if (props.customerData && props.customerData.customerId) {
                isEditMode.value = true;
                form.value = {
                    ...initialForm,
                    ...(props.customerData as typeof initialForm),
                };
            } else {
                isEditMode.value = false;
                form.value = { ...initialForm };
            }
        }
    },
);

const saveCustomer = async () => {
    fieldErrors.value = {}; // Önceki hataları sıfırla

    try {
        if (isEditMode.value) {
            await customerStore.updateCustomer(form.value);
            $q.notify({
                message: 'Müşteri başarıyla güncellendi.',
                color: 'positive',
                icon: 'check_circle',
                position: 'top-right',
                timeout: 4000,
            });
        } else {
            await customerStore.addCustomer(form.value);
            $q.notify({
                message: 'Müşteri başarıyla eklendi.',
                color: 'positive',
                icon: 'check_circle',
                position: 'top-right',
                timeout: 4000,
            });
        }

        emit('update:modelValue', false);
        emit('saved');
    } catch (error: unknown) {
        if (error instanceof ValidationError && error.errors) {
            fieldErrors.value = error.errors;
            return;
        }

        console.error('Müşteri işlem hatası:', error);
    }
};
</script>