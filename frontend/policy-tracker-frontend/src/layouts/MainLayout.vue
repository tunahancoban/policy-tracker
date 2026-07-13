<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />
        <q-toolbar-title> Policy Tracker Panel </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header class="text-weight-bold text-uppercase text-grey-7">
          Yönetim Paneli
        </q-item-label>

        <q-item v-for="link in linksList" :key="link.label" clickable v-ripple :to="link.link"
          active-class="bg-blue-1 text-primary text-weight-bold">
          <q-item-section avatar>
            <q-icon :name="link.icon" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ link.label }}</q-item-label>
            <q-item-label caption class="text-grey-6">{{ link.caption }}</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface MenuLink {
  label: string;
  caption: string;
  icon: string;
  link: string;
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
  }
];

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
</script>
