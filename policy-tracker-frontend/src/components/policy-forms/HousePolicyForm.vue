<template>
    <div class="q-gutter-sm">
        <q-separator class="q-my-xs" />
        <div class="text-subtitle2 text-weight-bold q-mb-xs">
            <q-icon name="home" class="q-mr-xs" />Konut Sigortası Alanları
        </div>

        <!-- UAVT Kodu -->
        <q-input v-model="fields.uavtCode" label="UAVT Kodu *" outlined dense
            maxlength="10" hint="10 haneli rakam" lazy-rules
            :error="!!fieldErrors.uavtCode" :error-message="fieldErrors.uavtCode"
            @update:model-value="clearError('uavtCode')" :rules="[
                (val: string | null) => !!val || 'UAVT adres kodu boş bırakılamaz',
                (val: string | null) => !val || /^[0-9]{10}$/.test(val) || 'UAVT adres kodu 10 haneli rakam olmalıdır'
            ]" />

        <!-- İkamet Tipi -->
        <q-select v-model="fields.residenceType" :options="residenceTypeOptions"
            option-value="value" option-label="label" emit-value map-options
            label="İkamet Tipi *" outlined dense lazy-rules
            :error="!!fieldErrors.residenceType" :error-message="fieldErrors.residenceType"
            @update:model-value="clearError('residenceType')"
            :rules="[(val: unknown) => !!val || 'Konut kullanım türü (Ev Sahibi/Kiracı) seçilmelidir']" />

        <!-- Bina Teminat Bedeli -->
        <q-input v-model.number="fields.buildingCoverageLimit" type="number"
            label="Bina Teminat Bedeli (TL)" outlined dense class="col" prefix="₺" lazy-rules
            :error="!!fieldErrors.buildingCoverageLimit" :error-message="fieldErrors.buildingCoverageLimit"
            @update:model-value="clearError('buildingCoverageLimit')" :rules="[
                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Bina teminat bedeli negatif olamaz',
                (val: number | string | null) => Number(val || 0) > 0 || Number(fields.contentsCoverageLimit || 0) > 0 || 'Bina veya eşya teminat bedelinden en az biri sıfırdan büyük olmalıdır'
            ]" />

        <!-- Eşya Teminat Bedeli -->
        <q-input v-model.number="fields.contentsCoverageLimit" type="number"
            label="Eşya Teminat Bedeli (TL)" outlined dense class="col" prefix="₺" lazy-rules
            :error="!!fieldErrors.contentsCoverageLimit" :error-message="fieldErrors.contentsCoverageLimit"
            @update:model-value="clearError('contentsCoverageLimit')" :rules="[
                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Eşya teminat bedeli negatif olamaz',
                (val: number | string | null) => Number(val || 0) > 0 || Number(fields.buildingCoverageLimit || 0) > 0 || 'Bina veya eşya teminat bedelinden en az biri sıfırdan büyük olmalıdır'
            ]" />

        <!-- Komşuluk Sorumluluk Limiti -->
        <q-input v-model.number="fields.thirdPartyLiabilityLimit" type="number"
            label="Komşuluk Sorumluluk Limiti (TL)" outlined dense prefix="₺" lazy-rules
            :error="!!fieldErrors.thirdPartyLiabilityLimit" :error-message="fieldErrors.thirdPartyLiabilityLimit"
            @update:model-value="clearError('thirdPartyLiabilityLimit')" :rules="[
                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Komşuluk mali sorumluluk limiti negatif olamaz'
            ]" />

        <!-- Teminat Toggles -->
        <div class="row q-gutter-md">
            <div>
                <q-toggle v-model="fields.theftCoverage" label="Hırsızlık Teminatı" lazy-rules
                    :error="!!fieldErrors.theftCoverage"
                    @update:model-value="clearError('theftCoverage')"
                    :rules="[(val: unknown) => val !== null && val !== undefined || 'Hırsızlık teminat durumu belirtilmelidir']" />
                <div v-if="fieldErrors.theftCoverage" class="text-negative text-caption q-mt-xs">
                    {{ fieldErrors.theftCoverage }}
                </div>
            </div>
            <div>
                <q-toggle v-model="fields.waterDamageCoverage" label="Su Hasarı Teminatı" lazy-rules
                    :error="!!fieldErrors.waterDamageCoverage"
                    @update:model-value="clearError('waterDamageCoverage')"
                    :rules="[(val: unknown) => val !== null && val !== undefined || 'Dahili su teminat durumu belirtilmelidir']" />
                <div v-if="fieldErrors.waterDamageCoverage" class="text-negative text-caption q-mt-xs">
                    {{ fieldErrors.waterDamageCoverage }}
                </div>
            </div>
            <div>
                <q-toggle v-model="fields.glassBreakageCoverage" label="Cam Kırılması" lazy-rules
                    :error="!!fieldErrors.glassBreakageCoverage"
                    @update:model-value="clearError('glassBreakageCoverage')"
                    :rules="[(val: unknown) => val !== null && val !== undefined || 'Cam kırılması teminat durumu belirtilmelidir']" />
                <div v-if="fieldErrors.glassBreakageCoverage" class="text-negative text-caption q-mt-xs">
                    {{ fieldErrors.glassBreakageCoverage }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { type HouseFields, residenceTypeOptions } from '../../types/policy.types';

const fields = defineModel<HouseFields>('fields', { required: true });

defineProps<{
    fieldErrors: Record<string, string>;
}>();

const emit = defineEmits<{
    (e: 'clear-error', field: string): void;
}>();

const clearError = (field: string) => emit('clear-error', field);
</script>
