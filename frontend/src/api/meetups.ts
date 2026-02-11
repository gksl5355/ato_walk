import { http, unwrap, unwrapNullable } from '@/api/http'
import type { ApiResponse, Page } from '@/api/types'

export type MeetupStatus = 'RECRUITING' | 'ENDED' | 'CANCELED'

export type DogSize = 'SMALL' | 'MEDIUM' | 'LARGE'
export type DogLevel = 'LOW' | 'MEDIUM' | 'HIGH'

export type Meetup = {
  id: number
  hostUserId: number
  title: string
  description: string | null
  location: string
  maxParticipants: number
  scheduledAt: string
  dogSize: DogSize | null
  sociabilityLevel: DogLevel | null
  reactivityLevel: DogLevel | null
  neutered: boolean | null
  status: MeetupStatus
}

export type CreateMeetupRequest = {
  title: string
  description?: string
  location: string
  maxParticipants: number
  scheduledAt: string
  dogSize?: DogSize
  sociabilityLevel?: DogLevel
  reactivityLevel?: DogLevel
  neutered?: boolean
}

export type UpdateMeetupRequest = CreateMeetupRequest

export async function listMeetups(params: {
  page?: number
  size?: number
  dogSize?: DogSize
  sociabilityLevel?: DogLevel
  reactivityLevel?: DogLevel
  neutered?: boolean
}): Promise<Page<Meetup>> {
  const res = await http.get<ApiResponse<Page<Meetup>>>('/v1/meetups', { params })
  return unwrap(res.data)
}

export async function getMeetup(meetupId: number): Promise<Meetup> {
  const res = await http.get<ApiResponse<Meetup>>(`/v1/meetups/${meetupId}`)
  return unwrap(res.data)
}

export async function createMeetup(req: CreateMeetupRequest): Promise<Meetup> {
  const res = await http.post<ApiResponse<Meetup>>('/v1/meetups', req)
  return unwrap(res.data)
}

export async function updateMeetup(meetupId: number, req: UpdateMeetupRequest): Promise<Meetup> {
  const res = await http.put<ApiResponse<Meetup>>(`/v1/meetups/${meetupId}`, req)
  return unwrap(res.data)
}

export async function cancelMeetup(meetupId: number): Promise<void> {
  const res = await http.post<ApiResponse<null>>(`/v1/meetups/${meetupId}/cancel`)
  unwrapNullable(res.data)
}

export async function endMeetup(meetupId: number): Promise<void> {
  const res = await http.post<ApiResponse<null>>(`/v1/meetups/${meetupId}/end`)
  unwrapNullable(res.data)
}
