<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />
        <q-toolbar-title> Poliçe </q-toolbar-title>


        <NotificationMenu />

        <!-- Dark Mode Toggle -->
        <q-btn flat dense round :icon="isDarkMode ? 'light_mode' : 'dark_mode'"
          :aria-label="isDarkMode ? 'Aydınlık Mod' : 'Karanlık Mod'" @click="toggleDarkMode"
          class="dark-mode-btn q-mr-xs">
          <q-tooltip>{{ isDarkMode ? 'Aydınlık Moda Geç' : 'Karanlık Moda Geç' }}</q-tooltip>
        </q-btn>

        <q-btn flat dense round icon="logout" aria-label="Logout" @click="isLogoutDialogOpen = true" />

        <q-dialog v-model="isLogoutDialogOpen">
          <q-card class="modal-card" style="min-width: 350px">
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
        <q-item-label header class="text-weight-bold text-uppercase">
          Yönetim Paneli
        </q-item-label>

        <q-separator class="sidebar-separator" />

        <template v-for="(link, idx) in linksList" :key="link.label">
          <q-separator v-if="idx === 3" class="sidebar-separator" />
          <q-item v-if="!link.requiresAdmin || isAdmin" clickable v-ripple :to="link.link"
            :active-class="'sidebar-item--active'">
            <q-item-section avatar>
              <q-icon :name="link.icon" />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ link.label }}</q-item-label>
              <q-item-label caption>{{ link.caption }}</q-item-label>
            </q-item-section>
          </q-item>
        </template>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </q-page-container>

    <!-- Scroll to Top FAB -->
    <transition name="scroll-top-fade">
      <q-btn v-show="showScrollTop" fab icon="keyboard_arrow_up" color="primary" class="scroll-to-top-btn"
        aria-label="Yukarı Çık" @click="scrollToTop">
        <q-tooltip>Yukarı Çık</q-tooltip>
      </q-btn>
    </transition>

  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useQuasar } from 'quasar';
import { useAuthStore } from '../stores/auth';
import { useNotificationStore } from '@/stores/notification';
import { useRouter } from 'vue-router';
import { useWebSocket } from '@/composables/useWebSocket';
import NotificationMenu from '@/components/NotificationMenu.vue';
import type { Notification } from '@/types/notification.types';

const router = useRouter();
const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const isLogoutDialogOpen = ref(false);
const $q = useQuasar();

const wsState = useWebSocket();
const { connect, disconnect } = wsState;

// --- Dark Mode ---
const isDarkMode = ref(localStorage.getItem('darkMode') === 'true');



function toggleDarkMode() {
  $q.dark.set(!$q.dark.isActive)
  isDarkMode.value = !isDarkMode.value;
}

// --- Scroll to Top ---
const showScrollTop = ref(false);

function handleScroll() {
  showScrollTop.value = window.scrollY > 300;
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

onMounted(async () => {
  window.addEventListener('scroll', handleScroll);

  await Promise.all([
    notificationStore.fetchNotifications(),
    notificationStore.fetchUnreadCount(),
  ]);

  connect({
    onDashboardUpdate: (data: string) => {
      console.log('WS Dashboard Signal Received:', data);
    },
    onNotificationReceived: (newNotification: Notification) => {
      notificationStore.addFromSocket(newNotification);
    }
  });
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

interface MenuLink {
  label: string;
  caption: string;
  icon: string;
  link: string;
  requiresAdmin?: boolean;
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
    disconnect();
    notificationStore.reset(); // logout'ta bildirim state'i temizlenmeli
    await authStore.logout();
    await router.push({ name: 'login' });
  } catch (error) {
    console.error('Çıkış yapılırken hata oluştu:', error);
  }
};
</script>

<style scoped>
/* Dark mode toggle butonu hover efekti */
.dark-mode-btn {
  transition: transform 0.3s ease, opacity 0.2s ease;
}

.dark-mode-btn:hover {
  transform: rotate(20deg);
  opacity: 0.85;
}

/* Scroll to top butonu konumu */
.scroll-to-top-btn {
  position: fixed;
  bottom: 28px;
  right: 28px;
  z-index: 9999;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.scroll-to-top-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.28);
}

/* Scroll top butonunun giriş/çıkış animasyonu */
.scroll-top-fade-enter-active,
.scroll-top-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.scroll-top-fade-enter-from,
.scroll-top-fade-leave-to {
  opacity: 0;
  transform: translateY(12px);
}
</style>