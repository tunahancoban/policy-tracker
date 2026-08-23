<!-- src/pages/UserManagementPage.vue -->
<template>
    <q-page class="q-pa-md fade-in-up">
        <!-- Breadcrumb -->
        <div class="app-breadcrumb">
            <router-link to="/dashboard">Dashboard</router-link>
            <span class="separator">›</span>
            <span class="current">Kullanıcı Yönetimi</span>
        </div>

        <!-- Başlık Bölümü -->
        <div class="row items-center justify-between q-mb-md">
            <div class="text-h5 text-weight-bold  row items-center">
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
                        <q-chip color="primary" text-color="white" dense square class="status-chip">
                            {{ props.row.role }}
                        </q-chip>
                    </q-td>
                </template>
                <template v-slot:body-cell-actions="props">
                    <q-td :props="props" class="text-center q-gutter-xs">
                        <q-btn flat round color="primary" icon="edit" size="sm" @click="openEditDialog(props.row)">
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
            <q-card class="modal-card">
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
                            :error="!!fieldErrors.firstName" :error-message="fieldErrors.firstName"
                            @update:model-value="clearFieldError('firstName')"
                            :rules="[val => !!val || 'Ad alanı zorunludur']" />
                        <q-input v-model="form.lastName" label="Soyad *" outlined dense
                            :error="!!fieldErrors.lastName" :error-message="fieldErrors.lastName"
                            @update:model-value="clearFieldError('lastName')"
                            :rules="[val => !!val || 'Soyad alanı zorunludur']" />
                        <q-input v-model="form.email" label="E-posta *" outlined dense type="email"
                            :error="!!fieldErrors.email" :error-message="fieldErrors.email"
                            @update:model-value="clearFieldError('email')" :rules="[
                            val => !!val || 'E-posta alanı zorunludur',
                            val => /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/.test(val) || 'Geçerli bir e-posta girin'
                        ]" />

                        <template v-if="!isEditMode">
                            <q-input v-model="newPassword" label="Şifre *" outlined dense type="password"
                                :error="!!fieldErrors.password" :error-message="fieldErrors.password"
                                @update:model-value="clearFieldError('password')"
                                :rules="[val => !!val || 'Şifre alanı zorunludur']" />

                            <q-input v-model="form.password" label="Şifre (Yeniden) *" outlined dense type="password"
                                @update:model-value="clearFieldError('password')"
                                :rules="[
                                    val => !!val || 'Şifre tekrarı zorunludur',
                                    val => newPassword === val || 'Şifreler birbiriyle uyuşmuyor'
                                ]" />
                        </template>

                        <template v-if="isEditMode">
                            <q-toggle v-model="wantsPasswordChange" label="Şifreyi değiştirmek istiyorum" />

                            <template v-if="wantsPasswordChange">
                                <q-input v-model="newPassword" label="Yeni Şifre *" outlined dense type="password"
                                    :error="!!fieldErrors.password" :error-message="fieldErrors.password"
                                    @update:model-value="clearFieldError('password')"
                                    :rules="[val => !!val || 'Yeni şifre alanı zorunludur']" />

                                <q-input v-model="form.password" label="Yeni Şifre (Yeniden) *" outlined dense
                                    type="password"
                                    @update:model-value="clearFieldError('password')"
                                    :rules="[
                                        val => !!val || 'Şifre tekrarı zorunludur',
                                        val => newPassword === val || 'Şifreler birbiriyle uyuşmuyor'
                                    ]" />
                            </template>
                        </template>

                        <q-select v-model="form.role" outlined dense :options="userRoleOptions" label="Kullanıcı Rolü *"
                            :error="!!fieldErrors.role" :error-message="fieldErrors.role"
                            @update:model-value="clearFieldError('role')"
                            :rules="[val => !!val || 'Rol seçimi zorunludur']" />
                    </q-card-section>

                    <q-card-actions align="right" class="text-primary q-pb-md q-px-md">
                        <q-btn flat label="Vazgeç" v-close-popup class="text-weight-bold" />
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
import { useUserList } from '../composables/useUserList';
import { useNotify } from '../composables/useNotify';
import { useUserForm } from '../composables/useUserForm';
import { useConfirmDialog } from '../composables/useConfirmDialog';
import { userColumns, userRoleOptions, type User } from '../types/user.types';
import { ValidationError } from '../error/errors';

const { users, isLoading, loadUsers, addUser, updateUser, deleteUser } = useUserList();
const { notifySuccess, notifyError } = useNotify();
const { confirm } = useConfirmDialog();

const {
    form,
    newPassword,
    wantsPasswordChange,
    isEditMode,
    resetForCreate,
    resetForEdit,
    buildCreatePayload,
    buildUpdatePayload,
} = useUserForm();

const showDialog = ref(false);

// Backend'den dönen alan bazlı hatalar
const fieldErrors = ref<Record<string, string>>({});

const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
        delete fieldErrors.value[field];
    }
};

const openCreateDialog = () => {
    fieldErrors.value = {}; // Hataları temizle
    resetForCreate();
    showDialog.value = true;
};

const openEditDialog = (user: User) => {
    fieldErrors.value = {}; // Hataları temizle
    resetForEdit(user);
    showDialog.value = true;
};

const handleCreateUser = async () => {
    await addUser(buildCreatePayload());
    notifySuccess('Yeni kullanıcı başarıyla oluşturuldu.');
};

const handleUpdateUser = async () => {
    const patchData = buildUpdatePayload();

    if (!patchData) {
        showDialog.value = false;
        return;
    }

    await updateUser(patchData, form.value.id);
    notifySuccess('Kullanıcı bilgileri başarıyla güncellendi.');
};

const saveUser = async () => {
    fieldErrors.value = {}; // Önceki hataları sıfırla

    try {
        if (isEditMode.value) {
            await handleUpdateUser();
        } else {
            await handleCreateUser();
        }
        showDialog.value = false;
    } catch (error) {
        if (error instanceof ValidationError && error.errors) {
            fieldErrors.value = error.errors;
            return; // Modal açık kalsın, kullanıcı düzeltsin
        }

        console.error('Kullanıcı işlemi başarısız:', error);
        notifyError('İşlem sırasında bir hata oluştu.');
    }
};

const confirmDelete = async (user: User) => {
    const isConfirmed = await confirm({
        title: 'Kullanıcıyı Sil',
        message: `${user.firstName} ${user.lastName} adlı kullanıcıyı sistemden silmek istediğinize emin misiniz?`,
        okLabel: 'Sil',
        cancelLabel: 'Vazgeç',
        color: 'negative',
    });

    if (!isConfirmed) return;
    void handleDelete(user);
};

const handleDelete = async (user: User) => {
    try {
        await deleteUser(user.id);
        notifySuccess('Kullanıcı başarıyla silindi.');
    } catch (error) {
        console.error('Kullanıcı silinirken hata:', error);
        notifyError('Kullanıcı silinirken bir hata oluştu.');
    }
};

onMounted(() => {
    void loadUsers();
});
</script>