<template>
  <section class="head">
    <RouterLink class="back" to="/shop/orders">Back to orders</RouterLink>
    <RouterLink to="/shop"><UiButton size="sm" variant="soft">Products</UiButton></RouterLink>
  </section>

  <section v-if="state.loading" class="loading">Loading order...</section>
  <section v-else-if="state.order">
    <UiCard class="summary">
      <div class="summary__top">
        <h1 class="title">Order details</h1>
        <span :class="['status', `status--${state.order.status === 'CREATED' ? 'created' : 'canceled'}`]">
          {{ state.order.status }}
        </span>
      </div>
      <p class="meta meta--id">No. {{ state.order.id }}</p>
      <p class="meta">{{ formatWhen(state.order.createdAt) }}</p>
      <p class="total">{{ formatPrice(state.order.totalPrice) }}</p>

      <UiButton v-if="state.order.status === 'CREATED'" :disabled="state.busy" @click="onCancelOrder">
        {{ state.busy ? 'Canceling...' : 'Cancel order' }}
      </UiButton>
    </UiCard>

    <section class="items">
      <h2 class="items__title">Items</h2>
      <article v-for="item in state.order.items" :key="item.id" class="itemRow">
        <div>
          <p class="itemRow__name">Product #{{ item.productId }}</p>
          <p class="itemRow__meta">Qty {{ item.quantity }} · Unit {{ formatPrice(item.unitPrice) }}</p>
        </div>
        <p class="itemRow__price">{{ formatPrice(item.subtotalPrice) }}</p>
      </article>
    </section>
  </section>
  <section v-else class="loading">Order not found.</section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { RouterLink, useRoute } from 'vue-router'

import { cancelOrder, getOrder, type Order } from '@/api/commerce'
import { toApiClientError } from '@/api/http'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import { useToastStore } from '@/stores/toasts'
import { formatWhen } from '@/utils/datetime'

const route = useRoute()
const toasts = useToastStore()

const state = reactive<{ loading: boolean; busy: boolean; order: Order | null }>({
  loading: true,
  busy: false,
  order: null,
})

const formatter = new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW', maximumFractionDigits: 0 })

function formatPrice(value: number): string {
  return formatter.format(value)
}

async function refresh() {
  const id = Number(route.params.orderId)
  if (!Number.isFinite(id)) {
    state.loading = false
    state.order = null
    return
  }

  state.loading = true
  try {
    state.order = await getOrder(id)
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
    state.order = null
  } finally {
    state.loading = false
  }
}

async function onCancelOrder() {
  if (!state.order || state.order.status !== 'CREATED') {
    return
  }

  state.busy = true
  try {
    state.order = await cancelOrder(state.order.id)
    toasts.push({ tone: 'success', title: 'Canceled', message: 'Order canceled.' })
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    state.busy = false
  }
}

void refresh()
</script>

<style scoped>
.head {
  display: flex;
  justify-content: space-between;
  gap: var(--s-3);
  flex-wrap: wrap;
  align-items: center;
}

.back {
  color: var(--c-ink-2);
}

.loading {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.summary {
  margin-top: var(--s-6);
  padding: var(--s-5);
}

.summary__top {
  display: flex;
  justify-content: space-between;
  gap: var(--s-3);
  align-items: center;
}

.title {
  margin: 0;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.status {
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 10px;
  border: 1px solid transparent;
}

.status--created {
  color: #1a4322;
  border-color: rgba(38, 133, 63, 0.3);
  background: rgba(128, 203, 148, 0.28);
}

.status--canceled {
  color: #5b3f3f;
  border-color: rgba(182, 90, 90, 0.3);
  background: rgba(233, 151, 151, 0.26);
}

.meta {
  margin: var(--s-2) 0 0;
  color: var(--c-ink-2);
}

.meta--id {
  margin-top: var(--s-3);
}

.total {
  margin: var(--s-3) 0 var(--s-4);
  font-size: 28px;
  font-weight: 800;
}

.items {
  margin-top: var(--s-6);
}

.items__title {
  margin: 0;
  font-family: var(--font-display);
  font-size: 24px;
}

.itemRow {
  margin-top: var(--s-3);
  border: 1px solid rgba(27, 31, 35, 0.12);
  border-radius: var(--r-md);
  background: rgba(255, 255, 255, 0.75);
  padding: var(--s-4);
  display: flex;
  justify-content: space-between;
  gap: var(--s-3);
  align-items: center;
}

.itemRow__name {
  margin: 0;
  font-weight: 700;
}

.itemRow__meta {
  margin: 6px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
}

.itemRow__price {
  margin: 0;
  font-weight: 700;
}
</style>
