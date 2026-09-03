<template>
    <div class="q-gutter-sm">

        <q-separator class="q-my-xs" />
        <div class="text-subtitle2 text-weight-bold q-mb-xs">
            <q-icon name="car_repair" class="q-mr-xs" />Kasko Sigortası Alanları
        </div>

        <!-- Plaka -->
        <q-input v-model="fields.plateNumber" label="Plaka *" outlined dense class="col"
            hint="Örn: 34ABC123" lazy-rules :error="!!fieldErrors.plateNumber"
            :error-message="fieldErrors.plateNumber"
            @update:model-value="clearError('plateNumber')" :rules="[
                (val: string | null) => !!val || 'Plaka zorunludur',
                (val: string | null) => !val || /^(0[1-9]|[1-7][0-9]|8[0-1])[A-Z]{1,3}[0-9]{2,4}$/.test(val) || 'Geçersiz Türkiye plaka formatı'
            ]" />

        <!-- Şasi Numarası -->
        <q-input v-model="fields.chassisNumber" label="Şasi Numarası *" outlined dense class="col"
            maxlength="17" hint="17 karakter, I/O/Q içermemeli" lazy-rules
            :error="!!fieldErrors.chassisNumber" :error-message="fieldErrors.chassisNumber"
            @update:model-value="clearError('chassisNumber')" :rules="[
                (val: string | null) => !!val || 'Şasi numarası boş bırakılamaz',
                (val: string | null) => !val || /^[A-HJ-NPR-Z0-9]{17}$/.test(val) || 'Şasi numarası 17 karakter olmalı ve I, O, Q harflerini içermemelidir'
            ]" />

        <!-- Araç Markası -->
        <q-input v-model="fields.vehicleBrand" label="Araç Markası *" outlined dense class="col"
            maxlength="50" lazy-rules :error="!!fieldErrors.vehicleBrand"
            :error-message="fieldErrors.vehicleBrand"
            @update:model-value="clearError('vehicleBrand')" :rules="[
                (val: string | null) => !!val || 'Araç markası boş bırakılamaz',
                (val: string | null) => !val || (val.length >= 2 && val.length <= 50) || 'Araç markası 2-50 karakter arasında olmalıdır'
            ]" />

        <!-- Araç Modeli -->
        <q-input v-model="fields.vehicleModel" label="Araç Modeli *" outlined dense class="col"
            maxlength="50" lazy-rules :error="!!fieldErrors.vehicleModel"
            :error-message="fieldErrors.vehicleModel"
            @update:model-value="clearError('vehicleModel')" :rules="[
                (val: string | null) => !!val || 'Araç modeli boş bırakılamaz',
                (val: string | null) => !val || (val.length >= 1 && val.length <= 50) || 'Araç modeli 1-50 karakter arasında olmalıdır'
            ]" />

        <!-- Model Yılı -->
        <q-input v-model.number="fields.modelYear" type="number" label="Model Yılı *" outlined
            dense class="col" maxlength="4" lazy-rules :error="!!fieldErrors.modelYear"
            :error-message="fieldErrors.modelYear"
            @update:model-value="(val: number | string | null) => {
                clearError('modelYear');
                if (val !== null && val !== undefined && String(val).length > 4) {
                    fields.modelYear = Number(String(val).slice(0, 4));
                }
            }" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Model yılı boş bırakılamaz',
                (val: number | string | null) => String(val).length <= 4 || 'Model yılı en fazla 4 haneli olabilir',
                (val: number | string | null) => Number(val) >= 1990 || 'Kasko için model yılı en az 1990 olabilir',
                (val: number | string | null) => Number(val) <= (new Date().getFullYear() + 1) || 'Model yılı geçerli bir yıl olmalıdır'
            ]" />

        <!-- Araç Değeri -->
        <q-input v-model.number="fields.vehicleValue" type="number" label="Araç Değeri (TL) *"
            outlined dense class="col" prefix="₺" lazy-rules :error="!!fieldErrors.vehicleValue"
            :error-message="fieldErrors.vehicleValue"
            @update:model-value="clearError('vehicleValue')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Araç kasko değeri boş bırakılamaz',
                (val: number | string | null) => Number(val) > 0 || 'Araç kasko değeri 0\'dan büyük olmalıdır'
            ]" />

        <q-select v-model="fields.cascoType" :options="cascoTypeOptions" option-value="value"
            option-label="label" emit-value map-options label="Kasko Tipi *" outlined dense lazy-rules
            :error="!!fieldErrors.cascoType" :error-message="fieldErrors.cascoType"
            @update:model-value="clearError('cascoType')"
            :rules="[(val: unknown) => !!val || 'Kasko türü seçilmelidir']" />

        <q-toggle v-model="fields.hasReplacementCar" label="İkame Araç"
            :error="!!fieldErrors.hasReplacementCar"
            @update:model-value="clearError('hasReplacementCar')" />
        <div v-if="fieldErrors.hasReplacementCar" class="text-negative text-caption q-mt-xs">
            {{ fieldErrors.hasReplacementCar }}
        </div>

        <!-- İkame Araç Gün Sayısı -->
        <q-input v-if="fields.hasReplacementCar" v-model.number="fields.replacementCarDays"
            type="number" label="İkame Araç Gün Sayısı *" outlined dense class="col" lazy-rules
            :error="!!fieldErrors.replacementCarDays" :error-message="fieldErrors.replacementCarDays"
            @update:model-value="clearError('replacementCarDays')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '' && Number(val) > 0) || 'İkame araç seçildiyse gün sayısı 0\'dan büyük olmalıdır',
                (val: number | string | null) => Number(val) <= 60 || 'İkame araç gün sayısı en fazla 60 olabilir'
            ]" />

        <q-toggle v-model="fields.authorizedServiceOnly" label="Yetkili Servis"
            :error="!!fieldErrors.authorizedServiceOnly"
            @update:model-value="clearError('authorizedServiceOnly')" />
        <div v-if="fieldErrors.authorizedServiceOnly" class="text-negative text-caption q-mt-xs">
            {{ fieldErrors.authorizedServiceOnly }}
        </div>

        <q-toggle v-model="fields.glassExemption" label="Cam Muafiyeti"
            :error="!!fieldErrors.glassExemption"
            @update:model-value="clearError('glassExemption')" />
        <div v-if="fieldErrors.glassExemption" class="text-negative text-caption q-mt-xs">
            {{ fieldErrors.glassExemption }}
        </div>
    </div>
</template>

<script setup lang="ts">
import { type CascoFields, cascoTypeOptions } from '../../types/policy.types';

const fields = defineModel<CascoFields>('fields', { required: true });

defineProps<{
    fieldErrors: Record<string, string>;
}>();

const emit = defineEmits<{
    (e: 'clear-error', field: string): void;
}>();

const clearError = (field: string) => emit('clear-error', field);
</script>
