<template>
  <section class="hero">
    <div class="hero__copy">
      <h1 class="hero__title">
        Make meetups feel
        <span class="hero__titleAccent">safe</span>
        and
        <span class="hero__titleAccent2">easy</span>
      </h1>
      <p class="hero__sub">
        A gentle, structured board for dog-walk meetups. No chaos, just clear steps.
      </p>

      <div class="hero__cta">
        <UiButton @click="goMeetups">Explore meetups</UiButton>
        <UiButton variant="soft" @click="scrollToAuth">Login / Sign up</UiButton>
      </div>
    </div>

    <div class="hero__art" aria-hidden="true">
      <div class="hero__cloud hero__cloud--a" />
      <div class="hero__cloud hero__cloud--b" />
      <div class="hero__frame">
        <img class="hero__puppy" :src="puppyUrl" alt="" />
      </div>
      <div class="hero__sticker hero__sticker--sun">Friendly meetups</div>
      <div class="hero__sticker hero__sticker--ribbon">Clear steps</div>
    </div>
  </section>

  <section ref="authSection" class="auth">
    <UiCard class="auth__card">
      <div class="auth__head">
        <h2 class="auth__title">Welcome back</h2>
        <p class="auth__note">Quick entry for now. You can change this anytime.</p>
      </div>

      <div class="auth__form">
        <UiInput v-model="email" label="Email" type="email" placeholder="you@domain.com" />
        <div class="auth__actions">
          <UiButton :disabled="email.trim().length === 0" @click="onLogin">Continue</UiButton>
          <UiButton variant="ghost" :disabled="!auth.isLoggedIn" @click="auth.logout">Logout</UiButton>
        </div>
      </div>
    </UiCard>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import puppyUrl from '@/assets/puppy.png'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import UiInput from '@/components/ui/UiInput.vue'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toasts'

const router = useRouter()
const auth = useAuthStore()
const toasts = useToastStore()

const email = ref(auth.email)
const authSection = ref<HTMLElement | null>(null)

function scrollToAuth() {
  authSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function goMeetups() {
  router.push('/meetups')
}

function onLogin() {
  auth.login(email.value)
  toasts.push({ tone: 'success', title: 'Logged in', message: 'Welcome.' })
}
</script>

<style scoped>
.hero {
  display: grid;
  gap: var(--s-8);
  align-items: center;
}

@media (min-width: 880px) {
  .hero {
    grid-template-columns: 1.15fr 0.85fr;
    gap: var(--s-12);
  }
}

.hero__title {
  margin: 0;
  font-family: var(--font-display);
  font-size: clamp(42px, 5vw, 60px);
  line-height: 1.02;
  letter-spacing: -0.03em;
}

.hero__titleAccent {
  background: linear-gradient(180deg, rgba(88, 169, 224, 0.0), rgba(88, 169, 224, 0.26));
  border-radius: 12px;
  padding: 0 8px;
}

.hero__titleAccent2 {
  background: linear-gradient(180deg, rgba(246, 195, 56, 0.0), rgba(246, 195, 56, 0.28));
  border-radius: 12px;
  padding: 0 8px;
}

.hero__sub {
  margin: var(--s-3) 0 0;
  max-width: 56ch;
  color: var(--c-ink-2);
  font-size: 16px;
  line-height: 1.6;
}

.hero__cta {
  margin-top: var(--s-6);
  display: flex;
  flex-wrap: wrap;
  gap: var(--s-2);
}

.hero__art {
  position: relative;
  min-height: 420px;
}

.hero__frame {
  position: absolute;
  inset: 0;
  margin: auto;
  width: min(360px, 100%);
  aspect-ratio: 1 / 1.1;
  border-radius: 40px;
  background: rgba(255, 255, 255, 0.65);
  border: 1px solid rgba(27, 31, 35, 0.12);
  box-shadow: var(--sh-2);
  overflow: hidden;
}

.hero__puppy {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: translateY(-4%);
}

.hero__cloud {
  position: absolute;
  width: 260px;
  height: 130px;
  background: radial-gradient(circle at 30% 30%, rgba(234, 243, 251, 1), rgba(234, 243, 251, 0.35));
  border-radius: 999px;
  filter: blur(0.2px);
  opacity: 0.9;
}

.hero__cloud--a {
  left: -30px;
  top: 26px;
  transform: rotate(-6deg);
}

.hero__cloud--b {
  right: -40px;
  bottom: 20px;
  transform: rotate(8deg);
}

.hero__sticker {
  position: absolute;
  padding: 10px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.74);
  border: 1px solid rgba(27, 31, 35, 0.12);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.08), 0 14px 30px rgba(27, 31, 35, 0.12);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.hero__sticker--sun {
  left: 0;
  top: 70%;
}

.hero__sticker--ribbon {
  right: 0;
  top: 14%;
}

.auth {
  margin-top: var(--s-12);
}

.auth__card {
  padding: var(--s-6);
}

.auth__head {
  margin-bottom: var(--s-4);
}

.auth__title {
  margin: 0;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.auth__note {
  margin: 8px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
  line-height: 1.5;
}

.auth__form {
  display: grid;
  gap: var(--s-4);
}

.auth__actions {
  display: flex;
  gap: var(--s-2);
  flex-wrap: wrap;
}
</style>
