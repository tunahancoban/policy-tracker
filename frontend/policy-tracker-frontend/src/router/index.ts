// src/router/index.ts
import { defineRouter } from '#q-app';
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';

// useQuasar yerine direkt Notify eklentisini import ediyoruz (Bileşen dışı kullanım için doğrusu budur)
import { Notify } from 'quasar';

import routes from './routes';
import { useAuthStore } from '../stores/auth';

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
    const authStore = useAuthStore(store);

    if (!authStore.isInitialized) {
      await authStore.checkAuth();
    }

    if (to.meta.requiresAdmin) {
      if (authStore.isAuthenticated && authStore.userRole === 'ROLE_ADMIN') {
        return next();
      } else {
        Notify.create({
          message: 'Bu sayfaya erişim yetkiniz bulunmamaktadır.',
          color: 'negative',
        });
        return next({ name: 'dashboard' });
      }
    }

    if (to.meta.requiresAuth) {
      if (authStore.isAuthenticated) {
        return next();
      } else {
        return next({ name: 'login' });
      }
    }

    if (to.meta.requiresGuest && authStore.isAuthenticated) {
      return next({ name: 'dashboard' });
    }

    return next();
  });

  return Router;
});
