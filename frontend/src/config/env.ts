const rawApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim() || '/api'

export const env = {
  /** Base URL for API calls, without a trailing slash. */
  apiBaseUrl: rawApiBaseUrl.replace(/\/+$/, ''),
} as const
