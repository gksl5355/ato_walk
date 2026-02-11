import { createApp } from 'vue'

import '@/styles/tokens.css'
import '@/styles/base.css'
import App from './App.vue'

import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'
import MeetupsPage from '@/pages/MeetupsPage.vue'
import ShopCartPage from '@/pages/shop/ShopCartPage.vue'
import ShopOrderDetailPage from '@/pages/shop/ShopOrderDetailPage.vue'
import ShopOrdersPage from '@/pages/shop/ShopOrdersPage.vue'
import ShopProductDetailPage from '@/pages/shop/ShopProductDetailPage.vue'
import ShopProductsPage from '@/pages/shop/ShopProductsPage.vue'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toasts'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/meetups', name: 'meetups', component: MeetupsPage, meta: { requiresAuth: true } },
    { path: '/shop', name: 'shop-products', component: ShopProductsPage },
    { path: '/shop/products/:productId', name: 'shop-product-detail', component: ShopProductDetailPage },
    { path: '/shop/cart', name: 'shop-cart', component: ShopCartPage, meta: { requiresAuth: true } },
    { path: '/shop/orders', name: 'shop-orders', component: ShopOrdersPage, meta: { requiresAuth: true } },
    {
      path: '/shop/orders/:orderId',
      name: 'shop-order-detail',
      component: ShopOrderDetailPage,
      meta: { requiresAuth: true },
    },
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
