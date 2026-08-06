<template>
    <div class="col-12 col-md-4">
        <q-card class="my-card full-height" flat bordered>
            <q-card-section class="bg-primary text-white row items-center q-gap-md q-pa-lg">
                <q-avatar size="64px" font-size="32px" color="white" text-color="primary" icon="person" />
                <div class="q-ml-md">
                    <div class="text-h5 text-weight-bold">
                        {{ customer.firstName }} {{ customer.lastName }}
                    </div>
                    <div class="text-caption text-blue-2">Müşteri Detay Profili</div>
                </div>
            </q-card-section>

            <q-separator />

            <q-card-section class="q-pa-md">
                <q-list dense>
                    <q-item class="q-py-sm">
                        <q-item-section avatar>
                            <q-icon name="account_box" color="primary" />
                        </q-item-section>
                        <q-item-section>
                            <q-item-label caption>Müşteri Numarası</q-item-label>
                            <q-item-label class="text-weight-medium">
                                {{ customer.customerId }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>

                    <q-item class="q-py-sm">
                        <q-item-section avatar>
                            <q-icon name="badge" color="primary" />
                        </q-item-section>
                        <q-item-section>
                            <q-item-label caption>T.C. Kimlik No</q-item-label>
                            <q-item-label class="text-weight-medium">
                                {{ customer.identityNumber }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>

                    <q-item class="q-py-sm">
                        <q-item-section avatar>
                            <q-icon name="email" color="primary" />
                        </q-item-section>
                        <q-item-section>
                            <q-item-label caption>E-Posta Adresi</q-item-label>
                            <q-item-label class="text-weight-medium">
                                {{ customer.email }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>

                    <q-item class="q-py-sm">
                        <q-item-section avatar>
                            <q-icon name="phone" color="primary" />
                        </q-item-section>
                        <q-item-section>
                            <q-item-label caption>Telefon Numarası</q-item-label>
                            <q-item-label class="text-weight-medium">
                                {{ customer.phoneNumber || 'Belirtilmedi' }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>

                    <q-item class="q-py-sm">
                        <q-item-section avatar>
                            <q-icon name="location_on" color="primary" />
                        </q-item-section>
                        <q-item-section>
                            <q-item-label caption>Adres Bilgisi</q-item-label>
                            <q-item-label class="text-weight-medium">
                                {{ customer.fullAddress }} {{ customer.district }}/{{ customer.city }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>

                    <q-item class="q-py-sm">
                        <q-item-section avatar>
                            <q-icon name="info" color="primary" />
                        </q-item-section>
                        <q-item-section>
                            <q-item-label caption>Sistem Durumu</q-item-label>
                            <q-item-label>
                                <q-chip :color="customer.active ? 'positive' : 'negative'" text-color="white" dense
                                    class="text-weight-bold">
                                    {{ customer.active ? 'AKTİF' : 'PASİF' }}
                                </q-chip>
                            </q-item-label>
                        </q-item-section>
                    </q-item>
                </q-list>
            </q-card-section>
        </q-card>
    </div>
</template>

<script setup lang="ts">
import type { Customer } from '@/types/customer.types';
defineProps<{ customer: Customer }>();
</script>