<template>
  <section class="pageHead">
    <div>
      <h1 class="title">Meetups</h1>
      <p class="sub">A tidy board with clear steps.</p>
    </div>
    <div class="pageHead__actions">
      <UiButton @click="openCreate">Create meetup</UiButton>
    </div>
  </section>

  <UiCard class="filters">
    <div class="filters__grid">
      <UiSelect v-model="filters.dogSize" label="Dog size" placeholder="Any">
        <option value="SMALL">SMALL</option>
        <option value="MEDIUM">MEDIUM</option>
        <option value="LARGE">LARGE</option>
      </UiSelect>
      <UiSelect v-model="filters.sociabilityLevel" label="Sociability" placeholder="Any">
        <option value="LOW">LOW</option>
        <option value="MEDIUM">MEDIUM</option>
        <option value="HIGH">HIGH</option>
      </UiSelect>
      <UiSelect v-model="filters.reactivityLevel" label="Reactivity" placeholder="Any">
        <option value="LOW">LOW</option>
        <option value="MEDIUM">MEDIUM</option>
        <option value="HIGH">HIGH</option>
      </UiSelect>

      <div class="filters__actions">
        <UiButton variant="soft" @click="refresh">Apply</UiButton>
        <UiButton variant="ghost" @click="resetFilters">Reset</UiButton>
      </div>
    </div>
  </UiCard>

  <section v-if="state.loading" class="loading">Loading meetups...</section>

  <section v-else>
    <div v-if="state.page && state.page.content.length === 0" class="empty">
      <img class="empty__img" :src="puppyUrl" alt="" />
      <div class="empty__title">No meetups yet</div>
      <div class="empty__sub">Create the first one and keep it friendly.</div>
      <UiButton @click="openCreate">Create meetup</UiButton>
    </div>

    <div v-else class="cards">
      <button
        v-for="m in state.page?.content ?? []"
        :key="m.id"
        class="meetupCard"
        type="button"
        @click="openDetails(m)"
      >
        <div class="meetupCard__top">
          <UiBadge :tone="badgeTone(m.status)">{{ m.status }}</UiBadge>
          <div class="meetupCard__when">{{ formatWhen(m.scheduledAt) }}</div>
        </div>
        <div class="meetupCard__title">{{ m.title }}</div>
        <div class="meetupCard__meta">{{ m.location }} · max {{ m.maxParticipants }}</div>
      </button>
    </div>

    <div v-if="state.page" class="pager">
      <UiButton variant="ghost" size="sm" :disabled="state.page.first" @click="goPage(state.page.number - 1)">
        Prev
      </UiButton>
      <div class="pager__mid">Page {{ state.page.number + 1 }} / {{ state.page.totalPages }}</div>
      <UiButton variant="ghost" size="sm" :disabled="state.page.last" @click="goPage(state.page.number + 1)">
        Next
      </UiButton>
    </div>
  </section>

  <UiModal :open="modal.open" :title="modal.mode === 'create' ? 'Create meetup' : 'Edit meetup'" @close="closeModal">
    <form class="form" @submit.prevent="submitModal">
      <UiInput v-model="form.title" label="Title" placeholder="Morning walk at the park" />
      <UiTextarea v-model="form.description" label="Description (optional)" placeholder="Any notes for participants" />
      <UiInput v-model="form.location" label="Location" placeholder="Gangnam, Seoul" />
      <UiInput v-model="form.maxParticipants" label="Max participants" type="number" min="1" />
      <UiInput v-model="form.scheduledAt" label="Scheduled at" type="datetime-local" />

      <div class="form__actions">
        <UiButton type="submit" :disabled="modal.submitting">
          {{ modal.submitting ? 'Saving...' : 'Save' }}
        </UiButton>
        <UiButton variant="ghost" :disabled="modal.submitting" @click="closeModal">Cancel</UiButton>
      </div>
    </form>
  </UiModal>

  <UiDrawer :open="drawer.open" :title="drawer.meetup?.title ?? ''" @close="closeDrawer">
    <template v-if="drawer.meetup">
      <section class="drawerBlock">
        <div class="drawerBlock__grid">
          <div>
            <div class="drawerLabel">When</div>
            <div class="drawerValue">{{ formatWhen(drawer.meetup.scheduledAt) }}</div>
          </div>
          <div>
            <div class="drawerLabel">Where</div>
            <div class="drawerValue">{{ drawer.meetup.location }}</div>
          </div>
          <div>
            <div class="drawerLabel">Status</div>
            <div class="drawerValue">
              <UiBadge :tone="badgeTone(drawer.meetup.status)">{{ drawer.meetup.status }}</UiBadge>
            </div>
          </div>
        </div>

        <p v-if="drawer.meetup.description" class="drawerDesc">{{ drawer.meetup.description }}</p>

        <div class="drawerActions">
          <UiButton variant="soft" @click="openEdit(drawer.meetup)">Edit</UiButton>
          <UiButton variant="ghost" :disabled="drawer.busy" @click="onCancel(drawer.meetup.id)">Cancel</UiButton>
          <UiButton variant="ghost" :disabled="drawer.busy" @click="onEnd(drawer.meetup.id)">End</UiButton>
        </div>
      </section>

      <section class="drawerBlock">
        <h3 class="drawerH">Participation</h3>
        <p class="drawerMuted">UI is ready; wiring will happen once participation endpoints land.</p>
        <div class="drawerActions">
          <UiButton :disabled="true">Request to join (준비중)</UiButton>
          <UiButton variant="ghost" :disabled="true">Approve / Reject (준비중)</UiButton>
        </div>
      </section>

      <section class="drawerBlock">
        <h3 class="drawerH">Announcements</h3>
        <div class="comms">
          <div v-if="comms.loading" class="drawerMuted">Loading...</div>
          <div v-else-if="comms.page && comms.page.content.length === 0" class="drawerMuted">
            No announcements yet.
          </div>
          <div v-else class="comms__list">
            <div v-for="c in comms.page?.content ?? []" :key="c.id" class="comms__item">
              <div class="comms__content">{{ c.content }}</div>
              <div class="comms__meta">{{ formatWhen(c.createdAt) }}</div>
            </div>
          </div>

          <form class="comms__form" @submit.prevent="submitComm">
            <UiInput v-model="comms.draft" label="New message" placeholder="Short and clear" />
            <UiButton type="submit" :disabled="comms.sending || comms.draft.trim().length === 0">
              {{ comms.sending ? 'Sending...' : 'Send' }}
            </UiButton>
          </form>
        </div>
      </section>
    </template>
  </UiDrawer>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

import puppyUrl from '@/assets/puppy.png'
import { toApiClientError } from '@/api/http'
import { cancelMeetup, createMeetup, endMeetup, getMeetup, listMeetups, updateMeetup, type Meetup } from '@/api/meetups'
import { createCommunication, listCommunications, type Communication } from '@/api/communications'
import type { Page } from '@/api/types'
import UiBadge from '@/components/ui/UiBadge.vue'
import UiButton from '@/components/ui/UiButton.vue'
import UiCard from '@/components/ui/UiCard.vue'
import UiDrawer from '@/components/ui/UiDrawer.vue'
import UiInput from '@/components/ui/UiInput.vue'
import UiModal from '@/components/ui/UiModal.vue'
import UiSelect from '@/components/ui/UiSelect.vue'
import UiTextarea from '@/components/ui/UiTextarea.vue'
import { useToastStore } from '@/stores/toasts'
import { useAuthStore } from '@/stores/auth'
import { formatWhen, toIsoFromLocalInput } from '@/utils/datetime'

const toasts = useToastStore()
const auth = useAuthStore()
const router = useRouter()

const filters = reactive({
  dogSize: '',
  sociabilityLevel: '',
  reactivityLevel: '',
})

const state = reactive<{ loading: boolean; page: Page<Meetup> | null; pageNum: number }>({
  loading: true,
  page: null,
  pageNum: 0,
})

const modal = reactive<{ open: boolean; mode: 'create' | 'edit'; submitting: boolean; meetupId: number | null }>({
  open: false,
  mode: 'create',
  submitting: false,
  meetupId: null,
})

const form = reactive({
  title: '',
  description: '',
  location: '',
  maxParticipants: '4',
  scheduledAt: '',
})

const drawer = reactive<{ open: boolean; meetup: Meetup | null; busy: boolean }>({
  open: false,
  meetup: null,
  busy: false,
})

const comms = reactive<{
  loading: boolean
  page: Page<Communication> | null
  draft: string
  sending: boolean
}>({
  loading: false,
  page: null,
  draft: '',
  sending: false,
})

async function refresh() {
  state.loading = true
  try {
    state.page = await listMeetups({
      page: state.pageNum,
      size: 12,
      dogSize: filters.dogSize || undefined,
      sociabilityLevel: filters.sociabilityLevel || undefined,
      reactivityLevel: filters.reactivityLevel || undefined,
    })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
    state.page = { content: [], totalElements: 0, totalPages: 0, number: 0, size: 12, numberOfElements: 0, first: true, last: true }
  } finally {
    state.loading = false
  }
}

function requireLogin(): boolean {
  if (auth.isLoggedIn) {
    return true
  }
  toasts.push({ tone: 'info', title: 'Login needed', message: 'Please login to continue.' })
  router.push('/')
  return false
}

function resetFilters() {
  filters.dogSize = ''
  filters.sociabilityLevel = ''
  filters.reactivityLevel = ''
  state.pageNum = 0
  void refresh()
}

function goPage(pageNum: number) {
  state.pageNum = Math.max(0, pageNum)
  void refresh()
}

function badgeTone(status: Meetup['status']) {
  if (status === 'RECRUITING') return 'sky'
  if (status === 'ENDED') return 'sun'
  return 'danger'
}

function openCreate() {
  if (!requireLogin()) {
    return
  }
  modal.open = true
  modal.mode = 'create'
  modal.meetupId = null
  form.title = ''
  form.description = ''
  form.location = ''
  form.maxParticipants = '4'
  form.scheduledAt = ''
}

function openEdit(m: Meetup) {
  if (!requireLogin()) {
    return
  }
  modal.open = true
  modal.mode = 'edit'
  modal.meetupId = m.id
  form.title = m.title
  form.description = m.description ?? ''
  form.location = m.location
  form.maxParticipants = String(m.maxParticipants)
  const d = new Date(m.scheduledAt)
  if (!Number.isNaN(d.getTime())) {
    const pad2 = (n: number) => String(n).padStart(2, '0')
    form.scheduledAt = `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}T${pad2(d.getHours())}:${pad2(d.getMinutes())}`
  } else {
    form.scheduledAt = ''
  }
}

function closeModal() {
  modal.open = false
}

async function submitModal() {
  if (!requireLogin()) {
    return
  }
  modal.submitting = true
  try {
    const payload = {
      title: form.title.trim(),
      description: form.description.trim() || undefined,
      location: form.location.trim(),
      maxParticipants: Number(form.maxParticipants),
      scheduledAt: toIsoFromLocalInput(form.scheduledAt),
    }

    if (modal.mode === 'create') {
      await createMeetup(payload)
      toasts.push({ tone: 'success', title: 'Created', message: 'Meetup created.' })
    } else if (modal.meetupId !== null) {
      await updateMeetup(modal.meetupId, payload)
      toasts.push({ tone: 'success', title: 'Updated', message: 'Meetup updated.' })
    }
    modal.open = false
    state.pageNum = 0
    await refresh()
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    modal.submitting = false
  }
}

async function openDetails(m: Meetup) {
  drawer.open = true
  drawer.meetup = m
  comms.page = null
  comms.draft = ''
  await loadMeetup(m.id)
  await loadComms(m.id)
}

async function loadMeetup(meetupId: number) {
  try {
    drawer.meetup = await getMeetup(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  }
}

async function loadComms(meetupId: number) {
  comms.loading = true
  try {
    comms.page = await listCommunications(meetupId, { page: 0, size: 20 })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    comms.loading = false
  }
}

function closeDrawer() {
  drawer.open = false
  drawer.meetup = null
}

async function onCancel(meetupId: number) {
  if (!requireLogin()) {
    return
  }
  drawer.busy = true
  try {
    await cancelMeetup(meetupId)
    toasts.push({ tone: 'success', title: 'Canceled', message: 'Meetup canceled.' })
    await refresh()
    await loadMeetup(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    drawer.busy = false
  }
}

async function onEnd(meetupId: number) {
  if (!requireLogin()) {
    return
  }
  drawer.busy = true
  try {
    await endMeetup(meetupId)
    toasts.push({ tone: 'success', title: 'Ended', message: 'Meetup ended.' })
    await refresh()
    await loadMeetup(meetupId)
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    drawer.busy = false
  }
}

async function submitComm() {
  if (!drawer.meetup) return
  if (!requireLogin()) {
    return
  }
  comms.sending = true
  try {
    await createCommunication(drawer.meetup.id, { content: comms.draft.trim() })
    comms.draft = ''
    await loadComms(drawer.meetup.id)
    toasts.push({ tone: 'success', title: 'Sent', message: 'Announcement posted.' })
  } catch (e) {
    const err = toApiClientError(e)
    toasts.push({ tone: 'error', title: err.code, message: err.message })
  } finally {
    comms.sending = false
  }
}

void refresh()
</script>

<style scoped>
.pageHead {
  display: flex;
  gap: var(--s-4);
  align-items: flex-end;
  justify-content: space-between;
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
  max-width: 68ch;
}

.filters {
  margin-top: var(--s-6);
  padding: var(--s-4);
}

.filters__grid {
  display: grid;
  gap: var(--s-4);
}

@media (min-width: 860px) {
  .filters__grid {
    grid-template-columns: 1fr 1fr 1fr auto;
    align-items: end;
  }
}

.filters__actions {
  display: flex;
  gap: var(--s-2);
  justify-content: flex-start;
  flex-wrap: wrap;
}

.loading {
  margin-top: var(--s-8);
  color: var(--c-ink-2);
}

.cards {
  margin-top: var(--s-6);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  align-items: start;
}

.meetupCard {
  text-align: left;
  border-radius: var(--r-lg);
  padding: var(--s-4);
  border: 1px solid rgba(27, 31, 35, 0.14);
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.08), 0 14px 40px rgba(27, 31, 35, 0.12);
  cursor: pointer;
  transition: transform 140ms ease, box-shadow 140ms ease;
}

.meetupCard:hover {
  transform: translateY(-2px);
  box-shadow: 0 1px 0 rgba(27, 31, 35, 0.08), 0 20px 60px rgba(27, 31, 35, 0.14);
}

.meetupCard__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.meetupCard__when {
  font-size: 12px;
  color: var(--c-ink-2);
}

.meetupCard__title {
  margin-top: 10px;
  font-weight: 900;
  letter-spacing: -0.02em;
  font-size: 16px;
}

.meetupCard__meta {
  margin-top: 6px;
  font-size: 13px;
  color: var(--c-ink-2);
}

.pager {
  margin-top: var(--s-6);
  display: flex;
  gap: var(--s-2);
  align-items: center;
  justify-content: center;
  color: var(--c-ink-2);
}

.pager__mid {
  font-size: 13px;
}

.empty {
  margin-top: var(--s-10);
  display: grid;
  justify-items: center;
  gap: 8px;
  text-align: center;
}

.empty__img {
  width: 180px;
  height: 180px;
  border-radius: 999px;
  object-fit: cover;
  border: 1px solid rgba(27, 31, 35, 0.12);
  box-shadow: var(--sh-1);
}

.empty__title {
  font-family: var(--font-display);
  font-size: 22px;
  letter-spacing: -0.02em;
}

.empty__sub {
  color: var(--c-ink-2);
  margin-bottom: var(--s-2);
}

.form {
  display: grid;
  gap: var(--s-4);
}

.form__actions {
  display: flex;
  gap: var(--s-2);
  flex-wrap: wrap;
}

.drawerBlock {
  padding: var(--s-4);
  border-radius: var(--r-lg);
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(27, 31, 35, 0.12);
}

.drawerBlock + .drawerBlock {
  margin-top: var(--s-4);
}

.drawerBlock__grid {
  display: grid;
  gap: var(--s-3);
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.drawerLabel {
  font-size: 12px;
  color: var(--c-ink-2);
}

.drawerValue {
  margin-top: 2px;
  font-weight: 700;
}

.drawerDesc {
  margin: var(--s-4) 0 0;
  color: var(--c-ink-2);
}

.drawerActions {
  margin-top: var(--s-4);
  display: flex;
  gap: var(--s-2);
  flex-wrap: wrap;
}

.drawerH {
  margin: 0;
  font-family: var(--font-display);
  letter-spacing: -0.02em;
}

.drawerMuted {
  margin: 8px 0 0;
  color: var(--c-ink-2);
  font-size: 13px;
}

.comms {
  margin-top: var(--s-3);
}

.comms__list {
  display: grid;
  gap: 10px;
  margin-top: var(--s-3);
}

.comms__item {
  padding: 10px 12px;
  border-radius: 16px;
  border: 1px solid rgba(27, 31, 35, 0.12);
  background: rgba(255, 255, 255, 0.72);
}

.comms__content {
  font-weight: 700;
  letter-spacing: -0.01em;
}

.comms__meta {
  margin-top: 4px;
  font-size: 12px;
  color: var(--c-ink-2);
}

.comms__form {
  display: grid;
  gap: var(--s-3);
  margin-top: var(--s-4);
}
</style>
