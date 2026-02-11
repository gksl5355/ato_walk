<template>
  <article class="row">
    <div>
      <h3 class="name">{{ item.productName }}</h3>
      <p class="meta">Unit {{ formatPrice(item.unitPrice) }}</p>
      <p class="meta">Subtotal {{ formatPrice(item.subtotalPrice) }}</p>
    </div>

    <div class="actions">
      <input
        class="quantity"
        type="number"
        min="1"
        max="99"
        :value="quantity"
        :disabled="busy"
        @input="onQuantityInput"
      />
      <div class="buttons">
        <UiButton size="sm" :disabled="busy" @click="$emit('update', item.id, quantity)">Update</UiButton>
        <UiButton size="sm" variant="ghost" :disabled="busy" @click="$emit('remove', item.id)">Remove</UiButton>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

import type { CartItem } from '@/api/commerce'
import UiButton from '@/components/ui/UiButton.vue'

const props = defineProps<{ item: CartItem; busy?: boolean }>()

defineEmits<{
  (e: 'update', cartItemId: number, quantity: number): void
  (e: 'remove', cartItemId: number): void
}>()

const quantity = ref(props.item.quantity)

const formatter = new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW', maximumFractionDigits: 0 })

watch(
  () => props.item.quantity,
  (next) => {
    quantity.value = next
  },
)

function onQuantityInput(event: Event) {
  const next = Number((event.target as HTMLInputElement).value)
  if (Number.isFinite(next)) {
    quantity.value = next
  }
}

function formatPrice(value: number): string {
  return formatter.format(value)
}
</script>

<style scoped>
.row {
  display: flex;
  justify-content: space-between;
  gap: var(--s-4);
  align-items: center;
  border: 1px solid rgba(27, 31, 35, 0.12);
  border-radius: var(--r-md);
  background: rgba(255, 255, 255, 0.74);
  padding: var(--s-4);
  flex-wrap: wrap;
}

.name {
  margin: 0;
  font-size: 17px;
}

.meta {
  margin: 6px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
}

.actions {
  display: flex;
  align-items: center;
  gap: var(--s-2);
  flex-wrap: wrap;
}

.quantity {
  height: 34px;
  width: 88px;
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.16);
  padding: 0 10px;
}

.buttons {
  display: flex;
  gap: var(--s-2);
}
</style>
