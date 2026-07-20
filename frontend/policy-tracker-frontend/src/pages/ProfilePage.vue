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

                    <!-- Şifre Değiştirme Alanı (Sadece Yeni Şifre) -->
                    <div class="text-subtitle1 text-grey-7 text-weight-bold">Şifre Değiştir (İsteğe Bağlı)</div>
                    <p class="text-caption text-grey-6 q-mt-none">Şifrenizi değiştirmek istemiyorsanız bu alanları boş
                        bırakabilirsiniz.</p>

                    <q-input v-model="profileForm.password" label="Yeni Şifre" outlined dense type="password"
                        :rules="[val => !val || val.length >= 6]" />
                    <q-input v-model="confirmPassword" label="Yeni Şifre (Tekrar)" outlined dense type="password"
                        :rules="[val => val === profileForm.password || 'Şifreler birbiriyle uyuşmuyor']" />

                    <!-- Kaydetme Butonu -->
                    <div class="row justify-end q-mt-md">
                        <q-btn color="primary" label="Değişiklikleri Kaydet" type="submit" unelevated
                            :loading="userStore.isLoading" />
                    </div>
                </q-form>
            </q-card>
        </div>
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { useUserStore } from '../stores/user';
import { useAuthStore } from '../stores/auth';

const userStore = useUserStore();
const authStore = useAuthStore();
const $q = useQuasar();

const profileForm = ref({
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    password: ''
});

const confirmPassword = ref('');

onMounted(async () => {
    try {
        // 1. Önce oturumu tazele/doğrula
        await authStore.checkAuth();

        if (authStore.isAuthenticated) {
            // 2. Profil bilgilerini getiren store metodunu çağır 
            // (Metot ismini projene göre güncelleyebilirsin, örn: userStore.fetchProfile())
            const currentMe = await userStore.fetchProfile();

            // 3. Backend'den nesne geldi mi kontrol et ve direkt forma bas
            if (currentMe && currentMe.email) {
                profileForm.value.id = currentMe.id;
                profileForm.value.firstName = currentMe.firstName;
                profileForm.value.lastName = currentMe.lastName;
                profileForm.value.email = currentMe.email;
            } else {
                $q.notify({ message: 'Profil detay bilgileri okunamadı.', color: 'warning' });
            }
        } else {
            $q.notify({ message: 'Lütfen işlem yapabilmek için önce giriş yapın.', color: 'negative' });
        }
    } catch (error) {
        console.error('Profil yüklenirken hata oluştu:', error);
        $q.notify({ message: 'Profil bilgileri yüklenemedi.', color: 'negative' });
    }
});


const handleUpdateProfile = async () => {
    if (profileForm.value.password && profileForm.value.password !== confirmPassword.value) {
        $q.notify({ message: 'Girdiğiniz şifreler birbiriyle eşleşmiyor.', color: 'negative' });
        return;
    }

    const updatesPayload: Record<string, unknown> = {
        firstName: profileForm.value.firstName,
        lastName: profileForm.value.lastName,
        email: profileForm.value.email
    };

    if (profileForm.value.password) {
        updatesPayload.password = profileForm.value.password;
    }

    try {
        await userStore.updateMyProfile(updatesPayload);

        $q.notify({ message: 'Profil bilgileriniz başarıyla güncellendi.', color: 'positive' });

        profileForm.value.password = '';
        confirmPassword.value = '';
    } catch (error) {
        console.error(error);
        $q.notify({ message: 'Profil güncellenirken bir hata oluştu.', color: 'negative' });
    }
};
</script>