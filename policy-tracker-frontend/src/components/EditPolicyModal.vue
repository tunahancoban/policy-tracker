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
                <q-card-section class="q-gutter-md">

                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options label="Müşteri *" outlined dense disable
                        :error="!!fieldErrors.customerId" :error-message="fieldErrors.customerId"
                        :rules="[val => !!val || 'Müşteri seçimi zorunludur']" />

                    <!-- Sorumlu User -->
                    <q-select v-model="form.responsibleUserId" :options="filteredUserOptions" option-value="userId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterUserFn" label="Sorumlu Acente Personeli Seçin *" outlined
                        dense lazy-rules :error="!!fieldErrors.responsibleUserId"
                        :error-message="fieldErrors.responsibleUserId"
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

                    <!-- Poliçe Türü -->
                    <q-select v-model="form.type" :options="policyTypeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Poliçe Türü *" outlined dense disable
                        :error="!!fieldErrors.type" :error-message="fieldErrors.type"
                        :rules="[val => !!val || 'Poliçe türü zorunludur']" />

                    <!-- Aktiflik Durumu (Boolean değerler için val !== null kontrolü) -->
                    <q-select v-model="form.isActive" :options="activeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Aktiflik Durumu *" outlined dense lazy-rules
                        :error="!!fieldErrors.isActive" :error-message="fieldErrors.isActive"
                        @update:model-value="clearFieldError('isActive')"
                        :rules="[val => val !== null && val !== undefined || 'Aktiflik durumu zorunludur']" />

                    <!-- Prim Tutarı -->
                    <q-input v-model.number="form.premium" type="number" label="Prim Tutarı (TL) *" outlined dense
                        prefix="₺" lazy-rules
                        :error="!!fieldErrors.premium" :error-message="fieldErrors.premium"
                        @update:model-value="clearFieldError('premium')" :rules="[
                            val => (val !== null && val !== undefined && val !== '') || 'Prim tutarı zorunludur',
                            val => Number(val) > 0 || 'Prim tutarı 0\'dan büyük olmalıdır'
                        ]" />
                    <!-- Start Date -->
                    <q-input :model-value="formatDate(form.startDate)" label="Başlangıç Tarihi *" outlined dense
                        stack-label readonly lazy-rules
                        :error="!!fieldErrors.startDate" :error-message="fieldErrors.startDate" :rules="[
                            () => !!form.startDate || 'Başlangıç tarihi zorunludur'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                                    <q-date v-model="form.startDate" mask="YYYY-MM-DD" v-close-popup
                                        @update:model-value="clearFieldError('startDate')" />
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
                                <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                                    <q-date v-model="form.endDate" mask="YYYY-MM-DD"
                                        :options="date => !form.startDate || date >= form.startDate.replace(/-/g, '/')"
                                        v-close-popup
                                        @update:model-value="clearFieldError('endDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>



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
import { ref, computed } from 'vue';
import { usePolicyForm } from '../composables/usePolicyForm';
import { useUserStore } from '../stores/user';
import { useCustomerStore } from '../stores/customer';
import type { CustomerOption } from '../composables/usePolicyForm';
import { type Policy, type PolicyForm, policyTypeOptions } from '../types/policy.types';
import { formatDate } from '../utils/dateHelper';
import { ValidationError } from '../error/errors';

const userStore = useUserStore();
const customerStore = useCustomerStore();

interface Props {
    modelValue: boolean;
    policyData: Policy;
}

const activeOptions = [
    { value: 'ACTIVE', label: 'Aktif' },
    { value: 'PASSIVE', label: 'Pasif' }
];

const props = defineProps<Props>();
const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (e: 'updated', payload: { id: string; data: Partial<PolicyForm> }): Promise<void> | void;
}>();

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
});


const {
    form,
    loading,
    filteredUserOptions,
    filterUserFn,
    submitWithLoading,
} = usePolicyForm({ isRenewal: false, policyData: null });

const filteredCustomerOptions = ref<CustomerOption[]>([]);

const originalForm = ref<PolicyForm>({ ...form.value });

// Backend'den dönen alan bazlı hatalar
const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};

const trackedFields: (keyof PolicyForm)[] = [
    'customerId', 'type', 'premium', 'note', 'responsibleUserId', 'startDate', 'endDate', 'isActive'
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

const getChangedFields = (): Partial<PolicyForm> => {
    const changed: Partial<PolicyForm> = {};
    trackedFields.forEach((key) => {
        assignIfChanged(changed, key, form.value, originalForm.value);
    });
    return changed;
};

const hasChanges = computed(() => Object.keys(getChangedFields()).length > 0);


const onModalShow = async () => {
    fieldErrors.value = {}; // Hataları temizle

    if (!customerStore.customerData?.length) await customerStore.fetchCustomerData();

    const responsibleUser = await userStore.fetchUserById(props.policyData.responsibleUserId);

    form.value = {
        customerId: props.policyData.customerId || '',
        type: props.policyData.type || '',
        premium: props.policyData.premium || 0,
        installment: props.policyData.installment || 0,
        responsibleUserId: props.policyData.responsibleUserId || '',
        note: props.policyData.note || '',
        startDate: props.policyData.startDate
            ? props.policyData.startDate
            : '',
        endDate: props.policyData.endDate
            ? props.policyData.endDate
            : '',
        isActive: props.policyData.isActive || ''
    };

    originalForm.value = { ...form.value };

    const matchedCustomer = customerStore.customerData.find(
        (c) => c.customerId === form.value.customerId
    );
    filteredCustomerOptions.value = matchedCustomer
        ? [{
            customerId: matchedCustomer.customerId,
            identityNumber: matchedCustomer.identityNumber || '',
            fullName: `${matchedCustomer.firstName || ''} ${matchedCustomer.lastName || ''}`.trim(),
        }]
        : [];

    filteredUserOptions.value = responsibleUser
        ? [{
            userId: responsibleUser.id,
            email: responsibleUser.email || '',
            fullName: `${responsibleUser.firstName || ''} ${responsibleUser.lastName || ''}`.trim()
                + (responsibleUser.email ? ` (${responsibleUser.email})` : ''),
        }]
        : [];
};

const onSubmit = async () => {
    const patchData = getChangedFields();

    if (Object.keys(patchData).length === 0) {
        isOpen.value = false;
        return;
    }

    fieldErrors.value = {}; 

    try {
        await submitWithLoading(async () => {
            await emit('updated', {
                id: props.policyData.policyId,
                data: patchData
            });
            isOpen.value = false;
        });
    } catch (error: unknown) {
        if (error instanceof ValidationError && error.errors) {
            fieldErrors.value = error.errors;
            return; 
        }

        console.error('Poliçe güncelleme hatası:', error);
    }
};
</script>