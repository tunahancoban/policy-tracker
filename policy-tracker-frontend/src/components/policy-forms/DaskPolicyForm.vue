<template>
    <div class="q-gutter-sm">
        <q-separator class="q-my-xs" />
        <div class="text-subtitle2 text-weight-bold q-mb-xs">
            <q-icon name="domain" class="q-mr-xs" />DASK (Afet Sigortası) Alanları
        </div>

        <q-input v-model="fields.uavtCode" label="UAVT Kodu *" outlined dense maxlength="10"
            hint="10 haneli rakam" lazy-rules :error="!!fieldErrors.uavtCode"
            :error-message="fieldErrors.uavtCode" @update:model-value="clearError('uavtCode')"
            :rules="[
                (val: string | null) => !!val || 'UAVT adres kodu boş bırakılamaz',
                (val: string | null) => !val || /^[0-9]{10}$/.test(val) || 'UAVT adres kodu 10 haneli rakam olmalıdır'
            ]" />

        <q-input v-model.number="fields.grossSquareMeters" type="number" label="Brüt Alan (m²) *"
            outlined dense class="col" lazy-rules :error="!!fieldErrors.grossSquareMeters"
            :error-message="fieldErrors.grossSquareMeters"
            @update:model-value="clearError('grossSquareMeters')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Brüt metrekare alanı zorunludur',
                (val: number | string | null) => Number(val) >= 10 || 'Brüt metrekare en az 10 m² olmalıdır',
                (val: number | string | null) => Number(val) <= 1000 || 'Brüt metrekare en fazla 1000 m² olabilir'
            ]" />

        <q-input v-model.number="fields.buildingConstructionYear" type="number"
            label="İnşaat Yılı *" outlined dense class="col" maxlength="4" lazy-rules
            :error="!!fieldErrors.buildingConstructionYear"
            :error-message="fieldErrors.buildingConstructionYear"
            @update:model-value="(val: number | string | null) => {
                clearError('buildingConstructionYear');
                if (val !== null && val !== undefined && String(val).length > 4) {
                    fields.buildingConstructionYear = Number(String(val).slice(0, 4));
                }
            }" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Bina inşa yılı boş bırakılamaz',
                (val: number | string | null) => String(val).length <= 4 || 'İnşaat yılı en fazla 4 haneli olabilir',
                (val: number | string | null) => Number(val) >= 1900 || 'Bina inşa yılı 1900 yılından küçük olamaz',
                (val: number | string | null) => Number(val) <= new Date().getFullYear() || 'Bina inşa yılı içinde bulunulan yıldan büyük olamaz'
            ]" />

        <q-select v-model="fields.buildingConstructionType"
            :options="buildingConstructionTypeOptions" option-value="value" option-label="label"
            emit-value map-options label="Yapı Tipi *" outlined dense lazy-rules
            :error="!!fieldErrors.buildingConstructionType"
            :error-message="fieldErrors.buildingConstructionType"
            @update:model-value="clearError('buildingConstructionType')"
            :rules="[(val: unknown) => !!val || 'Bina yapı tarzı seçilmelidir']" />

        <q-input v-model.number="fields.totalFloorCount" type="number" label="Toplam Kat Sayısı *"
            outlined dense class="col" lazy-rules :error="!!fieldErrors.totalFloorCount"
            :error-message="fieldErrors.totalFloorCount"
            @update:model-value="clearError('totalFloorCount')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Toplam kat sayısı zorunludur',
                (val: number | string | null) => Number(val) >= 1 || 'Toplam kat sayısı en az 1 olmalıdır',
                (val: number | string | null) => Number(val) <= 100 || 'Toplam kat sayısı 100\'den büyük olamaz'
            ]" />

        <q-input v-model.number="fields.apartmentFloor" type="number"
            label="Dairenin Bulunduğu Kat *" outlined dense class="col" lazy-rules
            :error="!!fieldErrors.apartmentFloor" :error-message="fieldErrors.apartmentFloor"
            @update:model-value="clearError('apartmentFloor')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Bulunduğu kat bilgisi zorunludur',
                (val: number | string | null) => Number(val) >= -5 || 'Bulunduğu kat -5\'ten (bodrum katlar) küçük olamaz',
                (val: number | string | null) => Number(val) <= 100 || 'Bulunduğu kat 100\'den büyük olamaz',
                (val: number | string | null) => !fields.totalFloorCount || Number(val) <= Number(fields.totalFloorCount) || 'Bulunduğu kat, binanın toplam kat sayısından büyük olamaz'
            ]" />

        <q-input v-model.number="fields.earthquakeZone" type="number" label="Deprem Bölgesi (1-5) *"
            outlined dense lazy-rules :error="!!fieldErrors.earthquakeZone"
            :error-message="fieldErrors.earthquakeZone"
            @update:model-value="clearError('earthquakeZone')" :rules="[
                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Deprem risk bölgesi boş bırakılamaz',
                (val: number | string | null) => Number(val) >= 1 || 'Deprem bölgesi en az 1 olabilir',
                (val: number | string | null) => Number(val) <= 5 || 'Deprem bölgesi en fazla 5 olabilir'
            ]" />
    </div>
</template>

<script setup lang="ts">
import { type DaskFields, buildingConstructionTypeOptions } from '../../types/policy.types';

const fields = defineModel<DaskFields>('fields', { required: true });

defineProps<{
    fieldErrors: Record<string, string>;
}>();

const emit = defineEmits<{
    (e: 'clear-error', field: string): void;
}>();

const clearError = (field: string) => emit('clear-error', field);
</script>
