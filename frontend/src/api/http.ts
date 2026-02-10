import axios, { AxiosError } from 'axios'

import type { ApiError, ApiResponse } from '@/api/types'

export class ApiClientError extends Error {
  readonly code: string
  readonly details: string[]
  readonly httpStatus: number | null

  constructor(opts: { code: string; message: string; details?: string[]; httpStatus?: number | null }) {
    super(opts.message)
    this.name = 'ApiClientError'
    this.code = opts.code
    this.details = opts.details ?? []
    this.httpStatus = opts.httpStatus ?? null
  }
}

export const http = axios.create({
  baseURL: '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15_000,
})

function isApiError(value: unknown): value is ApiError {
  if (typeof value !== 'object' || value === null) {
    return false
  }
  const v = value as { code?: unknown; message?: unknown; details?: unknown }
  return typeof v.code === 'string' && typeof v.message === 'string' && Array.isArray(v.details)
}

export function unwrap<T>(envelope: ApiResponse<T>): T {
  if (envelope.success) {
    if (envelope.data === null) {
      throw new ApiClientError({
        code: 'COMMON_CLIENT_UNEXPECTED_NULL',
        message: 'Unexpected null response data',
      })
    }
    return envelope.data
  }

  const error = envelope.error
  if (error && isApiError(error)) {
    throw new ApiClientError({
      code: error.code,
      message: error.message,
      details: error.details,
    })
  }

  throw new ApiClientError({
    code: 'COMMON_CLIENT_UNKNOWN_ERROR',
    message: 'Unknown API error',
  })
}

export function unwrapNullable<T>(envelope: ApiResponse<T>): T | null {
  if (envelope.success) {
    return envelope.data
  }

  const error = envelope.error
  if (error && isApiError(error)) {
    throw new ApiClientError({
      code: error.code,
      message: error.message,
      details: error.details,
    })
  }

  throw new ApiClientError({
    code: 'COMMON_CLIENT_UNKNOWN_ERROR',
    message: 'Unknown API error',
  })
}

export function toApiClientError(err: unknown): ApiClientError {
  if (err instanceof ApiClientError) {
    return err
  }

  if (err instanceof AxiosError) {
    const status = err.response?.status ?? null
    const data = err.response?.data as unknown

    if (typeof data === 'object' && data !== null) {
      const maybeEnvelope = data as { success?: unknown; error?: unknown }
      if (maybeEnvelope.success === false && isApiError(maybeEnvelope.error)) {
        return new ApiClientError({
          code: maybeEnvelope.error.code,
          message: maybeEnvelope.error.message,
          details: maybeEnvelope.error.details,
          httpStatus: status,
        })
      }
    }

    if (err.code === 'ECONNABORTED') {
      return new ApiClientError({
        code: 'COMMON_CLIENT_TIMEOUT',
        message: 'Request timed out',
        httpStatus: status,
      })
    }

    return new ApiClientError({
      code: 'COMMON_CLIENT_NETWORK',
      message: 'Network error',
      httpStatus: status,
    })
  }

  if (err instanceof Error) {
    return new ApiClientError({
      code: 'COMMON_CLIENT_UNKNOWN',
      message: err.message,
    })
  }

  return new ApiClientError({
    code: 'COMMON_CLIENT_UNKNOWN',
    message: 'Unknown error',
  })
}
