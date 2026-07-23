<template>
    <q-dialog v-model="isOpen" @show="onModalShow">
        <q-card style="min-width: 450px; max-width: 600px;">

            <!-- Modal Başlığı -->
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold text-grey-8">
                    Yeni Poliçe Ekle
                </div>
                <q-space />
                <q-btn icon="close" flat round dense v-close-popup />
            </q-card-section>

            <q-separator class="q-my-sm" />

            <!-- Form Alanı -->
            <q-form @submit="onSubmit">
                <q-card-section class="q-gutter-md">

                    <!-- Müşteri Seçimi -->
                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterFn" label="Müşteri Ara (İsim, TC veya ID yazın.) *" outlined
                        dense :rules="[val => !!val || 'Müşteri seçimi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">
                                    Aradığınız kriterde (en az 2 karakter) müşteri bulunamadı.
                                </q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Poliçe Türü -->
                    <q-select v-model="form.type" :options="policyTypeOptions" label="Poliçe Türü *" outlined dense
                        :rules="[val => !!val || 'Poliçe türü zorunludur']" />


                    <q-input v-model.number="form.installmentNumber" type="number" label="Taksit Sayısı(1-3-6) *"
                        outlined dense :rules="[
                            val => val !== null && val !== undefined || 'Taksit Sayısı Zorunludur',
                            val => (val == 1 || val == 3 || val == 6) || ' 1-3-6 giriniz'
                        ]" />

                    <!-- Prim Tutarı -->
                    <q-input v-model.number="form.premium" type="number" label="Prim Tutarı (TL) *" outlined dense
                        prefix="₺" :rules="[
                            val => val !== null && val !== undefined || 'Prim tutarı zorunludur',
                            val => val > 0 || 'Prim tutarı 0\'dan büyük olmalıdır'
                        ]" />

                    <!-- Başlangıç Tarihi -->
                    <q-input v-model="form.startDate" label="Başlangıç Tarihi *" outlined dense stack-label
                        mask="####/##/##" lazy-rules :rules="[
                            val => !!val || 'Başlangıç tarihi zorunludur',
                            val => isValidDate(val) || 'Geçerli bir tarih giriniz (YYYY/AA/GG)'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy ref="startDateProxy" cover transition-show="scale"
                                    transition-hide="scale">
                                    <q-date v-model="form.startDate" mask="YYYY/MM/DD"
                                        @update:model-value="closeDateProxy('startDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- Bitiş Tarihi -->
                    <q-input v-model="form.endDate" label="Bitiş Tarihi *" outlined dense stack-label mask="####/##/##"
                        lazy-rules :rules="[
                            val => !!val || 'Bitiş tarihi zorunludur',
                            val => isValidDate(val) || 'Geçerli bir tarih giriniz (YYYY/AA/GG)'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy ref="endDateProxy" cover transition-show="scale" transition-hide="scale">
                                    <q-date v-model="form.endDate" mask="YYYY/MM/DD"
                                        @update:model-value="closeDateProxy('endDate')" />
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
                    <q-btn type="submit" label="Kaydet" color="primary" :loading="loading" />
                </q-card-actions>
            </q-form>
        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { policyTypeOptions, type PolicyForm } from '../types/policy.types';
import { QPopupProxy } from 'quasar'; // Bileşeni doğrudan import ediyoruz

interface Props {
    modelValue: boolean;
}

interface CustomerOption {
    customerId: string;
    fullName: string;
    identityNumber: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'created']);

const customerStore = useCustomerStore();
const loading = ref(false);

const filteredCustomerOptions = ref<CustomerOption[]>([]);

const startDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const endDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);

const closeDateProxy = (type: 'startDate' | 'endDate') => {
    if (type === 'startDate' && startDateProxy.value) {
        startDateProxy.value.hide();
    } else if (type === 'endDate' && endDateProxy.value) {
        endDateProxy.value.hide();
    }
};

const isValidDate = (dateStr: string | null | undefined): boolean => {
    if (!dateStr) return false;
    if (dateStr.length !== 10) return false;

    const parts = dateStr.split('/');
    if (parts.length !== 3) return false;

    const year = parseInt(parts[0] as string, 10);
    const month = parseInt(parts[1] as string, 10) - 1;
    const day = parseInt(parts[2] as string, 10);

    const date = new Date(year, month, day);

    return (
        date.getFullYear() === year &&
        date.getMonth() === month &&
        date.getDate() === day
    );
};

const isOpen = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
});

const initialFormState = (): PolicyForm => ({
    customerId: '',
    type: '',
    premium: 0,
    startDate: '',
    endDate: '',
    note: '',
    installmentNumber: 1,
});

const form = ref<PolicyForm>(initialFormState());

const customerOptions = computed<CustomerOption[]>(() => {
    const data = Array.isArray(customerStore.customerData) ? customerStore.customerData : [];
    return data.map(c => {
        const namePart = `${c.firstName || ''} ${c.lastName || ''}`.trim() || c.email || 'İsimsiz Müşteri';
        const identityPart = c.identityNumber ? `(TC: ${c.identityNumber})` : `(ID: ${c.customerId})`;

        return {
            customerId: c.customerId,
            identityNumber: c.identityNumber || '',
            fullName: `${namePart} ${identityPart}`
        };
    });
});

const filterFn = (val: string, update: (callback: () => void) => void) => {
    update(() => {
        const needle = val.trim().toLowerCase();

        if (needle.length < 2) {
            filteredCustomerOptions.value = [];
            return;
        }

        filteredCustomerOptions.value = customerOptions.value.filter(
            v => v.fullName.toLowerCase().includes(needle) ||
                v.customerId.toLowerCase().includes(needle) ||
                v.identityNumber.toLowerCase().includes(needle)
        );
    });
};

const onModalShow = async () => {
    if (!customerStore.customerData || customerStore.customerData.length === 0) {
        await customerStore.fetchCustomerData({ customerId: "121" });
    }

    filteredCustomerOptions.value = [];
    form.value = initialFormState();
};

const onSubmit = () => {
    loading.value = true;
    try {
        emit('created', { data: { ...form.value } });
        isOpen.value = false;
    } catch (error) {
        console.error('Poliçe kaydedilirken hata oluştu:', error);
    } finally {
        loading.value = false;
    }
};
</script>