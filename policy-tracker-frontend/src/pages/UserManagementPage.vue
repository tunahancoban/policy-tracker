<!-- src/pages/UserManagementPage.vue -->
<template>
    <q-page class="q-pa-md">
        <!-- Başlık Bölümü -->
        <div class="row items-center justify-between q-mb-md">
            <div class="text-h5 text-weight-bold text-grey-8 row items-center">
                <q-icon name="manage_accounts" color="primary" class="q-mr-sm" size="32px" />
                Sistem Kullanıcıları Yönetimi
            </div>
            <q-btn color="primary" icon="add" label="Yeni Kullanıcı Ekle" unelevated @click="openCreateDialog" />
        </div>

        <q-card flat bordered>
            <q-table flat :rows="users" :columns="userColumns" row-key="id" :loading="isLoading"
                no-data-label="Sistemde kayıtlı kullanıcı bulunamadı." loading-label="Kullanıcı listesi getiriliyor...">
                <template v-slot:body-cell-role="props">
                    <q-td :props="props" class="text-center">
                        <q-chip color='primary' text-color="white" dense square class="text-weight-bold text-caption">
                            {{ props.row.role }}
                        </q-chip>
                    </q-td>
                </template>
                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="text-center q-gutter-xs">
                        <q-btn flat round color="secondary" icon="edit" size="sm" @click="openEditDialog(props.row)">
                            <q-tooltip>Kullanıcıyı Düzenle</q-tooltip>
                        </q-btn>
                        <q-btn flat round color="negative" icon="delete" size="sm" @click="confirmDelete(props.row)">
                            <q-tooltip>Kullanıcıyı Sil</q-tooltip>
                        </q-btn>
                    </q-td>
                </template>
            </q-table>
        </q-card>

        <q-dialog v-model="showDialog">
            <q-card style="min-width: 400px;">
                <q-card-section class="row items-center q-pb-none">
                    <div class="text-h6 text-weight-bold text-grey-8">
                        {{ isEditMode ? 'Kullanıcı Bilgilerini Güncelle' : 'Yeni Kullanıcı Oluştur' }}
                    </div>
                    <q-space />
                    <q-btn icon="close" flat round dense v-close-popup />
                </q-card-section>

                <q-form @submit="saveUser">
                    <q-card-section class="q-gutter-sm q-pt-md">
                        <q-input v-model="form.firstName" label="Ad *" outlined dense
                            :rules="[val => !!val || 'Ad alanı zorunludur']" />
                        <q-input v-model="form.lastName" label="Soyad *" outlined dense
                            :rules="[val => !!val || 'Soyad alanı zorunludur']" />
                        <q-input v-model="form.email" label="E-posta *" outlined dense type="email" :rules="[
                            val => !!val || 'E-posta alanı zorunludur',
                            val => /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/.test(val) || 'Geçerli bir e-posta girin'
                        ]" />

                        <template v-if="!isEditMode">
                            <q-input v-model="newPassword" label="Şifre *" outlined dense type="password"
                                :rules="[val => !!val || 'Şifre alanı zorunludur']" />

                            <q-input v-model="form.password" label="Şifre (Yeniden) *" outlined dense type="password"
                                :rules="[
                                    val => !!val || 'Şifre tekrarı zorunludur',
                                    val => newPassword === val || 'Şifreler birbiriyle uyuşmuyor'
                                ]" />
                        </template>

                        <template v-if="isEditMode">
                            <q-toggle v-model="wantsPasswordChange" label="Şifreyi değiştirmek istiyorum" />

                            <template v-if="wantsPasswordChange">
                                <q-input v-model="newPassword" label="Yeni Şifre *" outlined dense type="password"
                                    :rules="[val => !!val || 'Yeni şifre alanı zorunludur']" />

                                <q-input v-model="form.password" label="Yeni Şifre (Yeniden) *" outlined dense
                                    type="password" :rules="[
                                        val => !!val || 'Şifre tekrarı zorunludur',
                                        val => newPassword === val || 'Şifreler birbiriyle uyuşmuyor'
                                    ]" />
                            </template>
                        </template>

                        <q-select v-model="form.role" outlined dense :options="userRoleOptions" label="Kullanıcı Rolü *"
                            :rules="[val => !!val || 'Rol seçimi zorunludur']" />
                    </q-card-section>

                    <q-card-actions align="right" class="text-primary q-pb-md q-px-md">
                        <q-btn flat label="Vazgeç" v-close-popup color="grey" class="text-weight-bold" />
                        <q-btn unelevated type="submit" :label="isEditMode ? 'Güncelle' : 'Kaydet'" color="primary"
                            class="text-weight-bold q-px-md" :loading="isLoading" />
                    </q-card-actions>
                </q-form>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { useUserList } from '@/composables/useUserList';
import { userColumns, userRoleOptions } from '@/types/user.types';
import type { User, RegisterRequest, UserForm, UpdateUserRequest } from '@/types/user.types';

const { users, isLoading, loadUsers, addUser, updateUser, deleteUser } = useUserList();
const $q = useQuasar();
const wantsPasswordChange = ref(false);


const originalUser = ref<User | null>(null);
const showDialog = ref(false);
const isEditMode = ref(false);
const newPassword = ref('');


const initialForm: UserForm = {
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'ROLE_USER',
};

const form = ref<UserForm>({ ...initialForm });

const openCreateDialog = () => {
    isEditMode.value = false;
    form.value = { ...initialForm };
    newPassword.value = '';
    wantsPasswordChange.value = false; // eklendi
    showDialog.value = true;
};


const openEditDialog = (user: User) => {
    isEditMode.value = true;
    originalUser.value = user; // orijinali sakla
    form.value = {
        id: user.id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        role: user.role,
    };
    newPassword.value = '';
    wantsPasswordChange.value = false;
    showDialog.value = true;
};

const handleCreateUser = async () => {
    const payload: RegisterRequest = {
        firstName: form.value.firstName,
        lastName: form.value.lastName,
        email: form.value.email,
        password: form.value.password!,
        role: form.value.role,
    };
    await addUser(payload);
    $q.notify({ message: 'Yeni kullanıcı başarıyla oluşturuldu.', color: 'positive' });
};

const handleUpdateUser = async () => {
    if (!form.value.id || !originalUser.value) {
        throw new Error('Güncellenecek kullanıcı bilgisi eksik.');
    }

    const patchData: UpdateUserRequest = {};

    if (form.value.firstName !== originalUser.value.firstName) {
        patchData.firstName = form.value.firstName;
    }
    if (form.value.lastName !== originalUser.value.lastName) {
        patchData.lastName = form.value.lastName;
    }
    if (form.value.email !== originalUser.value.email) {
        patchData.email = form.value.email;
    }
    if (form.value.role !== originalUser.value.role) {
        patchData.role = form.value.role;
    }
    if (wantsPasswordChange.value && form.value.password) {
        patchData.password = form.value.password;
    }

    // Hiçbir şey değişmemişse gereksiz istek atma
    if (Object.keys(patchData).length === 0) {
        showDialog.value = false;
        return;
    }

    await updateUser(patchData, form.value.id);
    $q.notify({ message: 'Kullanıcı bilgileri başarıyla güncellendi.', color: 'positive' });
};

const saveUser = async () => {
    try {
        if (isEditMode.value) {
            await handleUpdateUser();
        } else {
            await handleCreateUser();
        }
        showDialog.value = false;
    } catch (error) {
        console.error('Kullanıcı işlemi başarısız:', error);
        $q.notify({ message: 'İşlem sırasında bir hata oluştu.', color: 'negative' });
    }
};

const confirmDelete = (user: User) => {
    $q.dialog({
        title: 'Kullanıcıyı Sil',
        message: `${user.firstName} ${user.lastName} adlı kullanıcıyı sistemden silmek istediğinize emin misiniz?`,
        cancel: { label: 'Vazgeç', flat: true, color: 'grey' },
        ok: { label: 'Sil', flat: true, color: 'negative' },
    }).onOk(() => {
        void handleDelete(user);
    });
};

const handleDelete = async (user: User) => {
    try {
        await deleteUser(user.id);
        $q.notify({ message: 'Kullanıcı başarıyla silindi.', color: 'positive' });
    } catch (error) {
        console.error('Kullanıcı silinirken hata:', error);
        $q.notify({ message: 'Kullanıcı silinirken bir hata oluştu.', color: 'negative' });
    }
};

onMounted(() => {
    void loadUsers();
});
</script>