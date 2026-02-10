<template>
  <div class="shell">
    <header class="top">
      <div class="top__inner">
        <RouterLink class="brand" to="/">
          <span class="brand__dot" aria-hidden="true" />
          <span class="brand__name">Ato Walk</span>
        </RouterLink>

        <nav class="nav">
          <RouterLink class="nav__link" to="/meetups">Meetups</RouterLink>
        </nav>

        <div class="auth">
          <template v-if="auth.isLoggedIn">
            <span class="auth__email">{{ auth.email }}</span>
            <UiButton size="sm" variant="ghost" @click="auth.logout">Logout</UiButton>
          </template>
          <template v-else>
            <RouterLink class="nav__link" to="/">Login</RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main class="main">
      <div class="main__inner">
        <slot />
      </div>
    </main>

    <UiToastHost />
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'

import UiButton from '@/components/ui/UiButton.vue'
import UiToastHost from '@/components/ui/UiToastHost.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<style scoped>
.shell {
  min-height: 100vh;
}

.top {
  position: sticky;
  top: 0;
  z-index: 30;
  background: rgba(255, 255, 255, 0.62);
  border-bottom: 1px solid var(--c-line);
  backdrop-filter: blur(10px);
}

.top__inner {
  height: 64px;
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 var(--s-4);
  display: flex;
  align-items: center;
  gap: var(--s-4);
}

.brand {
  display: inline-flex;
  gap: 10px;
  align-items: center;
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: -0.02em;
}

.brand__dot {
  width: 12px;
  height: 12px;
  border-radius: 999px;
  background: radial-gradient(circle at 35% 35%, var(--c-ribbon), #ff8fb9);
  box-shadow: 0 10px 22px rgba(255, 143, 185, 0.28);
}

.nav {
  display: flex;
  gap: var(--s-2);
  margin-right: auto;
}

.nav__link {
  padding: 10px 12px;
  border-radius: 999px;
  color: var(--c-ink-2);
}

.nav__link.router-link-active {
  color: var(--c-ink);
  background: rgba(255, 255, 255, 0.74);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.08);
}

.auth {
  display: flex;
  gap: var(--s-2);
  align-items: center;
}

.auth__email {
  display: none;
  font-size: 13px;
  color: var(--c-ink-2);
  max-width: 260px;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

@media (min-width: 720px) {
  .auth__email {
    display: inline;
  }
}

.main {
  padding: var(--s-8) 0;
}

.main__inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 var(--s-4);
}
</style>
