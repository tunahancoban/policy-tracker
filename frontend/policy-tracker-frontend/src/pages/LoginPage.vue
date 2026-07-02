<template>
    <q-layout view="lHh Lpr lFf">
        <q-page-container>

            <q-page class="login-bg flex flex-center q-pa-md">
                <q-card class="login-card shadow-24 q-pa-lg">

                    <q-card-section class="text-center q-pb-none">
                        <div class="text-h5 text-weight-bold text-grey-9 tracking-wide">Sigorta Poliçe Takip</div>
                        <div class="text-caption text-grey-6 q-mt-xs">Devam etmek için lütfen giriş yapın</div>
                    </q-card-section>

                    <q-card-section>
                        <q-form @submit.prevent="handleLogin" class="q-gutter-y-md">

                            <q-input v-model="email" label="E-posta Adresi" type="email" outlined lazy-rules :rules="[
                                val => val && val.length > 0 || 'E-posta alanı boş bırakılamaz',
                                val => /.+@.+\..+/.test(val) || 'Geçerli bir e-posta adresi giriniz']" color="primary"
                                :disable="isLoading">
                                <template v-slot:prepend>
                                    <q-icon name="email" color="primary" />
                                </template>
                            </q-input>

                            <q-input v-model="password" :type="isPwd ? 'password' : 'text'" label="Şifre" outlined
                                lazy-rules :rules="[val => val && val.length > 0 || 'Şifre alanı boş bırakılamaz']"
                                color="primary" :disable="isLoading">
                                <template v-slot:prepend>
                                    <q-icon name="lock" color="primary" />
                                </template>
                                <template v-slot:append>
                                    <q-icon :name="isPwd ? 'visibility_off' : 'visibility'" class="cursor-pointer"
                                        color="grey-7" @click="isPwd = !isPwd" />
                                </template>
                            </q-input>

                            <div class="q-mt-xl">
                                <q-btn label="Giriş Yap" type="submit" color="primary"
                                    class="full-width text-weight-bold q-py-sm shadow-2" rounded unevaluated
                                    :loading="isLoading" />
                            </div>

                        </q-form>
                    </q-card-section>

                </q-card>
            </q-page>

        </q-page-container>
    </q-layout>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import { api } from '../boot/axios';
import { useAuthStore } from '../stores/auth';

const email = ref<string>('');
const password = ref<string>('');
const isLoading = ref<boolean>(false);
const isPwd = ref<boolean>(true);

const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
    try {
        isLoading.value = true;
        const response = await api.post('/rest/api/auth/login-request', {
            email: email.value,
            password: password.value
        });

        const restResponse = response.data;
        if (restResponse.success && restResponse.data) {
            const { token, role, userEmail } = restResponse.data;
            authStore.saveLoginData({ token, role, userEmail });

            // Yönlendirme öncesi çökme riskini sıfırlamak için alert kullandık
            await router.push('/dashboard');
        } else {
            alert('Giriş başarısız: ' + (restResponse.message || 'Bilinmeyen hata'));
        }

    } catch (error) {
        console.error('An unexpected error occurred:', error);
        alert('Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>
.login-bg {
    background-image: url('../assets/login_background.jpg');
    background-size: cover;
    background-position: center;
}

.login-card {
    width: 200%;
    max-width: 400px;
    background-color: #ffffff;
}

.tracking-wide {
    letter-spacing: 0.7px;
}
</style>