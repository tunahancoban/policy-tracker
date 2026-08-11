<template>
    <q-layout view="lHh Lpr lFf">
        <q-page-container>
            <q-page class="login-bg flex flex-center q-pa-md">
                <q-card class="login-form-card q-pa-lg">
                    <q-card-section class="text-center q-pb-none">

                        <div class="login-brand-icon">
                            <q-icon name="shield" size="96px" color="primary" />
                        </div>
                        <div class="text-h5 text-weight-bold text-grey-9 tracking-wide">
                            Sigorta Poliçe Takip
                        </div>
                        <div class="text-caption text-grey-6 q-mt-xs">
                            Devam etmek için lütfen giriş yapın
                        </div>
                    </q-card-section>

                    <q-card-section>
                        <q-form @submit.prevent="handleLogin" class="q-gutter-y-md">
                            <q-input v-model="email" label="E-posta Adresi" type="email" outlined lazy-rules :rules="[
                                (val) => (val && val.length > 0) || 'E-posta alanı boş bırakılamaz',
                                (val) => /.+@.+\..+/.test(val) || 'Geçerli bir e-posta adresi giriniz',
                            ]" color="primary" :disable="isLoading">
                                <template v-slot:prepend>
                                    <q-icon name="email" color="primary" />
                                </template>
                            </q-input>

                            <q-input v-model="password" :type="isPwd ? 'password' : 'text'" label="Şifre" outlined
                                lazy-rules :rules="[(val) => (val && val.length > 0) || 'Şifre alanı boş bırakılamaz']"
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
                                    class="full-width text-weight-bold q-py-sm shadow-2" rounded unelevated
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
import { useAuthStore } from '../stores/auth';
import { Notify } from 'quasar';

const email = ref<string>('');
const password = ref<string>('');
const isLoading = ref<boolean>(false);
const isPwd = ref<boolean>(true);

const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
    isLoading.value = true;
    try {
        await authStore.login(email.value, password.value);
        await router.push({ name: 'dashboard' });
    } catch (error: unknown) {
        const errorMessage = error instanceof Error ? error.message : String(error);

        Notify.create({
            message: `Giriş yaparken bir hata oluştu: ${errorMessage}`,
            color: 'negative',
            icon: 'error',
            position: 'top-right',
            timeout: 5000,
        });
        console.error(error);
    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>
/* --- Login page --- */
.login-bg {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: url('../assets/login_background.jpg');
    background-size: cover;
}

.login-form-card {
    width: 100%;
    max-width: 420px;
    background-color: rgba(255, 255, 255, 0.95);
    border-radius: var(--border-radius-lg) !important;
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
    border: 4px solid #c1c0c0;
}
</style>