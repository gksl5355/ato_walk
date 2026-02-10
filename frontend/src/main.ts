import { createApp } from 'vue'

import '@/styles/tokens.css'
import '@/styles/base.css'
import App from './App.vue'

import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'
import MeetupsPage from '@/pages/MeetupsPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/meetups', name: 'meetups', component: MeetupsPage },
  ],
})

createApp(App).use(createPinia()).use(router).mount('#app')
