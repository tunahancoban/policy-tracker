// src/router/index.ts
import { defineRouter } from '#q-app';
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';

import { useQuasar } from 'quasar';

import routes from './routes';
import { useAuthStore } from '../stores/auth';

const $q = useQuasar();

export default defineRouter(({ store }) => {
  const createHistory = import.meta.env.QUASAR_SERVER
    ? createMemoryHistory
    : import.meta.env.QUASAR_VUE_ROUTER_MODE === 'history'
      ? createWebHistory
      : createWebHashHistory;

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(import.meta.env.QUASAR_VUE_ROUTER_BASE),
  });

  Router.beforeEach(async (to, from, next) => {
    //  KRİTİK DÜZELTME: Quasar'ın aktif Pinia store'unu parametre olarak içeri verdik.
    // Bu sayede F5 anında Pinia'nın hazır olduğundan %100 emin oluyoruz.
    const authStore = useAuthStore(store);

    // F5 atıldığında cookie kontrolü ile hafızayı tazele
    if (!authStore.isInitialized) {
      await authStore.checkAuth();
    }

    // Giriş gerektiren sayfa kontrolü
    if (to.meta.requiresAuth) {
      if (authStore.isAuthenticated) {
        next();
      } else {
        // Eğer isAuthenticated false ise ve istek bittiyse logine atar
        next({ name: 'login' });
      }
    } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
      next({ name: 'dashboard' });
    } else {
      next();
    }

    if (to.meta.requiresAdmin) {
      // Kullanıcı giriş yapmış mı VE rolü 'ADMIN' mi?
      if (authStore.isAuthenticated && authStore.userRole === 'ADMIN') {
        next();
      } else {
        // Admin değilse yetkisiz sayfa uyarısı verebilir veya Dashboard'a fırlatabilirsin
        $q.notify({ message: 'Bu sayfaya erişim yetkiniz bulunmamaktadır.', color: 'negative' });
        next({ name: 'dashboard' });
      }
    }
  });

  return Router;
});
