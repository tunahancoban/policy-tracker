<template>
    <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)">
        <q-card style="min-width: 450px;">
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6">{{ isEditMode ? 'Müşteri Güncelle' : 'Yeni Müşteri Ekle' }}</div>
                <q-space />
                <q-btn icon="close" flat round dense v-close-popup />
            </q-card-section>

            <q-card-section class="q-gutter-sm">
                <q-input v-model="form.firstName" label="Ad *" outlined dense
                    :rules="[val => !!val || 'Ad zorunludur']" />
                <q-input v-model="form.lastName" label="Soyad *" outlined dense
                    :rules="[val => !!val || 'Soyad zorunludur']" />
                <q-input v-model="form.identityNumber" label="T.C *" outlined dense
                    :rules="[val => !!val || 'T.C. zorunludur']" />
                <q-input v-model="form.email" label="E-posta" outlined dense type="email" />
                <q-input v-model="form.phoneNumber" label="Telefon" outlined dense mask="(###) ### ## ##"
                    unmasked-value />
                <q-input v-model="form.city" label="Şehir" outlined dense />
                <q-input v-model="form.district" label="İlçe" outlined dense />
                <q-input v-model="form.fullAddress" label="Adres" outlined dense type="textarea" rows="2" />
            </q-card-section>

            <q-card-actions align="right" class="text-primary q-pt-none">
                <q-btn flat label="Vazgeç" v-close-popup color="grey" />
                <q-btn flat :label="isEditMode ? 'Güncelle' : 'Kaydet'" color="primary" @click="saveCustomer"
                    :loading="customerStore.isLoading" />
            </q-card-actions>
        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useCustomerStore } from '../stores/customer';
import { useQuasar } from 'quasar';
import type { Customer } from '../types/customer.types';
import { initialForm } from '../types/customer.types';

const props = defineProps<{
    modelValue: boolean;
    customerData?: Customer | Partial<Customer>;
}>();

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (e: 'saved'): void;
}>();

const customerStore = useCustomerStore();
const $q = useQuasar();

const isEditMode = ref(false);

const form = ref({ ...initialForm });

// When the dialog opens, populate form from the passed customerData
watch(() => props.modelValue, (newVal) => {
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
});

// Kaydet/Güncelle fonksiyonu
const saveCustomer = async () => {
    if (!form.value.firstName || !form.value.lastName || !form.value.identityNumber) {
        $q.notify({ message: 'Lütfen zorunlu alanları doldurun.', color: 'warning' });
        return;
    }

    try {
        if (isEditMode.value) {
            await customerStore.updateCustomer(form.value);
            $q.notify({ message: 'Müşteri başarıyla güncellendi.', color: 'positive' });
        } else {
            await customerStore.addCustomer(form.value);
            $q.notify({ message: 'Müşteri başarıyla eklendi.', color: 'positive' });
        }

        emit('update:modelValue', false);
        emit('saved');

    } catch (error) {
        console.error('Müşteri güncelleme/kayıt hatası:', error);
        $q.notify({ message: 'İşlem sırasında bir hata oluştu', color: 'negative' });
    }
};
</script>
