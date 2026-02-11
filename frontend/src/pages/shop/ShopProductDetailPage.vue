<template>
  <section class="head">
    <RouterLink class="back" to="/shop">Back to products</RouterLink>
    <div class="head__actions">
      <RouterLink to="/shop/cart"><UiButton size="sm" variant="soft">Cart</UiButton></RouterLink>
      <RouterLink to="/shop/orders"><UiButton size="sm" variant="ghost">Orders</UiButton></RouterLink>
    </div>
  </section>

  <section v-if="state.loading" class="loading">Loading product...</section>

  <UiCard v-else-if="state.product" class="card">
    <section class="heroGrid">
      <div class="infoCol">
        <p class="category">{{ state.product.category }} · {{ state.product.status }}</p>
        <h1 class="title">{{ state.product.name }}</h1>
        <p class="price">{{ formatPrice(state.product.price) }}</p>
        <p class="stock">Stock {{ state.product.stockQuantity }}</p>

        <form class="cart" @submit.prevent="onAddToCart">
          <label class="field">
            <span class="field__label">Quantity</span>
            <input v-model="quantity" class="field__control" type="number" min="1" max="99" />
          </label>
          <UiButton type="submit" :disabled="state.submitting || state.product.status !== 'ON_SALE' || state.product.stockQuantity < 1">
            {{ state.submitting ? 'Adding...' : 'Add to cart' }}
          </UiButton>
        </form>
      </div>

      <div class="imageCol">
        <img class="thumb" :src="imageSrc" :alt="state.product.name" @error="onImageError" />
      </div>
    </section>

    <section class="detailBlock">
      <h2 class="detailTitle">상품 설명</h2>
      <p class="desc">{{ longDescription }}</p>
      <ul class="detailList">
        <li>사용 대상: {{ targetText }}</li>
        <li>권장 사용: 매일 1~2회, 반려견 체형과 활동량에 맞춰 조절</li>
        <li>보관 방법: 직사광선을 피해 서늘한 곳에 보관</li>
      </ul>
    </section>
  </UiCard>

  <section v-else class="loading">Product not found.</section>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'

import { addCartItem, getProduct, type Product } from '@/api/commerce'
import puppyUrl from '@/assets/puppy.png'
import { toApiClientError } from '@/api/http'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import { useToastStore } from '@/stores/toasts'
import { getProductImageCandidates } from '@/utils/shop-product-image'

const route = useRoute()
const router = useRouter()
const toasts = useToastStore()

const state = reactive<{ loading: boolean; submitting: boolean; product: Product | null }>({
  loading: true,
  submitting: false,
  product: null,
})

const quantity = ref('1')
const imageCandidates = ref<string[]>([])
const imageIndex = ref(0)

const formatter = new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW', maximumFractionDigits: 0 })

function formatPrice(value: number): string {
  return formatter.format(value)
}

const imageSrc = computed(() => imageCandidates.value[imageIndex.value] ?? puppyUrl)

const longDescription = computed(() => {
  if (!state.product) {
    return ''
  }

  if (state.product.description && state.product.description.trim().length > 0) {
    return state.product.description
  }

  if (state.product.category === 'FEED') {
    return `${state.product.name}은(는) 균형 잡힌 영양 설계로 매일 급여하기 좋은 데일리 사료입니다. 식감과 기호성을 고려해 식사 만족도를 높이고, 규칙적인 급여 루틴을 만들기 쉽게 구성했습니다.`
  }
  if (state.product.category === 'SNACK') {
    return `${state.product.name}은(는) 훈련 보상이나 산책 후 간식으로 활용하기 좋은 스낵입니다. 과한 부담 없이 급여할 수 있도록 적절한 크기와 식감을 중심으로 구성했습니다.`
  }
  return `${state.product.name}은(는) 반려견의 놀이 집중도를 높이는 노즈워크/장난감 라인입니다. 혼자 노는 시간에도 흥미를 유지할 수 있도록 촉감과 사용성을 고려해 제작되었습니다.`
})

const targetText = computed(() => {
  if (!state.product) {
    return '-'
  }
  if (state.product.category === 'FEED') {
    return '전 연령 반려견 주식/보조식'
  }
  if (state.product.category === 'SNACK') {
    return '훈련 보상 또는 간식 섭취가 필요한 반려견'
  }
  return '활동량이 높거나 노즈워크 놀이를 좋아하는 반려견'
})

function onImageError() {
  if (imageIndex.value < imageCandidates.value.length) {
    imageIndex.value += 1
  }
}

async function refresh() {
  const id = Number(route.params.productId)
  if (!Number.isFinite(id)) {
    state.loading = false
    state.product = null
    return
  }

  state.loading = true
  try {
    state.product = await getProduct(id)
    imageCandidates.value = getProductImageCandidates(state.product.category)
    imageIndex.value = 0
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
    state.product = null
  } finally {
    state.loading = false
  }
}

watch(
  () => state.product?.id,
  () => {
    if (!state.product) {
      imageCandidates.value = []
      imageIndex.value = 0
    }
  },
)

async function onAddToCart() {
  if (!state.product) {
    return
  }

  const nextQuantity = Number(quantity.value)
  if (!Number.isFinite(nextQuantity) || nextQuantity < 1 || nextQuantity > 99) {
    toasts.push({ tone: 'error', title: 'COMMON_VALIDATION_FAILED', message: 'Quantity must be between 1 and 99.' })
    return
  }

  state.submitting = true
  try {
    await addCartItem({ productId: state.product.id, quantity: nextQuantity })
    toasts.push({ tone: 'success', title: 'Added', message: 'Item added to cart.' })
    await router.push('/shop/cart')
  } catch (error) {
    const err = toApiClientError(error)
    if (err.code === 'COMMON_AUTH_REQUIRED') {
      toasts.push({ tone: 'info', title: err.code, message: 'Please login first.' })
      await router.push('/')
      return
    }
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    state.submitting = false
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

.head__actions {
  display: flex;
  gap: var(--s-2);
}

.loading {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.card {
  margin-top: var(--s-6);
  padding: var(--s-6);
}

.heroGrid {
  display: grid;
  gap: var(--s-6);
}

@media (min-width: 920px) {
  .heroGrid {
    grid-template-columns: 1.05fr 0.95fr;
    align-items: start;
  }

  .imageCol {
    order: 2;
  }

  .infoCol {
    order: 1;
  }
}

.thumb {
  display: block;
  width: 100%;
  max-height: 420px;
  object-fit: cover;
  border-radius: 16px;
  border: 1px solid rgba(27, 31, 35, 0.12);
}

.category {
  margin: var(--s-4) 0 0;
  color: var(--c-ink-2);
  font-size: 12px;
}

.title {
  margin: var(--s-2) 0 0;
  font-size: clamp(30px, 4vw, 42px);
  font-family: var(--font-display);
}

.price {
  margin: var(--s-4) 0 0;
  font-size: 28px;
  font-weight: 800;
}

.stock {
  margin: var(--s-2) 0 0;
  color: var(--c-ink-2);
}

.cart {
  margin-top: var(--s-6);
  display: flex;
  gap: var(--s-3);
  align-items: end;
  flex-wrap: wrap;
}

.field {
  display: grid;
  gap: 8px;
}

.field__label {
  font-size: 13px;
  color: var(--c-ink-2);
}

.field__control {
  height: 42px;
  width: 120px;
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.14);
  padding: 0 12px;
}

.detailBlock {
  margin-top: var(--s-8);
  border-top: 1px solid rgba(27, 31, 35, 0.12);
  padding-top: var(--s-6);
}

.detailTitle {
  margin: 0;
  font-size: 22px;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.desc {
  margin: var(--s-3) 0 0;
  line-height: 1.7;
  color: var(--c-ink);
}

.detailList {
  margin: var(--s-3) 0 0;
  padding-left: 20px;
  color: var(--c-ink-2);
  display: grid;
  gap: 8px;
}
</style>
