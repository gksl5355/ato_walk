<template>
  <article class="card">
    <div class="top">
      <h3 class="title">Order</h3>
      <span :class="['status', `status--${tone}`]">{{ order.status }}</span>
    </div>
    <p class="orderNo">No. {{ order.id }}</p>
    <p class="meta">{{ formatWhen(order.createdAt) }}</p>
    <p class="price">{{ formatPrice(order.totalPrice) }}</p>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { OrderSummary } from '@/api/commerce'
import { formatWhen } from '@/utils/datetime'

const props = defineProps<{ order: OrderSummary }>()

const formatter = new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW', maximumFractionDigits: 0 })

const tone = computed(() => (props.order.status === 'CREATED' ? 'created' : 'canceled'))

function formatPrice(value: number): string {
  return formatter.format(value)
}
</script>

<style scoped>
.card {
  border: 1px solid rgba(27, 31, 35, 0.14);
  border-radius: var(--r-md);
  padding: var(--s-4);
  background: rgba(255, 255, 255, 0.78);
}

.top {
  display: flex;
  justify-content: space-between;
  gap: var(--s-3);
  align-items: center;
}

.title {
  margin: 0;
  font-size: 18px;
}

.status {
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 700;
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
  margin: 6px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
}

.orderNo {
  margin: 8px 0 0;
  font-size: 12px;
  color: var(--c-ink-2);
}

.price {
  margin: var(--s-2) 0 0;
  font-weight: 800;
  letter-spacing: -0.01em;
}
</style>
