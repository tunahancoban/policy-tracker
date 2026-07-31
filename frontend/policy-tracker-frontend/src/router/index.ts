// src/router/index.ts
import { defineRouter } from '#q-app';
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';

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
      try {
        await authStore.checkAuth();
      } catch (error) {
        console.error('CheckAuth hatası:', error);
      }
    }

    if (to.meta.requiresGuest && authStore.isAuthenticated) {
      return next({ name: 'dashboard' });
    }

    if ((to.meta.requiresAuth || to.meta.requiresAdmin) && !authStore.isAuthenticated) {
      return next({ name: 'login' });
    }

    if (to.meta.requiresAdmin && authStore.userRole !== 'ROLE_ADMIN') {
      Notify.create({
        message: 'Bu sayfaya erişim yetkiniz bulunmamaktadır.',
        color: 'negative',
      });

      if (to.name !== 'dashboard') {
        return next({ name: 'dashboard' });
      }
    }

    return next();
  });

  return Router;
});
