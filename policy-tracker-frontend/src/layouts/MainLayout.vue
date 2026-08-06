<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />
        <q-toolbar-title> Policy Tracker Panel </q-toolbar-title>
        <q-btn flat dense round icon="logout" aria-label="Logout" @click="isLogoutDialogOpen = true" />

        <q-dialog v-model="isLogoutDialogOpen">
          <q-card style="min-width: 350px">
            <q-card-section class="row items-center">
              <q-avatar icon="logout" color="primary" text-color="white" />
              <span class="q-ml-sm text-weight-bold text-subtitle1">Oturumu Kapat</span>
            </q-card-section>

            <q-card-section class="q-pt-none text-grey-8">
              Sistemden çıkış yapmak istediğinize emin misiniz?
            </q-card-section>

            <q-card-actions align="right" class="text-primary">
              <q-btn flat label="İptal" color="grey" v-close-popup />
              <q-btn flat label="Çıkış Yap" color="negative" @click="toggleLogout" />
            </q-card-actions>
          </q-card>
        </q-dialog>

      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header class="text-weight-bold text-uppercase text-grey-7">
          Yönetim Paneli
        </q-item-label>

        <template v-for="link in linksList" :key="link.label">
          <q-item v-if="!link.requiresAdmin || isAdmin" clickable v-ripple :to="link.link"
            active-class="bg-blue-1 text-primary text-weight-bold">
            <q-item-section avatar>
              <q-icon :name="link.icon" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ link.label }}</q-item-label>
              <q-item-label caption class="text-grey-6">{{ link.caption }}</q-item-label>
            </q-item-section>
          </q-item>
        </template>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { computed } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';


const router = useRouter();
const authStore = useAuthStore();
const isLogoutDialogOpen = ref(false);

interface MenuLink {
  label: string;
  caption: string;
  icon: string;
  link: string;
  requiresAdmin?: boolean
}

const linksList: MenuLink[] = [
  {
    label: 'Dashboard',
    caption: 'Sistem özet grafiklerinin yer aldığı ana sayfa.',
    icon: 'dashboard',
    link: '/dashboard',
  },
  {
    label: 'Müşteriler',
    caption: 'Müşteri ekleme, listeleme ve detay yönetimi.',
    icon: 'people',
    link: '/customers',
  },
  {
    label: 'Poliçeler',
    caption: 'Poliçe yönetim',
    icon: 'policy',
    link: '/policy'
  },
  {
    label: 'Profil Yönetimi',
    caption: 'Profil yönetim',
    icon: 'settings',
    link: '/profile'

  },
  {
    label: 'Kullanıcı Yönetimi',
    caption: 'Kullanıcı yönetim',
    icon: 'account_circle',
    link: '/users',
    requiresAdmin: true
  }
];

const leftDrawerOpen = ref(false);
const isAdmin = computed(() => authStore.userRole === 'ROLE_ADMIN');
function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
const toggleLogout = async (): Promise<void> => {
  try {
    isLogoutDialogOpen.value = false;
    await authStore.logout();
    await router.push({ name: 'login' });
  } catch (error) {
    console.error('Çıkış yapılırken hata oluştu:', error);
  }
};
</script>
