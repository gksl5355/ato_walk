import { createApp } from 'vue'

import '@/styles/tokens.css'
import '@/styles/base.css'
import App from './App.vue'

import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'
import MeetupsPage from '@/pages/MeetupsPage.vue'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toasts'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/meetups', name: 'meetups', component: MeetupsPage, meta: { requiresAuth: true } },
  ],
})

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

const auth = useAuthStore(pinia)
const toasts = useToastStore(pinia)

await auth.hydrate()

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    toasts.push({ tone: 'info', title: 'Login needed', message: 'Please login first.' })
    return { path: '/' }
  }
  return true
})

app.use(router)
app.mount('#app')
