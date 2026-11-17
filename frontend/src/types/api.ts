/** ISO-8601 instant as serialised by the backend, e.g. "2026-12-01T09:00:00Z". */
export type IsoDateString = string

/** Success envelope: com.taskly.backend.common.ApiResponse */
export interface ApiResponse<T> {
  message: string
  status: number
  timestamp: IsoDateString
  data: T
}

/** Error body: com.taskly.backend.exception.ApiErrorResponse */
export interface ApiErrorResponse {
  timestamp: IsoDateString
  status: number
  error: string
  message: string
  path: string
  errors: Record<string, string> | null
}

/** Paginated list, e.g. TaskListResponseDto. `page` is zero-based. */
export interface Page<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}
