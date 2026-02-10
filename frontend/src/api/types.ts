export type ApiError = {
  code: string
  message: string
  details: string[]
}

export type ApiResponse<T> = {
  success: boolean
  data: T | null
  error: ApiError | null
}

export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  numberOfElements: number
  first: boolean
  last: boolean
}
