<!-- src/pages/ProfilePage.vue -->
<template>
    <q-page class="q-pa-md row justify-center">
        <div class="col-12 col-md-8 col-lg-6">
            <q-card flat bordered class="q-pa-md">
                <div class="text-h6 text-grey-8 text-weight-bold q-mb-md">
                    <q-icon name="person" color="primary" class="q-mr-xs" size="28px" />
                    Profil Ayarlarım
                </div>

                <q-form @submit="handleUpdateProfile" class="q-gutter-md">
                    <q-input v-model="profileForm.firstName" label="Ad *" outlined dense
                        :rules="[val => !!val || 'Ad alanı zorunludur']" />
                    <q-input v-model="profileForm.lastName" label="Soyad *" outlined dense
                        :rules="[val => !!val || 'Soyad alanı zorunludur']" />
                    <q-input v-model="profileForm.email" label="E-posta *" outlined dense type="email"
                        :rules="[val => !!val || 'E-posta alanı zorunludur']" />

                    <q-separator class="q-my-lg" />

                    <div class="text-subtitle1 text-grey-7 text-weight-bold">Şifre Değiştir (İsteğe Bağlı)</div>
                    <p class="text-caption text-grey-6 q-mt-none">Şifrenizi değiştirmek istemiyorsanız bu alanları boş
                        bırakabilirsiniz.</p>

                    <q-input v-model="password" label="Yeni Şifre" outlined dense type="password" />
                    <q-input v-model="confirmPassword" label="Yeni Şifre (Tekrar)" outlined dense type="password"
                        :rules="[val => val === password || 'Şifreler birbiriyle uyuşmuyor']" />

                    <div class="row justify-end q-mt-md">
                        <q-btn color="primary" label="Değişiklikleri Kaydet" type="submit" unelevated
                            :loading="isLoading" />
                    </div>
                </q-form>
            </q-card>
        </div>
    </q-page>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { useProfile } from '@/composables/useProfile';
import { useProfileForm } from '@/composables/useProfileForm';

const $q = useQuasar();

const { profileForm, password, confirmPassword, populateFrom, validatePasswords, buildUpdatePayload, clearPasswords } = useProfileForm();
const { isLoading, loadProfile, updateProfile } = useProfile();

onMounted(async () => {
    try {
        const currentMe = await loadProfile();
        if (currentMe) populateFrom(currentMe);
    } catch (error) {
        if (error instanceof Error && error.message === 'NOT_AUTHENTICATED') {
            $q.notify({ message: 'Lütfen işlem yapabilmek için önce giriş yapın.', color: 'negative' });
        } else if (error instanceof Error && error.message === 'PROFILE_INCOMPLETE') {
            $q.notify({ message: 'Profil detay bilgileri okunamadı.', color: 'warning' });
        } else {
            console.error('Profil yüklenirken hata oluştu:', error);
            $q.notify({ message: 'Profil bilgileri yüklenemedi.', color: 'negative' });
        }
    }
});

const handleUpdateProfile = async () => {
    const passwordError = validatePasswords();
    if (passwordError) {
        $q.notify({ message: passwordError, color: 'negative' });
        return;
    }

    try {
        await updateProfile(buildUpdatePayload());
        $q.notify({ message: 'Profil bilgileriniz başarıyla güncellendi.', color: 'positive' });
        clearPasswords();
    } catch (error) {
        console.error(error);
        $q.notify({ message: 'Profil güncellenirken bir hata oluştu.', color: 'negative' });
    }
};
</script>