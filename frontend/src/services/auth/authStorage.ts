import type { User } from '@/types/auth'
import type { Organisation } from '@/types/organisation'
import { readJson, removeItem, writeJson } from '@/utils/storage'

export const AUTH_STORAGE_KEY = 'taskly.auth'

export interface StoredAuth {
  token: string
  user: User
  /** Only known after registering/joining in this browser; login doesn't return it. */
  organisation: Organisation | null
}

function isStoredAuth(value: unknown): value is StoredAuth {
  if (typeof value !== 'object' || value === null) return false
  const candidate = value as Partial<StoredAuth>
  return (
    typeof candidate.token === 'string' &&
    typeof candidate.user === 'object' &&
    candidate.user !== null &&
    typeof candidate.user.id === 'number' &&
    typeof candidate.user.email === 'string'
  )
}

export function loadStoredAuth(): StoredAuth | null {
  const value = readJson(AUTH_STORAGE_KEY)
  if (!isStoredAuth(value)) return null
  return { ...value, organisation: value.organisation ?? null }
}

export function saveStoredAuth(auth: StoredAuth): void {
  writeJson(AUTH_STORAGE_KEY, auth)
}

export function clearStoredAuth(): void {
  removeItem(AUTH_STORAGE_KEY)
}
