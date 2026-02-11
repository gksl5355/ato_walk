<template>
  <section class="head">
    <div>
      <h1 class="title">Shopping</h1>
      <p class="sub">Browse products and manage cart/orders.</p>
    </div>
    <div class="head__actions">
      <RouterLink to="/shop/cart"><UiButton size="sm" variant="soft">Cart</UiButton></RouterLink>
      <RouterLink to="/shop/orders"><UiButton size="sm" variant="ghost">Orders</UiButton></RouterLink>
    </div>
  </section>

  <UiCard class="filters">
    <div class="filters__grid">
      <label class="field">
        <span class="field__label">Category</span>
        <select v-model="filters.category" class="field__control">
          <option value="">All</option>
          <option value="FEED">FEED</option>
          <option value="SNACK">SNACK</option>
          <option value="TOY">TOY</option>
        </select>
      </label>

      <label class="field">
        <span class="field__label">Status</span>
        <select v-model="filters.status" class="field__control">
          <option value="">All</option>
          <option value="ON_SALE">ON_SALE</option>
          <option value="SOLD_OUT">SOLD_OUT</option>
          <option value="HIDDEN">HIDDEN</option>
        </select>
      </label>

      <div class="filters__actions">
        <UiButton size="sm" variant="soft" @click="applyFilters">Apply</UiButton>
        <UiButton size="sm" variant="ghost" @click="resetFilters">Reset</UiButton>
      </div>
    </div>
  </UiCard>

  <section v-if="state.loading" class="loading">Loading products...</section>
  <section v-else>
    <div v-if="state.page.content.length === 0" class="empty">No products found.</div>

    <div v-else class="grid">
      <RouterLink
        v-for="item in state.page.content"
        :key="item.id"
        class="cardLink"
        :to="{ name: 'shop-product-detail', params: { productId: item.id } }"
      >
        <ProductCard :product="item" />
      </RouterLink>
    </div>

    <div class="pager">
      <UiButton size="sm" variant="ghost" :disabled="state.page.page.page <= 0" @click="goPage(state.page.page.page - 1)">
        Prev
      </UiButton>
      <span class="pager__mid">Page {{ state.page.page.page + 1 }} / {{ Math.max(1, state.page.page.totalPages) }}</span>
      <UiButton
        size="sm"
        variant="ghost"
        :disabled="state.page.page.page + 1 >= state.page.page.totalPages"
        @click="goPage(state.page.page.page + 1)"
      >
        Next
      </UiButton>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { RouterLink } from 'vue-router'

import { listProducts, type ProductPageData, type ProductCategory, type ProductStatus } from '@/api/commerce'
import { toApiClientError } from '@/api/http'
import ProductCard from '@/components/shop/ProductCard.vue'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import { useToastStore } from '@/stores/toasts'

type ProductCategoryFilter = '' | ProductCategory
type ProductStatusFilter = '' | ProductStatus

const toasts = useToastStore()

const state = reactive<{ loading: boolean; page: ProductPageData; pageNum: number }>({
  loading: true,
  page: {
    content: [],
    page: {
      page: 0,
      size: 12,
      totalElements: 0,
      totalPages: 0,
    },
  },
  pageNum: 0,
})

const filters = reactive<{ category: ProductCategoryFilter; status: ProductStatusFilter }>({
  category: '',
  status: '',
})

async function refresh() {
  state.loading = true
  try {
    state.page = await listProducts({
      page: state.pageNum,
      size: 12,
      category: filters.category === '' ? undefined : filters.category,
      status: filters.status === '' ? undefined : filters.status,
    })
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
    state.page = {
      content: [],
      page: {
        page: 0,
        size: 12,
        totalElements: 0,
        totalPages: 0,
      },
    }
  } finally {
    state.loading = false
  }
}

function applyFilters() {
  state.pageNum = 0
  void refresh()
}

function resetFilters() {
  filters.category = ''
  filters.status = ''
  state.pageNum = 0
  void refresh()
}

function goPage(nextPage: number) {
  state.pageNum = Math.max(0, nextPage)
  void refresh()
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

.filters {
  margin-top: var(--s-6);
  padding: var(--s-4);
}

.filters__grid {
  display: grid;
  gap: var(--s-3);
}

@media (min-width: 720px) {
  .filters__grid {
    grid-template-columns: 1fr 1fr auto;
    align-items: end;
  }
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
  border-radius: 12px;
  border: 1px solid rgba(27, 31, 35, 0.16);
  padding: 0 12px;
  background: rgba(255, 255, 255, 0.8);
}

.filters__actions {
  display: flex;
  gap: var(--s-2);
}

.loading {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.empty {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.grid {
  margin-top: var(--s-6);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  gap: 14px;
}

.cardLink {
  display: block;
}

.pager {
  margin-top: var(--s-6);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--s-2);
}

.pager__mid {
  font-size: 13px;
  color: var(--c-ink-2);
}
</style>
