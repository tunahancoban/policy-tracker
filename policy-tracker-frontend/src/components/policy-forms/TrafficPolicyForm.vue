<template>
    <div class="q-gutter-sm">
        <q-separator class="q-my-xs" />
        <div class="text-subtitle2 text-weight-bold q-mb-xs">
            <q-icon name="directions_car" class="q-mr-xs" />Trafik Sigortası
        </div>

        <!-- Plaka -->
        <q-input v-model="fields.plateNumber" label="Plaka *" outlined dense hint="Örn: 34ABC123"
            lazy-rules :error="!!fieldErrors.plateNumber" :error-message="fieldErrors.plateNumber"
            @update:model-value="clearError('plateNumber')" :rules="[
                (val: string | null) => !!val || 'Plaka alanı boş bırakılamaz',
                (val: string | null) => !val || /^(0[1-9]|[1-7][0-9]|8[0-1])[A-Z]{1,3}[0-9]{2,4}$/.test(val) || 'Geçersiz Türkiye plaka formatı'
            ]" />

        <!-- Şasi Numarası -->
        <q-input v-model="fields.chassisNumber" label="Şasi Numarası *" outlined dense
            maxlength="17" hint="17 karakter, I/O/Q içermemeli" lazy-rules
            :error="!!fieldErrors.chassisNumber" :error-message="fieldErrors.chassisNumber"
            @update:model-value="clearError('chassisNumber')" :rules="[
                (val: string | null) => !!val || 'Şasi numarası boş bırakılamaz',
                (val: string | null) => !val || /^[A-HJ-NPR-Z0-9]{17}$/.test(val) || 'Şasi numarası 17 karakter olmalı ve I, O, Q harflerini içermemelidir'
            ]" />

        <!-- Motor Numarası -->
        <q-input v-model="fields.engineNumber" label="Motor Numarası *" outlined dense
            maxlength="20" lazy-rules :error="!!fieldErrors.engineNumber"
            :error-message="fieldErrors.engineNumber"
            @update:model-value="clearError('engineNumber')" :rules="[
                (val: string | null) => !!val || 'Motor numarası boş bırakılamaz',
                (val: string | null) => !val || (val.length >= 6 && val.length <= 20) || 'Motor numarası 6-20 karakter arasında olmalıdır'
            ]" />

        <q-select v-model="fields.vehicleUsageType" :options="vehicleUsageTypeOptions"
            option-value="value" option-label="label" emit-value map-options
            label="Araç Kullanım Tipi *" outlined dense lazy-rules
            :error="!!fieldErrors.vehicleUsageType" :error-message="fieldErrors.vehicleUsageType"
            @update:model-value="clearError('vehicleUsageType')"
            :rules="[(val: unknown) => !!val || 'Araç kullanım tarzı seçilmelidir']" />

        <!-- Hasarsızlık Basamağı -->
        <q-input v-model.number="fields.noClaimDiscountStep" type="number"
            label="Hasarsızlık Basamağı *" outlined dense min="0" max="8" lazy-rules
            :error="!!fieldErrors.noClaimDiscountStep" :error-message="fieldErrors.noClaimDiscountStep"
            @update:model-value="clearError('noClaimDiscountStep')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Hasarsızlık kademesi boş bırakılamaz',
                (val: number | string | null) => Number(val) >= 0 || 'Hasarsızlık basamağı en az 0 olabilir',
                (val: number | string | null) => Number(val) <= 8 || 'Hasarsızlık basamağı en fazla 8 olabilir'
            ]" />

        <q-toggle v-model="fields.hasImm" label="İMM Teminatı Var" :error="!!fieldErrors.hasImm"
            @update:model-value="clearError('hasImm')" />
        <div v-if="fieldErrors.hasImm" class="text-negative text-caption q-mt-xs">
            {{ fieldErrors.hasImm }}
        </div>

        <!-- İMM Limiti -->
        <q-input v-if="fields.hasImm" v-model.number="fields.immLimit" type="number"
            label="İMM Limiti (TL) *" outlined dense prefix="₺" class="col" lazy-rules
            :error="!!fieldErrors.immLimit" :error-message="fieldErrors.immLimit"
            @update:model-value="clearError('immLimit')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '' && Number(val) > 0) || 'İMM teminatı seçildiyse geçerli bir İMM limiti girilmelidir'
            ]" />
    </div>
</template>

<script setup lang="ts">
import { type TrafficFields, vehicleUsageTypeOptions } from '../../types/policy.types';

const fields = defineModel<TrafficFields>('fields', { required: true });

defineProps<{
    fieldErrors: Record<string, string>;
}>();

const emit = defineEmits<{
    (e: 'clear-error', field: string): void;
}>();

const clearError = (field: string) => emit('clear-error', field);
</script>
