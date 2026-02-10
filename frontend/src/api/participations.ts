import { http, unwrap, unwrapNullable } from '@/api/http'
import type { ApiResponse, Page } from '@/api/types'

export type ParticipationStatus = 'REQUESTED' | 'APPROVED' | 'REJECTED'

export type Participation = {
  id: number
  meetupId: number
  userId: number
  userNickname: string
  status: ParticipationStatus
  createdAt: string
}

export async function requestParticipation(meetupId: number): Promise<Participation> {
  const res = await http.post<ApiResponse<Participation>>(`/v1/meetups/${meetupId}/participations`)
  return unwrap(res.data)
}

export async function getMyParticipation(meetupId: number): Promise<Participation | null> {
  const res = await http.get<ApiResponse<Participation>>(`/v1/meetups/${meetupId}/participations/me`)
  return unwrapNullable(res.data)
}

export async function listRequestedParticipations(meetupId: number, params: { page?: number; size?: number }) {
  const res = await http.get<ApiResponse<Page<Participation>>>(`/v1/meetups/${meetupId}/participations`, { params })
  return unwrap(res.data)
}

export async function listApprovedParticipations(meetupId: number, params: { page?: number; size?: number }) {
  const res = await http.get<ApiResponse<Page<Participation>>>(`/v1/meetups/${meetupId}/participations/approved`, { params })
  return unwrap(res.data)
}

export async function approveParticipation(meetupId: number, participationId: number): Promise<Participation> {
  const res = await http.post<ApiResponse<Participation>>(
    `/v1/meetups/${meetupId}/participations/${participationId}/approve`,
  )
  return unwrap(res.data)
}

export async function rejectParticipation(meetupId: number, participationId: number): Promise<Participation> {
  const res = await http.post<ApiResponse<Participation>>(
    `/v1/meetups/${meetupId}/participations/${participationId}/reject`,
  )
  return unwrap(res.data)
}
