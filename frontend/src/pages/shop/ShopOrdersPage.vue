<template>
  <section class="head">
    <div>
      <h1 class="title">Orders</h1>
      <p class="sub">Your order history.</p>
    </div>
    <RouterLink to="/shop"><UiButton size="sm" variant="soft">Products</UiButton></RouterLink>
  </section>

  <section v-if="state.loading" class="loading">Loading orders...</section>
  <section v-else>
    <div v-if="state.page.content.length === 0" class="empty">No orders yet.</div>
    <div v-else class="list">
      <RouterLink
        v-for="order in state.page.content"
        :key="order.id"
        class="cardLink"
        :to="{ name: 'shop-order-detail', params: { orderId: order.id } }"
      >
        <OrderSummaryCard :order="order" />
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

import { listOrders, type OrderSummaryPageData } from '@/api/commerce'
import { toApiClientError } from '@/api/http'
import OrderSummaryCard from '@/components/shop/OrderSummaryCard.vue'
import UiButton from '@/components/ui/UiButton.vue'
import { useToastStore } from '@/stores/toasts'

const toasts = useToastStore()

const state = reactive<{ loading: boolean; page: OrderSummaryPageData; pageNum: number }>({
  loading: true,
  page: {
    content: [],
    page: {
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
    },
  },
  pageNum: 0,
})

async function refresh() {
  state.loading = true
  try {
    state.page = await listOrders({ page: state.pageNum, size: 10 })
  } catch (error) {
    const err = toApiClientError(error)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
    state.page = {
      content: [],
      page: {
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
      },
    }
  } finally {
    state.loading = false
  }
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
  gap: var(--s-3);
  align-items: flex-end;
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

.cardLink {
  display: block;
}

.pager {
  margin-top: var(--s-6);
  display: flex;
  justify-content: center;
  gap: var(--s-2);
}

.pager__mid {
  font-size: 13px;
  color: var(--c-ink-2);
}
</style>
