<!-- src/pages/UserManagementPage.vue -->
<template>
    <q-page class="q-pa-md">
        <!-- Başlık Bölümü -->
        <div class="row items-center justify-between q-mb-md">
            <div class="text-h5 text-weight-bold text-grey-8 row items-center">
                <q-icon name="manage_accounts" color="primary" class="q-mr-sm" size="32px" />
                Sistem Kullanıcıları Yönetimi
            </div>
            <!-- Sadece Adminin Kullanabileceği Ekleme Butonu -->
            <q-btn color="primary" icon="add" label="Yeni Kullanıcı Ekle" unelevated @click="openCreateDialog" />
        </div>

        <!-- Tablo Bölümü -->
        <q-card flat bordered>
            <q-table flat :rows="userStore.users" :columns="userColumns" row-key="id" :loading="userStore.isLoading"
                no-data-label="Sistemde kayıtlı kullanıcı bulunamadı." loading-label="Kullanıcı listesi getiriliyor...">
                <!-- Rol Hücresi (Badge tasarımı ile) -->
                <template v-slot:body-cell-role="props">
                    <q-td :props="props" class="text-center">
                        <q-chip :color="props.row.role === 'ROLE_ADMIN' ? 'red-2' : 'blue-2'"
                            :text-color="props.row.role === 'ROLE_ADMIN' ? 'red-9' : 'blue-9'" dense square
                            class="text-weight-bold text-caption">
                            {{ props.row.role }}
                        </q-chip>
                    </q-td>
                </template>

                <!-- İşlemler Hücresi (Düzenleme ve Silme Butonları) -->
                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="text-center q-gutter-xs">
                        <!-- Düzenleme Butonu -->
                        <q-btn flat round color="secondary" icon="edit" size="sm" @click="openEditDialog(props.row)">
                            <q-tooltip>Kullanıcıyı Düzenle</q-tooltip>
                        </q-btn>

                        <!-- Silme Butonu -->
                        <q-btn flat round color="negative" icon="delete" size="sm" @click="confirmDelete(props.row)">
                            <q-tooltip>Kullanıcıyı Sil</q-tooltip>
                        </q-btn>
                    </q-td>
                </template>
            </q-table>
        </q-card>

        <!-- Tek Parça İç Diyalog (Hem Ekleme Hem Güncelleme İçin) -->
        <q-dialog v-model="showDialog" persistent>
            <q-card style="min-width: 400px;">
                <q-card-section class="row items-center q-pb-none">
                    <div class="text-h6 text-weight-bold text-grey-8">
                        {{ isEditMode ? 'Kullanıcı Bilgilerini Güncelle' : 'Yeni Kullanıcı Oluştur' }}
                    </div>
                    <q-space />
                    <q-btn icon="close" flat round dense v-close-popup />
                </q-card-section>

                <q-card-section class="q-gutter-sm q-pt-md">
                    <q-input v-model="form.firstName" label="Ad *" outlined dense
                        :rules="[val => !!val || 'Ad alanı zorunludur']" />
                    <q-input v-model="form.lastName" label="Soyad *" outlined dense
                        :rules="[val => !!val || 'Soyad alanı zorunludur']" />
                    <q-input v-model="form.email" label="E-posta *" outlined dense type="email" :rules="[
                        val => !!val || 'E-posta alanı zorunludur',
                        val => /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/.test(val) || 'Geçerli bir e-posta girin'
                    ]" />
                    <q-input v-model="newPassword" label="Şifre *" outlined dense type="password"
                        :rules="[val => !!val || 'Şifre alanı zorunludur']" />

                    <q-input v-model="form.password" label="Şifre (Yeniden) *" outlined dense type="password"
                        :rules="[val => newPassword === form.password || 'Şifre aynı olmak zorunda']" />

                    <q-select v-model="form.role" outlined dense :options="['ROLE_ADMIN', 'ROLE_USER']"
                        label="Kullanıcı Rolü *" :rules="[val => !!val || 'Rol seçimi zorunludur']" />
                </q-card-section>

                <q-card-actions align="right" class="text-primary q-pb-md q-px-md">
                    <q-btn flat label="Vazgeç" v-close-popup color="grey" class="text-weight-bold" />
                    <q-btn unelevated :label="isEditMode ? 'Güncelle' : 'Kaydet'" color="primary"
                        class="text-weight-bold q-px-md" @click="saveUser" :loading="userStore.isLoading" />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { useUserStore } from '../stores/user';
import { userColumns } from '../types/user.types';
import type { User, RegisterRequest } from '../types/user.types';

const userStore = useUserStore();
const $q = useQuasar();

// Diyalog ve Form Kontrolleri
const showDialog = ref(false);
const isEditMode = ref(false);
const newPassword = ref('');

interface UserForm {
    id?: string;
    firstName: string;
    lastName: string;
    email: string;
    password?: string;
    role: 'ROLE_ADMIN' | 'ROLE_USER';
}

const initialForm: UserForm = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'ROLE_USER'
};

const form = ref<UserForm>({ ...initialForm });

// Yeni Kullanıcı Ekleme Penceresini Aç
const openCreateDialog = () => {
    isEditMode.value = false;
    form.value = { ...initialForm };
    showDialog.value = true;
};

// Düzenleme Penceresini Aç
const openEditDialog = (user: User) => {
    isEditMode.value = true;
    form.value = {
        id: user.id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        role: user.role
    };
    showDialog.value = true;
};

// Kaydetme/Güncelleme Ana Tetikleyicisi
const saveUser = async () => {
    if (!form.value.firstName || !form.value.lastName || !form.value.email || (!isEditMode.value && !form.value.password)) {
        $q.notify({ message: 'Lütfen zorunlu alanları eksiksiz doldurun.', color: 'warning' });
        return;
    }

    try {
        if (isEditMode.value) {
            await userStore.updateUser(form.value as User);
            newPassword.value = '';
            $q.notify({ message: 'Kullanıcı bilgileri başarıyla güncellendi.', color: 'positive' });
        } else {
            const registerPayload: RegisterRequest = {
                firstName: form.value.firstName,
                lastName: form.value.lastName,
                email: form.value.email,
                password: form.value.password!,
                role: form.value.role
            };
            await userStore.addUser(registerPayload);
            $q.notify({ message: 'Yeni kullanıcı başarıyla oluşturuldu.', color: 'positive' });
        }
        showDialog.value = false;
        void userStore.fetchUsers(); // Tabloyu tazeleyelim
    } catch (error) {
        console.error('Kullanıcı işlemi başarısız:', error);
        $q.notify({ message: 'İşlem sırasında bir hata oluştu.', color: 'negative' });
    }
};

// Silme Onay Penceresi
const confirmDelete = (user: User) => {
    $q.dialog({
        title: 'Kullanıcıyı Sil',
        message: `${user.firstName} ${user.lastName} adlı kullanıcıyı sistemden silmek istediğinize emin misiniz?`,
        cancel: { label: 'Vazgeç', flat: true, color: 'grey' },
        ok: { label: 'Sil', flat: true, color: 'negative' },
        persistent: true
    }).onOk(() => {
        void (async () => {
            try {
                await userStore.deleteUser(user.id);
                $q.notify({ message: 'Kullanıcı başarıyla silindi.', color: 'positive' });
            } catch (error) {
                console.log(error);
                $q.notify({ message: 'Kullanıcı silinirken bir hata oluştu.', color: 'negative' });
            }
        })();
    });
};

onMounted(() => {
    void userStore.fetchUsers();
});
</script>