<template>
  <section class="head">
    <div>
      <h1 class="title">Cart</h1>
      <p class="sub">Your current shopping cart items.</p>
    </div>
    <div class="head__actions">
      <RouterLink to="/shop"><UiButton size="sm" variant="soft">Products</UiButton></RouterLink>
      <RouterLink to="/shop/orders"><UiButton size="sm" variant="ghost">Orders</UiButton></RouterLink>
    </div>
  </section>

  <section v-if="state.loading" class="loading">Loading cart...</section>
  <section v-else>
    <div v-if="state.items.length === 0" class="empty">Cart is empty.</div>

    <div v-else class="list">
      <CartItemRow v-for="item in state.items" :key="item.id" :item="item" :busy="state.busy" @update="onUpdate" @remove="onRemove" />
    </div>

    <UiCard class="summary">
      <div class="paymentSection">
        <div class="paymentSection__methods">
          <button :class="['methodButton', { 'methodButton--active': state.selectedPaymentMethod === 'NAVER' }]" type="button" @click="state.selectedPaymentMethod = 'NAVER'">
            Naver Pay
          </button>
          <button :class="['methodButton', { 'methodButton--active': state.selectedPaymentMethod === 'KAKAO' }]" type="button" @click="state.selectedPaymentMethod = 'KAKAO'">
            Kakao Pay
          </button>
          <button :class="['methodButton', { 'methodButton--active': state.selectedPaymentMethod === 'CARD' }]" type="button" @click="state.selectedPaymentMethod = 'CARD'">
            Card
          </button>
          <button :class="['methodButton', { 'methodButton--active': state.selectedPaymentMethod === 'POINT' }]" type="button" @click="state.selectedPaymentMethod = 'POINT'">
            Point
          </button>
        </div>

        <div class="paymentPanel">
          <template v-if="state.selectedPaymentMethod === 'POINT'">
            <div class="summary__grid">
              <div>
                <div class="summary__label">Point balance</div>
                <div class="summary__value" :class="{ 'summary__value--error': state.pointError }">
                  {{ state.pointLoading ? 'Loading...' : pointBalanceText }}
                </div>
              </div>
              <div>
                <div class="summary__label">Total</div>
                <div class="summary__value">{{ formatPrice(totalPrice) }}</div>
              </div>
              <div>
                <div class="summary__label">After payment</div>
                <div class="summary__value" :class="{ 'summary__value--error': remainingBalance < 0 }">
                  {{ formatPrice(Math.max(remainingBalance, 0)) }}
                </div>
              </div>
              <div>
                <label class="summary__label" for="pointToPay">Points to pay</label>
                <input id="pointToPay" v-model="state.pointToPayInput" class="pointInput" type="number" min="0" :disabled="state.busy" />
              </div>
              <p v-if="!isPointAmountExact" class="summary__warn">Enter exactly {{ formatPrice(totalPrice) }} to pay with points.</p>
              <p v-if="remainingBalance < 0" class="summary__warn">Not enough points for this order.</p>
              <p v-else-if="state.pointError" class="summary__warn">Point balance is unavailable right now.</p>
            </div>
            <UiButton :disabled="!canPayWithPoints" @click="onCreateOrder">
              {{ state.busy ? 'Processing...' : 'Pay with points' }}
            </UiButton>
          </template>

          <template v-else>
            <div class="summary__label">External payment</div>
            <p class="externalText">This option opens an external payment site.</p>
            <UiButton :disabled="state.busy" @click="onExternalPayment">
              Continue with {{ state.selectedPaymentMethod === 'NAVER' ? 'Naver Pay' : state.selectedPaymentMethod === 'KAKAO' ? 'Kakao Pay' : 'Card' }}
            </UiButton>
          </template>
        </div>
      </div>
    </UiCard>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import { createOrder, deleteCartItem, getCartItems, getMyPointBalance, updateCartItem, type CartItem } from '@/api/commerce'
import { toApiClientError } from '@/api/http'
import CartItemRow from '@/components/shop/CartItemRow.vue'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import { useToastStore } from '@/stores/toasts'

type PaymentMethodUi = 'NAVER' | 'KAKAO' | 'CARD' | 'POINT'

const router = useRouter()
const toasts = useToastStore()

const state = reactive<{
  loading: boolean
  busy: boolean
  items: CartItem[]
  pointLoading: boolean
  pointBalance: number | null
  pointError: boolean
  pointToPayInput: string
  selectedPaymentMethod: PaymentMethodUi
}>({
  loading: true,
  busy: false,
  items: [],
  pointLoading: true,
  pointBalance: null,
  pointError: false,
  pointToPayInput: '0',
  selectedPaymentMethod: 'POINT',
})

const formatter = new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW', maximumFractionDigits: 0 })

const totalPrice = computed(() => state.items.reduce((acc, item) => acc + item.subtotalPrice, 0))
const remainingBalance = computed(() => (state.pointBalance ?? 0) - totalPrice.value)
const pointToPay = computed(() => {
  const parsed = Number(state.pointToPayInput)
  if (!Number.isFinite(parsed)) {
    return 0
  }
  return Math.max(0, Math.floor(parsed))
})
const isPointAmountExact = computed(() => pointToPay.value === totalPrice.value)
const canPayWithPoints = computed(
  () =>
    state.items.length > 0 &&
    state.selectedPaymentMethod === 'POINT' &&
    !state.busy &&
    !state.pointLoading &&
    !state.pointError &&
    remainingBalance.value >= 0 &&
    isPointAmountExact.value,
)
const pointBalanceText = computed(() => (state.pointBalance === null ? '-' : formatPrice(state.pointBalance)))

function formatPrice(value: number): string {
  return formatter.format(value)
}

async function refresh() {
  state.loading = true
  state.pointLoading = true
  state.pointError = false
  try {
    const [items, point] = await Promise.all([getCartItems(), getMyPointBalance()])
    state.items = items
    state.pointBalance = point.balance
    state.pointToPayInput = String(items.reduce((acc, item) => acc + item.subtotalPrice, 0))
  } catch (error) {
    const err = toApiClientError(error)
    if (err.code === 'COMMON_AUTH_REQUIRED') {
      toasts.push({ tone: 'error', title: err.code, message: err.message })
      state.items = []
      state.pointBalance = null
      state.pointError = true
      state.pointToPayInput = '0'
    } else {
      try {
        state.items = await getCartItems()
      } catch {
        state.items = []
      }
      state.pointBalance = null
      state.pointError = true
      state.pointToPayInput = '0'
      toasts.push({ tone: 'error', title: err.code, message: err.message })
    }
  } finally {
    state.loading = false
    state.pointLoading = false
  }
}

async function onUpdate(cartItemId: number, quantity: number) {
  if (!Number.isFinite(quantity) || quantity < 1 || quantity > 99) {
    toasts.push({ tone: 'error', title: 'COMMON_VALIDATION_FAILED', message: 'Quantity must be between 1 and 99.' })
    return
  }

  state.busy = true
  try {
    await updateCartItem(cartItemId, { quantity })
    await refresh()
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    state.busy = false
  }
}

async function onRemove(cartItemId: number) {
  state.busy = true
  try {
    await deleteCartItem(cartItemId)
    await refresh()
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    state.busy = false
  }
}

async function onCreateOrder() {
  if (!canPayWithPoints.value) {
    return
  }

  state.busy = true
  try {
    const order = await createOrder({ paymentMethod: 'POINT' })
    toasts.push({ tone: 'success', title: 'Order created', message: `Order ${order.id} created.` })
    await router.push({ name: 'shop-order-detail', params: { orderId: order.id } })
  } catch (error) {
    const err = toApiClientError(error)
    if (err.code === 'ORDER_CREATE_POINT_INSUFFICIENT') {
      toasts.push({ tone: 'error', title: err.code, message: 'Not enough points to complete payment.' })
      await refresh()
    } else {
      toasts.push({ tone: 'error', title: err.code, message: err.message })
    }
  } finally {
    state.busy = false
  }
}

function openExternal(url: string, label: string) {
  window.open(url, '_blank', 'noopener,noreferrer')
  toasts.push({ tone: 'info', title: label, message: 'Opened external payment site.' })
}

function onExternalPayment() {
  if (state.selectedPaymentMethod === 'NAVER') {
    openExternal('https://order.pay.naver.com', 'Naver Pay')
    return
  }
  if (state.selectedPaymentMethod === 'KAKAO') {
    openExternal('https://pay.kakaopay.com', 'Kakao Pay')
    return
  }
  openExternal('https://www.card-gorilla.com', 'Card payment')
}

void refresh()
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: var(--s-3);
  flex-wrap: wrap;
}

.title {
  margin: 0;
  font-family: var(--font-display);
  font-size: 34px;
  letter-spacing: -0.03em;
}

.sub {
  margin: 8px 0 0;
  color: var(--c-ink-2);
}

.head__actions {
  display: flex;
  gap: var(--s-2);
}

.loading,
.empty {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.list {
  margin-top: var(--s-6);
  display: grid;
  gap: var(--s-3);
}

.summary {
  margin-top: var(--s-6);
  padding: var(--s-4);
}

.paymentSection {
  display: grid;
  gap: var(--s-4);
}

@media (min-width: 860px) {
  .paymentSection {
    grid-template-columns: 260px 1fr;
    align-items: start;
  }
}

.paymentSection__methods {
  display: grid;
  gap: var(--s-2);
}

.methodButton {
  text-align: left;
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.16);
  background: rgba(255, 255, 255, 0.72);
  color: var(--c-ink-2);
  font-weight: 600;
  padding: 10px 12px;
  cursor: pointer;
}

.methodButton--active {
  border-color: rgba(88, 169, 224, 0.4);
  background: rgba(88, 169, 224, 0.16);
  color: var(--c-ink);
}

.paymentPanel {
  border: 1px solid rgba(27, 31, 35, 0.12);
  border-radius: var(--r-md);
  background: rgba(255, 255, 255, 0.74);
  padding: var(--s-4);
}

.summary__grid {
  display: grid;
  gap: var(--s-2);
}

.summary__label {
  color: var(--c-ink-2);
  font-size: 13px;
}

.summary__value {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 800;
}

.summary__value--error {
  color: #9b2d2d;
}

.summary__warn {
  margin: 2px 0 0;
  color: #9b2d2d;
  font-size: 13px;
}

.externalText {
  margin: 10px 0 var(--s-3);
  color: var(--c-ink-2);
}

.pointInput {
  margin-top: 6px;
  height: 40px;
  width: 180px;
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.16);
  padding: 0 12px;
}
</style>
