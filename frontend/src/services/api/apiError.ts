import axios from 'axios'
import type { ApiErrorResponse } from '@/types/api'

/**
 * The single error type every API call rejects with.
 * Normalises the backend's different failure shapes:
 *  - ApiErrorResponse JSON (GlobalExceptionHandler)
 *  - text/plain 401s from the JWT filter ("Token expired", ...)
 *  - empty bodies (URL-level 403s, swallowed errors)
 *  - network / timeout failures (no response at all)
 */
export class AppError extends Error {
  /** HTTP status, or null when no response was received. */
  readonly status: number | null
  /** Field name -> message, from validation errors. Empty when none. */
  readonly fieldErrors: Readonly<Record<string, string>>

  constructor(message: string, status: number | null, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.name = 'AppError'
    this.status = status
    this.fieldErrors = fieldErrors
  }

  get isNetworkError(): boolean {
    return this.status === null
  }

  get hasFieldErrors(): boolean {
    return Object.keys(this.fieldErrors).length > 0
  }
}

function isApiErrorResponse(data: unknown): data is ApiErrorResponse {
  return (
    typeof data === 'object' &&
    data !== null &&
    'status' in data &&
    'message' in data &&
    typeof (data as { message: unknown }).message === 'string'
  )
}

function fallbackMessage(status: number): string {
  if (status === 400) return 'The request was invalid. Please check your input.'
  if (status === 401) return 'You are not signed in or your session has ended.'
  if (status === 403) return "You don't have permission to do that."
  if (status === 404) return 'The requested item could not be found.'
  if (status === 409) return 'This conflicts with the current state. Refresh and try again.'
  // 502–504 also cover "backend not running" behind the dev proxy / a gateway.
  if (status >= 502 && status <= 504) return 'The server is unavailable right now. Please try again shortly.'
  if (status >= 500) return 'The server ran into a problem. Please try again later.'
  return 'Something went wrong. Please try again.'
}

/** Plain-text bodies we are willing to show (excludes HTML error pages). */
function readableText(data: unknown): string | null {
  if (typeof data !== 'string') return null
  const text = data.trim()
  if (!text || text.startsWith('<') || text.length > 300) return null
  return text
}

export function toAppError(error: unknown): AppError {
  if (error instanceof AppError) return error

  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return error.code === 'ECONNABORTED'
        ? new AppError('The request timed out. Please try again.', null)
        : new AppError("Can't reach the server. Check your connection and try again.", null)
    }

    const { status, data } = error.response

    // Never surface server-error details: in development they can contain
    // stack traces and SQL. Show a generic message instead.
    if (status >= 500) {
      return new AppError(fallbackMessage(status), status)
    }

    if (isApiErrorResponse(data)) {
      // Spring Boot's /error page puts the bare reason phrase ("Forbidden")
      // in `message`; prefer our friendlier text in that case.
      const isGeneric = !data.message || data.message === data.error
      return new AppError(
        isGeneric ? fallbackMessage(status) : data.message,
        status,
        data.errors ?? {},
      )
    }

    return new AppError(readableText(data) ?? fallbackMessage(status), status)
  }

  if (error instanceof Error) return new AppError(error.message, null)
  return new AppError('Something went wrong. Please try again.', null)
}
