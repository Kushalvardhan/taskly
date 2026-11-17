import axios, { type AxiosResponse } from 'axios'
import { env } from '@/config/env'
import { authSession } from '@/services/auth/authSession'
import type { ApiResponse } from '@/types/api'
import { isTokenExpired } from '@/utils/jwt'
import { toAppError } from './apiError'

const axiosInstance = axios.create({
  baseURL: env.apiBaseUrl,
  timeout: 20_000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

axiosInstance.interceptors.request.use((config) => {
  const token = authSession.getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/** Messages the backend's JwtTokenValidatorFilter sends as text/plain 401s. */
const JWT_FILTER_MESSAGES = new Set([
  'Token expired',
  'Invalid token',
  'Invalid Authorization header',
  'Required JWT claims are missing',
])

axiosInstance.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    const appError = toAppError(error)

    // End the session only when the 401 is really about the token. Some
    // unrelated backend errors can also surface as bare 401s, and those
    // must not log the user out.
    if (appError.status === 401) {
      const token = authSession.getToken()
      const rejectedByJwtFilter = JWT_FILTER_MESSAGES.has(appError.message)
      if (token && (rejectedByJwtFilter || isTokenExpired(token))) {
        authSession.notifyUnauthorized()
      }
    }

    return Promise.reject(appError)
  },
)

/** Unwraps the backend's ApiResponse envelope and returns `data`. */
export async function unwrap<T>(request: Promise<AxiosResponse<ApiResponse<T>>>): Promise<T> {
  const response = await request
  return response.data.data
}

export default axiosInstance
