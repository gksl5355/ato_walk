<template>
  <article class="card">
    <img class="thumb" :src="imageSrc" :alt="product.name" @error="onImageError" />
    <div class="card__top">
      <span :class="['status', `status--${statusTone}`]">{{ product.status }}</span>
      <span class="category">{{ product.category }}</span>
    </div>
    <h3 class="title">{{ product.name }}</h3>
    <p class="meta">Stock {{ product.stockQuantity }}</p>
    <p class="price">{{ formatPrice(product.price) }}</p>
  </article>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import type { ProductSummary } from '@/api/commerce'
import puppyUrl from '@/assets/puppy.png'
import { getProductImageCandidates } from '@/utils/shop-product-image'

const props = defineProps<{ product: ProductSummary }>()

const formatter = new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW', maximumFractionDigits: 0 })

const statusTone = computed(() => {
  if (props.product.status === 'ON_SALE') {
    return 'sale'
  }
  if (props.product.status === 'SOLD_OUT') {
    return 'soldOut'
  }
  return 'hidden'
})

const imageCandidates = getProductImageCandidates(props.product.category)
const imageIndex = ref(0)

const imageSrc = computed(() => imageCandidates[imageIndex.value] ?? puppyUrl)

function onImageError() {
  if (imageIndex.value < imageCandidates.length) {
    imageIndex.value += 1
  }
}

function formatPrice(value: number): string {
  return formatter.format(value)
}
</script>

<style scoped>
.card {
  height: 100%;
  border-radius: var(--r-lg);
  border: 1px solid rgba(27, 31, 35, 0.14);
  background: rgba(255, 255, 255, 0.78);
  padding: var(--s-4);
  box-shadow: var(--sh-1);
}

.thumb {
  display: block;
  width: 100%;
  aspect-ratio: 1.25 / 1;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.1);
}

.card__top {
  display: flex;
  justify-content: space-between;
  gap: var(--s-2);
  align-items: center;
}

.status {
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border: 1px solid transparent;
}

.status--sale {
  color: #1a4322;
  background: rgba(128, 203, 148, 0.28);
  border-color: rgba(38, 133, 63, 0.3);
}

.status--soldOut {
  color: #52311c;
  background: rgba(244, 198, 123, 0.3);
  border-color: rgba(196, 132, 37, 0.3);
}

.status--hidden {
  color: #404651;
  background: rgba(194, 200, 210, 0.36);
  border-color: rgba(130, 137, 151, 0.3);
}

.category {
  color: var(--c-ink-2);
  font-size: 12px;
}

.title {
  margin: var(--s-3) 0 0;
  font-size: 18px;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.meta {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--c-ink-2);
}

.price {
  margin: var(--s-3) 0 0;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.02em;
}
</style>
