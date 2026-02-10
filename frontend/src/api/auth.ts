import { http, unwrap, unwrapNullable } from '@/api/http'
import type { ApiResponse } from '@/api/types'

export type User = {
  id: number
  email: string
  status: 'ACTIVE' | 'BLOCKED'
  createdAt: string
}

export type LoginRequest = {
  email: string
  password: string
}

export type SignupRequest = {
  email: string
  password: string
  dogName: string
  dogBreed: string
  dogSize: 'SMALL' | 'MEDIUM' | 'LARGE'
  dogNeutered: boolean
  dogSociabilityLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  dogReactivityLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  dogNotes?: string
}

export async function login(req: LoginRequest): Promise<User> {
  const res = await http.post<ApiResponse<User>>('/v1/login', req)
  return unwrap(res.data)
}

export async function signup(req: SignupRequest): Promise<User> {
  const res = await http.post<ApiResponse<User>>('/v1/signup', req)
  return unwrap(res.data)
}

export async function logout(): Promise<void> {
  const res = await http.post<ApiResponse<null>>('/v1/logout')
  unwrapNullable(res.data)
}

export async function getMe(): Promise<User> {
  const res = await http.get<ApiResponse<User>>('/v1/users/me')
  return unwrap(res.data)
}
