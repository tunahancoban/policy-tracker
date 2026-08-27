<template>
    <q-dialog v-model="isOpen" @show="onModalShow">
        <q-card class="modal-card">

            <!-- Modal Title -->
            <q-card-section class="row items-center q-pb-none">
                <div class="text-h6 text-weight-bold text-grey-8">
                    {{ isRenewal ? 'Poliçeyi Yenile' : 'Yeni Poliçe Ekle' }}
                </div>
                <q-space />
                <q-btn icon="close" flat round dense v-close-popup />
            </q-card-section>

            <q-separator class="q-my-sm" />

            <!-- Form -->
            <q-form @submit="onSubmit">
                <q-card-section class="q-gutter-md scroll" style="max-height: 72vh;">

                    <!-- Customer Select -->
                    <q-select v-model="form.customerId" :options="filteredCustomerOptions" option-value="customerId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterCustomerFn" label="Müşteri Ara *" outlined dense
                        :disable="isRenewal" :loading="customerStore.isLoading" :error="!!fieldErrors.customerId"
                        :error-message="fieldErrors.customerId" @update:model-value="clearFieldError('customerId')"
                        :rules="[(val: unknown) => !!val || 'Müşteri seçimi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">
                                    Aradığınız kriterde (en az 2 karakter) müşteri bulunamadı.
                                </q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Responsible User Select -->
                    <q-select v-model="form.responsibleUserId" :options="filteredUserOptions" option-value="userId"
                        option-label="fullName" emit-value map-options use-input fill-input hide-selected
                        input-debounce="300" @filter="filterUserFn" label="Sorumlu Acente Personeli *" outlined dense
                        lazy-rules :error="!!fieldErrors.responsibleUserId"
                        :error-message="fieldErrors.responsibleUserId"
                        @update:model-value="clearFieldError('responsibleUserId')"
                        :rules="[(val: unknown) => !!val || 'Sorumlu personel seçimi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">
                                    Aradığınız kriterde (en az 2 karakter) personel bulunamadı.
                                </q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Policy Type -->
                    <q-select v-model="form.type" :options="policyTypeOptions" option-value="value" option-label="label"
                        emit-value map-options label="Poliçe Türü *" outlined dense :disable="isRenewal"
                        :error="!!fieldErrors.type" :error-message="fieldErrors.type"
                        @update:model-value="clearFieldError('type')"
                        :rules="[(val: unknown) => !!val || 'Poliçe türü zorunludur']" />

                    <!-- Sigorta Şirketi -->
                    <q-select v-model="form.company" :options="filteredInsuranceCompanyOptions" option-value="value"
                        option-label="label" emit-value map-options use-input fill-input hide-selected
                        input-debounce="200" @filter="filterInsuranceCompanyFn" label="Sigorta Şirketi *" outlined dense
                        :error="!!fieldErrors.insuranceCompany" :error-message="fieldErrors.insuranceCompany"
                        @update:model-value="clearFieldError('insuranceCompany')"
                        :rules="[(val: unknown) => !!val || 'Sigorta şirketi zorunludur']">
                        <template v-slot:no-option>
                            <q-item>
                                <q-item-section class="text-grey">Eşleşen sigorta şirketi bulunamadı.</q-item-section>
                            </q-item>
                        </template>
                    </q-select>

                    <!-- Installment -->
                    <q-select v-model="form.installment" :options="installmentOptions" label="Taksit Sayısı *" outlined
                        dense emit-value map-options lazy-rules :error="!!fieldErrors.installment"
                        :error-message="fieldErrors.installment" @update:model-value="clearFieldError('installment')"
                        :rules="[
                            (val: number | string | null | undefined) => (val !== null && val !== undefined && val !== '') || 'Taksit Sayısı Zorunludur'
                        ]" />

                    <!-- Premium -->
                    <q-input v-model.number="form.premium" type="number" label="Prim Tutarı (TL) *" outlined dense
                        prefix="₺" lazy-rules :error="!!fieldErrors.premium" :error-message="fieldErrors.premium"
                        @update:model-value="clearFieldError('premium')" :rules="[
                            (val: number | string | null | undefined) => (val !== null && val !== undefined && val !== '') || 'Prim tutarı zorunludur',
                            (val: number | string | null | undefined) => Number(val) > 0 || 'Prim tutarı 0\'dan büyük olmalıdır'
                        ]" />

                    <!-- Start Date -->
                    <q-input :model-value="formatDate(form.startDate)" label="Başlangıç Tarihi *" outlined dense
                        stack-label readonly lazy-rules :error="!!fieldErrors.startDate"
                        :error-message="fieldErrors.startDate" :rules="[
                            () => !!form.startDate || 'Başlangıç tarihi zorunludur'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy ref="startDateProxy" cover transition-show="scale"
                                    transition-hide="scale">
                                    <q-date v-model="form.startDate" mask="YYYY-MM-DD"
                                        @update:model-value="closeDateProxy('startDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- End Date -->
                    <q-input :model-value="formatDate(form.endDate)" label="Bitiş Tarihi *" outlined dense stack-label
                        readonly lazy-rules :error="!!fieldErrors.endDate" :error-message="fieldErrors.endDate" :rules="[
                            () => !!form.endDate || 'Bitiş tarihi zorunludur',
                            () => !form.startDate || !form.endDate || form.endDate >= form.startDate || 'Bitiş tarihi başlangıç tarihinden önce olamaz'
                        ]">
                        <template v-slot:append>
                            <q-icon name="event" class="cursor-pointer">
                                <q-popup-proxy ref="endDateProxy" cover transition-show="scale" transition-hide="scale">
                                    <q-date v-model="form.endDate" mask="YYYY-MM-DD"
                                        :options="(date: string) => !form.startDate || date >= form.startDate.replace(/-/g, '/')"
                                        @update:model-value="closeDateProxy('endDate')" />
                                </q-popup-proxy>
                            </q-icon>
                        </template>
                    </q-input>

                    <!-- Note -->
                    <q-input v-model="form.note" label="Not" outlined dense :error="!!fieldErrors.note"
                        :error-message="fieldErrors.note" @update:model-value="clearFieldError('note')" />

                    <!-- ── Trafik Sigortası Alanları ───────────────────────── -->
                    <template v-if="form.type === 'TRAFIK'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="directions_car" class="q-mr-xs" />Trafik Sigortası
                        </div>

                        <!-- Plaka: TR plaka formatı ile eşleşmeli -->
                        <q-input v-model="trafficFields.plateNumber" label="Plaka *" outlined dense hint="Örn: 34ABC123"
                            lazy-rules :error="!!fieldErrors.plateNumber" :error-message="fieldErrors.plateNumber"
                            @update:model-value="clearFieldError('plateNumber')" :rules="[
                                (val: string | null) => !!val || 'Plaka alanı boş bırakılamaz',
                                (val: string | null) => !val || /^(0[1-9]|[1-7][0-9]|8[0-1])[A-Z]{1,3}[0-9]{2,4}$/.test(val) || 'Geçersiz Türkiye plaka formatı'
                            ]" />

                        <!-- Şasi Numarası: tam 17 karakter, I/O/Q içermemeli -->
                        <q-input v-model="trafficFields.chassisNumber" label="Şasi Numarası *" outlined dense
                            maxlength="17" hint="17 karakter, I/O/Q içermemeli" lazy-rules
                            :error="!!fieldErrors.chassisNumber" :error-message="fieldErrors.chassisNumber"
                            @update:model-value="clearFieldError('chassisNumber')" :rules="[
                                (val: string | null) => !!val || 'Şasi numarası boş bırakılamaz',
                                (val: string | null) => !val || /^[A-HJ-NPR-Z0-9]{17}$/.test(val) || 'Şasi numarası 17 karakter olmalı ve I, O, Q harflerini içermemelidir'
                            ]" />

                        <!-- Motor Numarası: 6-20 karakter -->
                        <q-input v-model="trafficFields.engineNumber" label="Motor Numarası *" outlined dense
                            maxlength="20" lazy-rules :error="!!fieldErrors.engineNumber"
                            :error-message="fieldErrors.engineNumber"
                            @update:model-value="clearFieldError('engineNumber')" :rules="[
                                (val: string | null) => !!val || 'Motor numarası boş bırakılamaz',
                                (val: string | null) => !val || (val.length >= 6 && val.length <= 20) || 'Motor numarası 6-20 karakter arasında olmalıdır'
                            ]" />

                        <q-select v-model="trafficFields.vehicleUsageType" :options="vehicleUsageTypeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="Araç Kullanım Tipi *" outlined dense lazy-rules
                            :error="!!fieldErrors.vehicleUsageType" :error-message="fieldErrors.vehicleUsageType"
                            @update:model-value="clearFieldError('vehicleUsageType')"
                            :rules="[(val: unknown) => !!val || 'Araç kullanım tarzı seçilmelidir']" />

                        <!-- Hasarsızlık Basamağı: 0-8 arası -->
                        <q-input v-model.number="trafficFields.noClaimDiscountStep" type="number"
                            label="Hasarsızlık Basamağı *" outlined dense min="0" max="8" lazy-rules
                            :error="!!fieldErrors.noClaimDiscountStep" :error-message="fieldErrors.noClaimDiscountStep"
                            @update:model-value="clearFieldError('noClaimDiscountStep')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Hasarsızlık kademesi boş bırakılamaz',
                                (val: number | string | null) => Number(val) >= 0 || 'Hasarsızlık basamağı en az 0 olabilir',
                                (val: number | string | null) => Number(val) <= 8 || 'Hasarsızlık basamağı en fazla 8 olabilir'
                            ]" />

                        <q-toggle v-model="trafficFields.hasImm" label="İMM Teminatı Var" :error="!!fieldErrors.hasImm"
                            @update:model-value="clearFieldError('hasImm')" />
                        <div v-if="fieldErrors.hasImm" class="text-negative text-caption q-mt-xs">
                            {{ fieldErrors.hasImm }}
                        </div>

                        <!-- İMM Limiti: hasImm true ise zorunlu ve > 0 -->
                        <q-input v-if="trafficFields.hasImm" v-model.number="trafficFields.immLimit" type="number"
                            label="İMM Limiti (TL) *" outlined dense prefix="₺" class="col" lazy-rules
                            :error="!!fieldErrors.immLimit" :error-message="fieldErrors.immLimit"
                            @update:model-value="clearFieldError('immLimit')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '' && Number(val) > 0) || 'İMM teminatı seçildiyse geçerli bir İMM limiti girilmelidir'
                            ]" />
                    </template>

                    <!-- ── Kasko Sigortası Alanları ────────────────────────── -->
                    <template v-if="form.type === 'KASKO'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="car_repair" class="q-mr-xs" />Kasko Sigortası Alanları
                        </div>
                        <!-- Plaka: TR plaka formatı ile eşleşmeli -->
                        <q-input v-model="cascoFields.plateNumber" label="Plaka *" outlined dense class="col"
                            hint="Örn: 34ABC123" lazy-rules :error="!!fieldErrors.plateNumber"
                            :error-message="fieldErrors.plateNumber"
                            @update:model-value="clearFieldError('plateNumber')" :rules="[
                                (val: string | null) => !!val || 'Plaka zorunludur',
                                (val: string | null) => !val || /^(0[1-9]|[1-7][0-9]|8[0-1])[A-Z]{1,3}[0-9]{2,4}$/.test(val) || 'Geçersiz Türkiye plaka formatı'
                            ]" />
                        <!-- Şasi Numarası: tam 17 karakter, I/O/Q içermemeli -->
                        <q-input v-model="cascoFields.chassisNumber" label="Şasi Numarası *" outlined dense class="col"
                            maxlength="17" hint="17 karakter, I/O/Q içermemeli" lazy-rules
                            :error="!!fieldErrors.chassisNumber" :error-message="fieldErrors.chassisNumber"
                            @update:model-value="clearFieldError('chassisNumber')" :rules="[
                                (val: string | null) => !!val || 'Şasi numarası boş bırakılamaz',
                                (val: string | null) => !val || /^[A-HJ-NPR-Z0-9]{17}$/.test(val) || 'Şasi numarası 17 karakter olmalı ve I, O, Q harflerini içermemelidir'
                            ]" />
                        <!-- Araç Markası: 2-50 karakter -->
                        <q-input v-model="cascoFields.vehicleBrand" label="Araç Markası *" outlined dense class="col"
                            maxlength="50" lazy-rules :error="!!fieldErrors.vehicleBrand"
                            :error-message="fieldErrors.vehicleBrand"
                            @update:model-value="clearFieldError('vehicleBrand')" :rules="[
                                (val: string | null) => !!val || 'Araç markası boş bırakılamaz',
                                (val: string | null) => !val || (val.length >= 2 && val.length <= 50) || 'Araç markası 2-50 karakter arasında olmalıdır'
                            ]" />
                        <!-- Araç Modeli: 1-50 karakter -->
                        <q-input v-model="cascoFields.vehicleModel" label="Araç Modeli *" outlined dense class="col"
                            maxlength="50" lazy-rules :error="!!fieldErrors.vehicleModel"
                            :error-message="fieldErrors.vehicleModel"
                            @update:model-value="clearFieldError('vehicleModel')" :rules="[
                                (val: string | null) => !!val || 'Araç modeli boş bırakılamaz',
                                (val: string | null) => !val || (val.length >= 1 && val.length <= 50) || 'Araç modeli 1-50 karakter arasında olmalıdır'
                            ]" />
                        <!-- Model Yılı: en az 1990, en fazla (bulunulan yıl + 1), en fazla 4 hane -->
                        <q-input v-model.number="cascoFields.modelYear" type="number" label="Model Yılı *" outlined
                            dense class="col" maxlength="4" lazy-rules :error="!!fieldErrors.modelYear"
                            :error-message="fieldErrors.modelYear" @update:model-value="(val: number | string | null) => {
                                clearFieldError('modelYear');
                                if (val !== null && val !== undefined && String(val).length > 4) {
                                    cascoFields.modelYear = Number(String(val).slice(0, 4));
                                }
                            }" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Model yılı boş bırakılamaz',
                                (val: number | string | null) => String(val).length <= 4 || 'Model yılı en fazla 4 haneli olabilir',
                                (val: number | string | null) => Number(val) >= 1990 || 'Kasko için model yılı en az 1990 olabilir',
                                (val: number | string | null) => Number(val) <= (new Date().getFullYear() + 1) || 'Model yılı geçerli bir yıl olmalıdır'
                            ]" />
                        <!-- Araç Kasko Değeri: 0'dan büyük olmalı -->
                        <q-input v-model.number="cascoFields.vehicleValue" type="number" label="Araç Değeri (TL) *"
                            outlined dense class="col" prefix="₺" lazy-rules :error="!!fieldErrors.vehicleValue"
                            :error-message="fieldErrors.vehicleValue"
                            @update:model-value="clearFieldError('vehicleValue')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Araç kasko değeri boş bırakılamaz',
                                (val: number | string | null) => Number(val) > 0 || 'Araç kasko değeri 0\'dan büyük olmalıdır'
                            ]" />
                        <q-select v-model="cascoFields.cascoType" :options="cascoTypeOptions" option-value="value"
                            option-label="label" emit-value map-options label="Kasko Tipi *" outlined dense lazy-rules
                            :error="!!fieldErrors.cascoType" :error-message="fieldErrors.cascoType"
                            @update:model-value="clearFieldError('cascoType')"
                            :rules="[(val: unknown) => !!val || 'Kasko türü seçilmelidir']" />
                        <q-toggle v-model="cascoFields.hasReplacementCar" label="İkame Araç"
                            :error="!!fieldErrors.hasReplacementCar"
                            @update:model-value="clearFieldError('hasReplacementCar')" />
                        <div v-if="fieldErrors.hasReplacementCar" class="text-negative text-caption q-mt-xs">
                            {{ fieldErrors.hasReplacementCar }}
                        </div>
                        <!-- İkame Araç Gün Sayısı: 0'dan büyük ve en fazla 60 -->
                        <q-input v-if="cascoFields.hasReplacementCar" v-model.number="cascoFields.replacementCarDays"
                            type="number" label="İkame Araç Gün Sayısı *" outlined dense class="col" lazy-rules
                            :error="!!fieldErrors.replacementCarDays" :error-message="fieldErrors.replacementCarDays"
                            @update:model-value="clearFieldError('replacementCarDays')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '' && Number(val) > 0) || 'İkame araç seçildiyse gün sayısı 0\'dan büyük olmalıdır',
                                (val: number | string | null) => Number(val) <= 60 || 'İkame araç gün sayısı en fazla 60 olabilir'
                            ]" />
                        <q-toggle v-model="cascoFields.authorizedServiceOnly" label="Yetkili Servis"
                            :error="!!fieldErrors.authorizedServiceOnly"
                            @update:model-value="clearFieldError('authorizedServiceOnly')" />
                        <div v-if="fieldErrors.authorizedServiceOnly" class="text-negative text-caption q-mt-xs">
                            {{ fieldErrors.authorizedServiceOnly }}
                        </div>
                        <q-toggle v-model="cascoFields.glassExemption" label="Cam Muafiyeti"
                            :error="!!fieldErrors.glassExemption"
                            @update:model-value="clearFieldError('glassExemption')" />
                        <div v-if="fieldErrors.glassExemption" class="text-negative text-caption q-mt-xs">
                            {{ fieldErrors.glassExemption }}
                        </div>
                    </template>

                    <!-- ── DASK Alanları ───────────────────────────────────── -->
                    <template v-if="form.type === 'DASK'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="domain" class="q-mr-xs" />DASK (Afet Sigortası) Alanları
                        </div>
                        <!-- UAVT Kodu: 10 haneli rakam -->
                        <q-input v-model="daskFields.uavtCode" label="UAVT Kodu *" outlined dense maxlength="10"
                            hint="10 haneli rakam" lazy-rules :error="!!fieldErrors.uavtCode"
                            :error-message="fieldErrors.uavtCode" @update:model-value="clearFieldError('uavtCode')"
                            :rules="[
                                (val: string | null) => !!val || 'UAVT adres kodu boş bırakılamaz',
                                (val: string | null) => !val || /^[0-9]{10}$/.test(val) || 'UAVT adres kodu 10 haneli rakam olmalıdır'
                            ]" />
                        <!-- Brüt Alan: 10-1000 m² -->
                        <q-input v-model.number="daskFields.grossSquareMeters" type="number" label="Brüt Alan (m²) *"
                            outlined dense class="col" lazy-rules :error="!!fieldErrors.grossSquareMeters"
                            :error-message="fieldErrors.grossSquareMeters"
                            @update:model-value="clearFieldError('grossSquareMeters')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Brüt metrekare alanı zorunludur',
                                (val: number | string | null) => Number(val) >= 10 || 'Brüt metrekare en az 10 m² olmalıdır',
                                (val: number | string | null) => Number(val) <= 1000 || 'Brüt metrekare en fazla 1000 m² olabilir'
                            ]" />
                        <!-- İnşaat Yılı: en az 1900, bulunulan yıldan büyük olamaz, en fazla 4 hane -->
                        <q-input v-model.number="daskFields.buildingConstructionYear" type="number"
                            label="İnşaat Yılı *" outlined dense class="col" maxlength="4" lazy-rules
                            :error="!!fieldErrors.buildingConstructionYear"
                            :error-message="fieldErrors.buildingConstructionYear" @update:model-value="(val: number | string | null) => {
                                clearFieldError('buildingConstructionYear');
                                if (val !== null && val !== undefined && String(val).length > 4) {
                                    daskFields.buildingConstructionYear = Number(String(val).slice(0, 4));
                                }
                            }" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Bina inşa yılı boş bırakılamaz',
                                (val: number | string | null) => String(val).length <= 4 || 'İnşaat yılı en fazla 4 haneli olabilir',
                                (val: number | string | null) => Number(val) >= 1900 || 'Bina inşa yılı 1900 yılından küçük olamaz',
                                (val: number | string | null) => Number(val) <= new Date().getFullYear() || 'Bina inşa yılı içinde bulunulan yıldan büyük olamaz'
                            ]" />

                        <q-select v-model="daskFields.buildingConstructionType"
                            :options="buildingConstructionTypeOptions" option-value="value" option-label="label"
                            emit-value map-options label="Yapı Tipi *" outlined dense lazy-rules
                            :error="!!fieldErrors.buildingConstructionType"
                            :error-message="fieldErrors.buildingConstructionType"
                            @update:model-value="clearFieldError('buildingConstructionType')"
                            :rules="[(val: unknown) => !!val || 'Bina yapı tarzı seçilmelidir']" />
                        <!-- Toplam Kat Sayısı: 1-100 -->
                        <q-input v-model.number="daskFields.totalFloorCount" type="number" label="Toplam Kat Sayısı *"
                            outlined dense class="col" lazy-rules :error="!!fieldErrors.totalFloorCount"
                            :error-message="fieldErrors.totalFloorCount"
                            @update:model-value="clearFieldError('totalFloorCount')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Toplam kat sayısı zorunludur',
                                (val: number | string | null) => Number(val) >= 1 || 'Toplam kat sayısı en az 1 olmalıdır',
                                (val: number | string | null) => Number(val) <= 100 || 'Toplam kat sayısı 100\'den büyük olamaz'
                            ]" />
                        <!-- Dairenin Bulunduğu Kat: -5 ile 100 arası, toplam kat sayısını geçemez -->
                        <q-input v-model.number="daskFields.apartmentFloor" type="number"
                            label="Dairenin Bulunduğu Kat *" outlined dense class="col" lazy-rules
                            :error="!!fieldErrors.apartmentFloor" :error-message="fieldErrors.apartmentFloor"
                            @update:model-value="clearFieldError('apartmentFloor')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Bulunduğu kat bilgisi zorunludur',
                                (val: number | string | null) => Number(val) >= -5 || 'Bulunduğu kat -5\'ten (bodrum katlar) küçük olamaz',
                                (val: number | string | null) => Number(val) <= 100 || 'Bulunduğu kat 100\'den büyük olamaz',
                                (val: number | string | null) => !daskFields.totalFloorCount || Number(val) <= Number(daskFields.totalFloorCount) || 'Bulunduğu kat, binanın toplam kat sayısından büyük olamaz'
                            ]" />
                        <!-- Deprem Bölgesi: 1-5 -->
                        <q-input v-model.number="daskFields.earthquakeZone" type="number" label="Deprem Bölgesi (1-5) *"
                            outlined dense lazy-rules :error="!!fieldErrors.earthquakeZone"
                            :error-message="fieldErrors.earthquakeZone"
                            @update:model-value="clearFieldError('earthquakeZone')" :rules="[
                                (val: number | string | null) => (val !== null && val !== undefined && val !== '') || 'Deprem risk bölgesi boş bırakılamaz',
                                (val: number | string | null) => Number(val) >= 1 || 'Deprem bölgesi en az 1 olabilir',
                                (val: number | string | null) => Number(val) <= 5 || 'Deprem bölgesi en fazla 5 olabilir'
                            ]" />
                    </template>

                    <!-- ── Konut Sigortası Alanları ───────────────────────── -->
                    <template v-if="form.type === 'KONUT'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="home" class="q-mr-xs" />Konut Sigortası Alanları
                        </div>

                        <!-- UAVT Kodu: 10 haneli rakam -->
                        <q-input v-model="houseFields.uavtCode" label="UAVT Kodu *" outlined dense
                            maxlength="10" hint="10 haneli rakam" lazy-rules
                            :error="!!fieldErrors.uavtCode" :error-message="fieldErrors.uavtCode"
                            @update:model-value="clearFieldError('uavtCode')" :rules="[
                                (val: string | null) => !!val || 'UAVT adres kodu boş bırakılamaz',
                                (val: string | null) => !val || /^[0-9]{10}$/.test(val) || 'UAVT adres kodu 10 haneli rakam olmalıdır'
                            ]" />

                        <!-- İkamet Tipi: NotNull -->
                        <q-select v-model="houseFields.residenceType" :options="residenceTypeOptions"
                            option-value="value" option-label="label" emit-value map-options
                            label="İkamet Tipi *" outlined dense lazy-rules
                            :error="!!fieldErrors.residenceType" :error-message="fieldErrors.residenceType"
                            @update:model-value="clearFieldError('residenceType')"
                            :rules="[(val: unknown) => !!val || 'Konut kullanım türü (Ev Sahibi/Kiracı) seçilmelidir']" />

                        <!-- Bina Teminat Bedeli -->
                        <q-input v-model.number="houseFields.buildingCoverageLimit" type="number"
                            label="Bina Teminat Bedeli (TL)" outlined dense class="col" prefix="₺" lazy-rules
                            :error="!!fieldErrors.buildingCoverageLimit" :error-message="fieldErrors.buildingCoverageLimit"
                            @update:model-value="clearFieldError('buildingCoverageLimit')" :rules="[
                                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Bina teminat bedeli negatif olamaz',
                                (val: number | string | null) => Number(val || 0) > 0 || Number(houseFields.contentsCoverageLimit || 0) > 0 || 'Bina veya eşya teminat bedelinden en az biri sıfırdan büyük olmalıdır'
                            ]" />

                        <!-- Eşya Teminat Bedeli -->
                        <q-input v-model.number="houseFields.contentsCoverageLimit" type="number"
                            label="Eşya Teminat Bedeli (TL)" outlined dense class="col" prefix="₺" lazy-rules
                            :error="!!fieldErrors.contentsCoverageLimit" :error-message="fieldErrors.contentsCoverageLimit"
                            @update:model-value="clearFieldError('contentsCoverageLimit')" :rules="[
                                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Eşya teminat bedeli negatif olamaz',
                                (val: number | string | null) => Number(val || 0) > 0 || Number(houseFields.buildingCoverageLimit || 0) > 0 || 'Bina veya eşya teminat bedelinden en az biri sıfırdan büyük olmalıdır'
                            ]" />

                        <!-- Komşuluk Sorumluluk Limiti: >= 0 -->
                        <q-input v-model.number="houseFields.thirdPartyLiabilityLimit" type="number"
                            label="Komşuluk Sorumluluk Limiti (TL)" outlined dense prefix="₺" lazy-rules
                            :error="!!fieldErrors.thirdPartyLiabilityLimit" :error-message="fieldErrors.thirdPartyLiabilityLimit"
                            @update:model-value="clearFieldError('thirdPartyLiabilityLimit')" :rules="[
                                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Komşuluk mali sorumluluk limiti negatif olamaz'
                            ]" />

                        <!-- Teminat Toggles (NotNull Validasyonları ile) -->
                        <div class="row q-gutter-md">
                            <div>
                                <q-toggle v-model="houseFields.theftCoverage" label="Hırsızlık Teminatı" lazy-rules
                                    :error="!!fieldErrors.theftCoverage"
                                    @update:model-value="clearFieldError('theftCoverage')"
                                    :rules="[(val: unknown) => val !== null && val !== undefined || 'Hırsızlık teminat durumu belirtilmelidir']" />
                                <div v-if="fieldErrors.theftCoverage" class="text-negative text-caption q-mt-xs">
                                    {{ fieldErrors.theftCoverage }}
                                </div>
                            </div>
                            <div>
                                <q-toggle v-model="houseFields.waterDamageCoverage" label="Su Hasarı Teminatı" lazy-rules
                                    :error="!!fieldErrors.waterDamageCoverage"
                                    @update:model-value="clearFieldError('waterDamageCoverage')"
                                    :rules="[(val: unknown) => val !== null && val !== undefined || 'Dahili su teminat durumu belirtilmelidir']" />
                                <div v-if="fieldErrors.waterDamageCoverage" class="text-negative text-caption q-mt-xs">
                                    {{ fieldErrors.waterDamageCoverage }}
                                </div>
                            </div>
                            <div>
                                <q-toggle v-model="houseFields.glassBreakageCoverage" label="Cam Kırılması" lazy-rules
                                    :error="!!fieldErrors.glassBreakageCoverage"
                                    @update:model-value="clearFieldError('glassBreakageCoverage')"
                                    :rules="[(val: unknown) => val !== null && val !== undefined || 'Cam kırılması teminat durumu belirtilmelidir']" />
                                <div v-if="fieldErrors.glassBreakageCoverage"
                                    class="text-negative text-caption q-mt-xs">
                                    {{ fieldErrors.glassBreakageCoverage }}
                                </div>
                            </div>
                        </div>
                    </template>

                    <!-- ── Sağlık Sigortası Alanları ──────────────────────── -->
                    <template v-if="form.type === 'SAGLIK'">
                        <q-separator class="q-my-xs" />
                        <div class="text-subtitle2 text-weight-bold q-mb-xs">
                            <q-icon name="health_and_safety" class="q-mr-xs" />Sağlık Sigortası Alanları
                        </div>
                        <!-- Kimlik/Pasaport No: 11 haneli TCKN veya 7-20 karakter pasaport -->
                        <q-input v-model="healthFields.identityNumber" label="Kimlik/Pasaport No *" outlined dense
                            class="col" maxlength="20" hint="11 haneli TCKN veya pasaport no" lazy-rules
                            :error="!!fieldErrors.identityNumber" :error-message="fieldErrors.identityNumber"
                            @update:model-value="clearFieldError('identityNumber')" :rules="[
                                (val: string | null) => !!val || 'Kimlik/Pasaport numarası boş bırakılamaz',
                                (val: string | null) => !val || /^([1-9][0-9]{10}|[A-Z0-9]{7,20})$/.test(val) || 'Geçerli bir TCKN (11 haneli) veya Pasaport numarası giriniz'
                            ]" />
                        <!-- Birth Date (date-picker, aynı format Başlangıç/Bitiş tarihi ile) -->
                        <q-input :model-value="formatDate(healthFields.birthDate)" label="Doğum Tarihi *" outlined dense
                            stack-label readonly class="col" lazy-rules :error="!!fieldErrors.birthDate"
                            :error-message="fieldErrors.birthDate" :rules="[
                                () => !!healthFields.birthDate || 'Doğum tarihi boş bırakılamaz',
                                () => !healthFields.birthDate || new Date(healthFields.birthDate) < new Date() || 'Doğum tarihi geçmiş bir tarih olmalıdır',
                                () => {
                                    if (!healthFields.birthDate) return true;
                                    const birth = new Date(healthFields.birthDate);
                                    const today = new Date();
                                    let age = today.getFullYear() - birth.getFullYear();
                                    const monthDiff = today.getMonth() - birth.getMonth();
                                    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) age--;
                                    return (age >= 0 && age <= 69) || 'Sigortalının yaşı sağlık poliçesi kabul sınırını (maksimum 69) aşamaz';
                                }
                            ]">
                            <template v-slot:append>
                                <q-icon name="event" class="cursor-pointer">
                                    <q-popup-proxy ref="birthDateProxy" cover transition-show="scale"
                                        transition-hide="scale">
                                        <q-date v-model="healthFields.birthDate" mask="YYYY-MM-DD"
                                            @update:model-value="closeDateProxy('birthDate')" />
                                    </q-popup-proxy>
                                </q-icon>
                            </template>
                        </q-input>
                        <q-select v-model="healthFields.gender" :options="genderOptions" option-value="value"
                            option-label="label" emit-value map-options label="Cinsiyet *" outlined dense class="col"
                            lazy-rules :error="!!fieldErrors.gender" :error-message="fieldErrors.gender"
                            @update:model-value="clearFieldError('gender')"
                            :rules="[(val: unknown) => !!val || 'Cinsiyet seçimi zorunludur']" />
                        <q-select v-model="healthFields.healthPlanType" :options="healthPlanTypeOptions"
                            option-value="value" option-label="label" emit-value map-options label="Plan Tipi *"
                            outlined dense class="col" lazy-rules :error="!!fieldErrors.healthPlanType"
                            :error-message="fieldErrors.healthPlanType"
                            @update:model-value="clearFieldError('healthPlanType')"
                            :rules="[(val: unknown) => !!val || 'Sağlık planı türü seçilmelidir']" />
                        <q-select v-model="healthFields.coverageScope" :options="coverageScopeOptions"
                            option-value="value" option-label="label" emit-value map-options label="Kapsam *" outlined
                            dense lazy-rules :error="!!fieldErrors.coverageScope"
                            :error-message="fieldErrors.coverageScope"
                            @update:model-value="clearFieldError('coverageScope')"
                            :rules="[(val: unknown) => !!val || 'Teminat kapsamı seçilmelidir']" />
                        <!-- Ayakta Muayene Adedi: 0-30, Kapsam YATARAK_AYAKTA ise zorunlu ve >0 -->
                        <q-input v-model.number="healthFields.outpatientLimitCount" type="number"
                            :label="healthFields.coverageScope === 'YATARAK_AYAKTA' ? 'Yıllık Ayakta Muayene Adedi *' : 'Yıllık Ayakta Muayene Adedi'"
                            outlined dense class="col" lazy-rules :error="!!fieldErrors.outpatientLimitCount"
                            :error-message="fieldErrors.outpatientLimitCount"
                            @update:model-value="clearFieldError('outpatientLimitCount')" :rules="[
                                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) >= 0 || 'Ayakta tedavi limiti 0\'dan küçük olamaz',
                                (val: number | string | null) => val === null || val === undefined || val === '' || Number(val) <= 30 || 'Ayakta tedavi limiti en fazla 30 olabilir',
                                (val: number | string | null) => healthFields.coverageScope !== 'YATARAK_AYAKTA' || (val !== null && val !== undefined && val !== '' && Number(val) > 0) || 'Ayakta tedavi teminatı seçildiyse muayene adedi 0\'dan büyük olmalıdır'
                            ]" />
                        <!-- Network: 2-50 karakter -->
                        <q-select v-model="healthFields.networkTier" :options="networkTierOptions" option-value="value"
                            option-label="label" emit-value map-options label="Network *" outlined dense class="col"
                            lazy-rules :error="!!fieldErrors.networkTier" :error-message="fieldErrors.networkTier"
                            @update:model-value="clearFieldError('networkTier')" :rules="[
                                (val: string | null) => !!val || 'Anlaşmalı hastane ağı (Network) boş bırakılamaz',
                                (val: string | null) => !val || (val.length >= 2 && val.length <= 50) || 'Network adı 2 ile 50 karakter arasında olmalıdır'
                            ]" />
                        <!-- Doğum Teminatı: sadece kadın sigortalılar için seçilebilir -->
                        <q-toggle v-model="healthFields.maternityCoverage" label="Doğum Teminatı" lazy-rules
                            :error="!!fieldErrors.maternityCoverage"
                            @update:model-value="clearFieldError('maternityCoverage')" :rules="[
                                (val: boolean | null | undefined) =>
                                    !val ||
                                    healthFields.gender === 'KADIN' ||
                                    healthFields.gender === 'FEMALE' ||
                                    'Doğum teminatı yalnızca kadın sigortalılar için seçilebilir'
                            ]" />
                        <div v-if="fieldErrors.maternityCoverage" class="text-negative text-caption q-mt-xs">
                            {{ fieldErrors.maternityCoverage }}
                        </div>
                    </template>

                </q-card-section>

                <q-separator />

                <!-- Action Buttons -->
                <q-card-actions align="right" class="q-pa-md">
                    <q-btn flat label="İptal" color="grey-7" v-close-popup :disable="loading" />
                    <q-btn type="submit" :label="isRenewal ? 'Poliçeyi Yenile' : 'Kaydet'"
                        :color="isRenewal ? 'secondary' : 'primary'" :loading="loading" />
                </q-card-actions>
            </q-form>

        </q-card>
    </q-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { QPopupProxy } from 'quasar';
import { type Policy, type CreatePolicyRequest, type RenewPolicyRequest } from '../types/policy.types';
import {
    type TrafficFields, type CascoFields, type DaskFields, type HouseFields, type HealthFields,
    vehicleUsageTypeOptions, cascoTypeOptions, buildingConstructionTypeOptions,
    residenceTypeOptions, genderOptions, healthPlanTypeOptions, coverageScopeOptions, networkTierOptions,
} from '../types/policy.types';
import { usePolicyForm } from '../composables/usePolicyForm';
import { installmentOptions } from '../types/installment.types';
import { formatDate } from '../utils/dateHelper';
import { ValidationError } from '../error/errors';

// ── Props & Emits ─────────────────────────────────────────────────────────────

interface Props {
    modelValue: boolean;
    isRenewal?: boolean;
    policyData?: Policy | null;
}

const props = withDefaults(defineProps<Props>(), {
    isRenewal: false,
    policyData: null,
});

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (e: 'created', payload: CreatePolicyRequest): void;
    (e: 'renewed', payload: RenewPolicyRequest): void;
}>();

// ── v-model bridge ────────────────────────────────────────────────────────────

const isOpen = computed({
    get: () => props.modelValue,
    set: (value: boolean) => emit('update:modelValue', value),
});

// ── Date-picker proxy refs ────────────────────────────────────────────────────

const startDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const endDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);
const birthDateProxy = ref<InstanceType<typeof QPopupProxy> | null>(null);

const closeDateProxy = (type: 'startDate' | 'endDate' | 'birthDate') => {
    if (type === 'startDate') startDateProxy.value?.hide();
    else if (type === 'endDate') endDateProxy.value?.hide();
    else birthDateProxy.value?.hide();
};

// ── Field-level errors ────────────────────────────────────────────────────────

const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};

// ── Composable ────────────────────────────────────────────────────────────────

const {
    form,
    loading,
    typeSpecificFields,
    filteredCustomerOptions,
    filteredUserOptions,
    filteredInsuranceCompanyOptions,
    customerStore,
    policyTypeOptions,
    onModalShow: onModalShowBase,
    filterCustomerFn,
    filterUserFn,
    filterInsuranceCompanyFn,
    buildSubmitPayload,
    submitWithLoading,
} = usePolicyForm(props);

// ── Type-specific field proxies ──────────────────────────────────────────────

function asType<T>(val: unknown): T { return val as T; }

const trafficFields = computed(() => asType<TrafficFields>(typeSpecificFields.value));
const cascoFields = computed(() => asType<CascoFields>(typeSpecificFields.value));
const daskFields = computed(() => asType<DaskFields>(typeSpecificFields.value));
const houseFields = computed(() => asType<HouseFields>(typeSpecificFields.value));
const healthFields = computed(() => asType<HealthFields>(typeSpecificFields.value));

// ── Modal lifecycle ───────────────────────────────────────────────────────────

const onModalShow = () => {
    fieldErrors.value = {};
    void onModalShowBase();
};

// ── Submit ────────────────────────────────────────────────────────────────────

const onSubmit = async () => {
    fieldErrors.value = {};

    try {
        await submitWithLoading(() => {
            const result = buildSubmitPayload();
            if (!result) return Promise.resolve();

            if (result.mode === 'renewed') {
                emit('renewed', result.payload);
            } else {
                emit('created', result.payload);
            }

            isOpen.value = false;
            return Promise.resolve();
        });
    } catch (error: unknown) {
        if (error instanceof ValidationError && error.errors) {
            fieldErrors.value = error.errors;
            return;
        }

        console.error('Poliçe kaydetme hatası:', error);
    }
};
</script>