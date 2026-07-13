// src/router/routes.ts
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'login',
    component: () => import('@/pages/LoginPage.vue'),
    meta: { requiresGuest: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/pages/DashboardPage.vue'),
      },
      {
        path: 'customers',
        name: 'customers',
        component: () => import('@/pages/CustomerPage.vue'),
      },
      {
        path: 'customer/:id',
        name: 'customer-detail',
        component: () => import('@/pages/CustomerDetailPage.vue'),
      },
      {
        path: 'policy',
        name: 'policy',
        component: () => import('@/pages/PolicyPage.vue'),
      },
    ],
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue'),
  },
];

export default routes;
