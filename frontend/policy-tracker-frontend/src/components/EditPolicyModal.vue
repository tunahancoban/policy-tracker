<template>
    <q-dialog v-model="isOpen" persistent @show="onModalShow">
        <q-card style="min-width: 450px; max-width: 600px;">

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

                    <!-- Müşteri Seçimi (Kilitli - Değiştirilemez) -->
                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options disable label="Müşteri" outlined dense />

                    <!-- Poliçe Türü -->
                    <q-select v-model="form.type" :options="policyTypeOptions" label="Poliçe Türü *" outlined dense
                        :rules="[val => !!val || 'Poliçe türü zorunludur']" />

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
                    <q-btn type="submit" label="Güncelle" color="primary" :loading="loading" />
                </q-card-actions>
            </q-form>
        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { policyTypeOptions, type Policy, type PolicyForm } from '../types/policy.types';

interface Props {
    modelValue: boolean;
    policyData: Policy;
}

interface CustomerOption {
    customerId: string;
    fullName: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'updated']);

const customerStore = useCustomerStore();
const loading = ref(false);

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
});

const form = ref<PolicyForm>({
    customerId: '',
    type: '',
    premium: 0,
    note: '',
    installmentNumber: 0,
    startDate: '',
    endDate: ''
});

const filteredCustomerOptions = computed<CustomerOption[]>(() => {
    const data = Array.isArray(customerStore.customerData) ? customerStore.customerData : [];
    const currentCustomer = data.find(c => c.customerId === props.policyData.customerId);

    if (!currentCustomer) return [];

    const namePart = `${currentCustomer.firstName || ''} ${currentCustomer.lastName || ''}`.trim() || currentCustomer.email || 'İsimsiz Müşteri';
    const identityPart = currentCustomer.identityNumber ? `(TC: ${currentCustomer.identityNumber})` : `(ID: ${currentCustomer.customerId})`;

    return [{
        customerId: currentCustomer.customerId,
        fullName: `${namePart} ${identityPart}`
    }];
});

const onModalShow = () => {
    form.value = {
        customerId: props.policyData.customerId || '',
        type: props.policyData.type || '',
        premium: props.policyData.premium || 0,
        installmentNumber: props.policyData.installmentNumber || 0,
        note: props.policyData.note || '',
        startDate: props.policyData.startDate ? props.policyData.startDate.slice(0, 10).replace(/-/g, '/') : '',
        endDate: props.policyData.endDate ? props.policyData.endDate.slice(0, 10).replace(/-/g, '/') : ''
    };
};
const onSubmit = () => {
    loading.value = true;
    try {
        // PATCH işlemi için sadece değişmesi beklenen/izin verilen alanları ayıklayıp emit ediyoruz
        const patchData = {
            type: form.value.type,
            premium: form.value.premium,
            startDate: form.value.startDate,
            endDate: form.value.endDate
        };

        emit('updated', {
            id: props.policyData.policyId,
            data: patchData
        });
        isOpen.value = false;
    } catch (error) {
        console.error('Poliçe güncellenirken hata oluştu:', error);
    } finally {
        loading.value = false;
    }
};
</script>