<template>
    <div class="q-gutter-sm">
        <q-separator class="q-my-xs" />
        <div class="text-subtitle2 text-weight-bold q-mb-xs">
            <q-icon name="health_and_safety" class="q-mr-xs" />Sağlık Sigortası Alanları
        </div>

        <!-- Kimlik/Pasaport No -->
        <q-input v-model="fields.identityNumber" label="Kimlik/Pasaport No *" outlined dense
            class="col" maxlength="20" hint="11 haneli TCKN veya pasaport no" lazy-rules
            :error="!!fieldErrors.identityNumber" :error-message="fieldErrors.identityNumber"
            @update:model-value="clearError('identityNumber')" :rules="[
                (val: string | null) => !!val || 'Kimlik/Pasaport numarası boş bırakılamaz',
                (val: string | null) => !val || /^([1-9][0-9]{10}|[A-Z0-9]{7,20})$/.test(val) || 'Geçerli bir TCKN (11 haneli) veya Pasaport numarası giriniz'
            ]" />

        <!-- Doğum Tarihi -->
        <q-input :model-value="formatDate(fields.birthDate)" label="Doğum Tarihi *" outlined dense
            stack-label readonly class="col" lazy-rules :error="!!fieldErrors.birthDate"
            :error-message="fieldErrors.birthDate" :rules="[
                () => !!fields.birthDate || 'Doğum tarihi boş bırakılamaz',
                () => !fields.birthDate || new Date(fields.birthDate) < new Date() || 'Doğum tarihi geçmiş bir tarih olmalıdır',
                () => {
                    if (!fields.birthDate) return true;
                    const birth = new Date(fields.birthDate);
                    const today = new Date();
                    let age = today.getFullYear() - birth.getFullYear();
                    const monthDiff = today.getMonth() - birth.getMonth();
                    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) age--;
                    return (age >= 0 && age <= 69) || 'Sigortalının yaşı sağlık poliçesi kabul sınırını (maksimum 69) aşamaz';
                }
            ]">
            <template v-slot:append>
                <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy ref="birthDateProxy" cover transition-show="scale" transition-hide="scale">
                        <q-date v-model="fields.birthDate" mask="YYYY-MM-DD"
                            @update:model-value="closeBirthDateProxy" />
                    </q-popup-proxy>
                </q-icon>
            </template>
        </q-input>

        <q-select v-model="fields.gender" :options="genderOptions" option-value="value"
            option-label="label" emit-value map-options label="Cinsiyet *" outlined dense class="col"
            lazy-rules :error="!!fieldErrors.gender" :error-message="fieldErrors.gender"
            @update:model-value="clearError('gender')"
            :rules="[(val: unknown) => !!val || 'Cinsiyet seçimi zorunludur']" />

        <q-select v-model="fields.healthPlanType" :options="healthPlanTypeOptions"
            option-value="value" option-label="label" emit-value map-options label="Plan Tipi *"
            outlined dense class="col" lazy-rules :error="!!fieldErrors.healthPlanType"
            :error-message="fieldErrors.healthPlanType"
            @update:model-value="clearError('healthPlanType')"
            :rules="[(val: unknown) => !!val || 'Sağlık planı türü seçilmelidir']" />

        <q-select v-model="fields.coverageScope" :options="coverageScopeOptions"
            option-value="value" option-label="label" emit-value map-options label="Kapsam *" outlined
            dense lazy-rules :error="!!fieldErrors.coverageScope"
            :error-message="fieldErrors.coverageScope"
            @update:model-value="clearError('coverageScope')"
            :rules="[(val: unknown) => !!val || 'Teminat kapsamı seçilmelidir']" />

        <!-- Ayakta Muayene Adedi -->
        <q-input v-model.number="fields.outpatientLimitCount" type="number"
            :label="fields.coverageScope === 'YATARAK_AYAKTA' ? 'Yıllık Ayakta Muayene Adedi *' : 'Yıllık Ayakta Muayene Adedi'"
            outlined dense class="col" lazy-rules :error="!!fieldErrors.outpatientLimitCount"
            :error-message="fieldErrors.outpatientLimitCount"
            @update:model-value="clearError('outpatientLimitCount')" :rules="[
                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Ayakta tedavi limiti 0\'dan küçük olamaz',
                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) <= 30 || 'Ayakta tedavi limiti en fazla 30 olabilir',
                (val: number | string | null) => fields.coverageScope !== 'YATARAK_AYAKTA' || (val !== null && val !== undefined && val !== '' && Number(val) > 0) || 'Ayakta tedavi teminatı seçildiyse muayene adedi 0\'dan büyük olmalıdır'
            ]" />

        <q-select v-model="fields.networkTier" :options="networkTierOptions" option-value="value"
            option-label="label" emit-value map-options label="Network *" outlined dense class="col"
            lazy-rules :error="!!fieldErrors.networkTier" :error-message="fieldErrors.networkTier"
            @update:model-value="clearError('networkTier')" :rules="[
                (val: string | null) => !!val || 'Anlaşmalı hastane ağı (Network) boş bırakılamaz',
                (val: string | null) => !val || (val.length >= 2 && val.length <= 50) || 'Network adı 2 ile 50 karakter arasında olmalıdır'
            ]" />

        <!-- Doğum Teminatı -->
        <q-field
            borderless
            dense
            :error="!!fieldErrors.maternityCoverage"
            :error-message="fieldErrors.maternityCoverage"
            no-error-icon
        >
            <template v-slot:control>
                <q-toggle
                    v-model="fields.maternityCoverage"
                    label="Doğum Teminatı"
                    @update:model-value="clearError('maternityCoverage')"
                />
            </template>
        </q-field>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { QPopupProxy } from 'quasar';
import {
    type HealthFields,
    genderOptions,
    healthPlanTypeOptions,
    coverageScopeOptions,
    networkTierOptions,
} from '../../types/policy.types';
import { formatDate } from '../../utils/dateHelper';

const fields = defineModel<HealthFields>('fields', { required: true });

defineProps<{
    fieldErrors: Record<string, string>;
}>();

const emit = defineEmits<{
    (e: 'clear-error', field: string): void;
}>();

const clearError = (field: string) => emit('clear-error', field);

const birthDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const closeBirthDateProxy = () => birthDateProxy.value?.hide();
</script>
