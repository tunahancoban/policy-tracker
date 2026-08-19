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

                    <!-- Müşteri Seçimi  -->
                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options label="Müşteri *" outlined dense disable />

                    <!-- Sorumlu User-->
                    <q-select v-model="form.responsibleUserId" :options="filteredUserOptions" option-value="userId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterUserFn" label="Sorumlu Acente Personeli Seçin *" outlined
                        dense :rules="[val => !!val || 'Sorumlu personel seçimi zorunludur']">
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
                        emit-value map-options label="Poliçe Türü *" outlined dense disable />

                    <!-- Aktiflik Durumu -->
                    <q-select v-model="form.active" :options="activeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Aktiflik Durumu *" outlined dense
                        :rules="[val => !!val || 'Aktiflik durumu zorunludur']" />

                    <!-- Prim Tutarı -->
                    <q-input v-model.number="form.premium" type="number" label="Prim Tutarı (TL) *" outlined dense
                        prefix="₺" :rules="[
                            val => val !== null && val !== undefined || 'Prim tutarı zorunludur',
                            val => val > 0 || 'Prim tutarı 0\'dan büyük olmalıdır'
                        ]" />

                    <!-- Tarih Alanları -->
                    <q-input v-model="form.startDate" label="Başlangıç Tarihi *" outlined dense stack-label
                        mask="####/##/##">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy cover transition-show="scale" transition-hide="scale" no-parent-close>
                                    <q-date v-model="form.startDate" mask="YYYY/MM/DD" v-close-popup />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- Bitiş Tarihi -->
                    <q-input v-model="form.endDate" label="Bitiş Tarihi *" outlined dense stack-label mask="####/##/##">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy cover transition-show="scale" transition-hide="scale" no-parent-close>
                                    <q-date v-model="form.endDate" mask="YYYY/MM/DD" v-close-popup />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <q-input v-model="form.note" label="Not giriniz" outlined dense />

                </q-card-section>

                <q-separator />

                <!-- Aksiyon Butonları -->
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

// Yalnızca form state, loading ve submit yönetimi için composable kullanılıyor.
// "create" moduna özel onModalShow override'ı yapılmıyor.
const {
    form,
    loading,
    filteredUserOptions,
    filterUserFn,
    submitWithLoading,
} = usePolicyForm({ isRenewal: false, policyData: null });

// EditPolicyModal'ın kendi customer seçenek listesi (sadece mevcut müşteri)
const filteredCustomerOptions = ref<CustomerOption[]>([]);

const originalForm = ref<PolicyForm>({ ...form.value });

const trackedFields: (keyof PolicyForm)[] = [
    'customerId', 'type', 'premium', 'note', 'responsibleUserId', 'startDate', 'endDate', 'active'
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

// ── Modal açılışında formu mevcut poliçe verisiyle doldur ─────────────────────
// baseOnModalShow yerine doğrudan customer/user store kullanılıyor.
const onModalShow = async () => {
    // Müşteri listesi henüz yüklenmediyse çek (edit'te filtreleme için gerekli)
    if (!customerStore.customerData?.length) await customerStore.fetchCustomerData();

    // Sorumlu kullanıcıyı ID ile getir
    const responsibleUser = await userStore.fetchUserById(props.policyData.responsibleUserId);

    form.value = {
        customerId: props.policyData.customerId || '',
        type: props.policyData.type || '',
        premium: props.policyData.premium || 0,
        installment: props.policyData.installment || 0,
        responsibleUserId: props.policyData.responsibleUserId || '',
        note: props.policyData.note || '',
        startDate: props.policyData.startDate
            ? props.policyData.startDate.slice(0, 10).replace(/-/g, '/')
            : '',
        endDate: props.policyData.endDate
            ? props.policyData.endDate.slice(0, 10).replace(/-/g, '/')
            : '',
        active: props.policyData.active || ''
    };

    originalForm.value = { ...form.value };

    // Sadece bu müşteri seçenek olarak gösterilsin (değiştirilemiyor)
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

    // Sorumlu kullanıcı drop-down'ı
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

    await submitWithLoading(async () => {
        await emit('updated', {
            id: props.policyData.policyId,
            data: patchData
        });
        isOpen.value = false;
    });
};
</script>