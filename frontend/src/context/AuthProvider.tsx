import { useQueryClient } from '@tanstack/react-query'
import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import { paths } from '@/routes/paths'
import { authSession } from '@/services/auth/authSession'
import {
  AUTH_STORAGE_KEY,
  clearStoredAuth,
  loadStoredAuth,
  saveStoredAuth,
  type StoredAuth,
} from '@/services/auth/authStorage'
import type { LoginResponse } from '@/types/auth'
import type { OrganisationAuthData } from '@/types/organisation'
import { getTokenExpiry, isTokenExpired } from '@/utils/jwt'
import { AuthContext, type AuthContextValue, type SignOutOptions } from './authContext'

/** setTimeout's maximum delay (~24.8 days). */
const MAX_TIMEOUT_MS = 2 ** 31 - 1

function readValidStoredAuth(): StoredAuth | null {
  const stored = loadStoredAuth()
  if (!stored) return null
  if (isTokenExpired(stored.token)) {
    clearStoredAuth()
    return null
  }
  return stored
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const [auth, setAuth] = useState<StoredAuth | null>(() => {
    const initial = readValidStoredAuth()
    authSession.setToken(initial?.token ?? null)
    return initial
  })

  /** Single place that keeps React state, the axios token and storage in sync. */
  const persist = useCallback((next: StoredAuth | null) => {
    authSession.setToken(next?.token ?? null)
    if (next) saveStoredAuth(next)
    else clearStoredAuth()
    setAuth(next)
  }, [])

  const signIn = useCallback(
    (response: LoginResponse) => {
      queryClient.clear()
      persist({ token: response.jwtToken, user: response.user, organisation: null })
    },
    [persist, queryClient],
  )

  const applyOrganisation = useCallback(
    (data: OrganisationAuthData) => {
      if (!auth) return
      persist({
        token: data.jwtToken,
        organisation: data.organisation,
        user: {
          ...auth.user,
          name: data.user.name,
          email: data.user.email,
          organisationId: data.organisation.id,
          organisationRole: data.user.organisationRole,
        },
      })
      // Anything cached under the old (role-less) token is stale.
      void queryClient.invalidateQueries()
    },
    [auth, persist, queryClient],
  )

  const signOut = useCallback(
    (options?: SignOutOptions) => {
      navigate(options?.expired ? `${paths.login}?expired=1` : paths.login, { replace: true })
      persist(null)
      queryClient.clear()
    },
    [navigate, persist, queryClient],
  )

  // Let the axios interceptor end the session when the backend rejects the token.
  useEffect(
    () => authSession.setUnauthorizedHandler(() => signOut({ expired: true })),
    [signOut],
  )

  // Sign out automatically when the token expires (no refresh tokens exist).
  useEffect(() => {
    if (!auth) return
    const expiry = getTokenExpiry(auth.token)
    if (expiry === null) return
    const delay = Math.min(Math.max(expiry - Date.now(), 0), MAX_TIMEOUT_MS)
    const timer = window.setTimeout(() => signOut({ expired: true }), delay)
    return () => window.clearTimeout(timer)
  }, [auth, signOut])

  // Keep multiple tabs in sync (sign-in / sign-out / organisation change).
  useEffect(() => {
    const onStorage = (event: StorageEvent) => {
      if (event.key !== AUTH_STORAGE_KEY) return
      const next = readValidStoredAuth()
      authSession.setToken(next?.token ?? null)
      setAuth(next)
      queryClient.clear()
    }
    window.addEventListener('storage', onStorage)
    return () => window.removeEventListener('storage', onStorage)
  }, [queryClient])

  const value = useMemo<AuthContextValue>(
    () => ({
      user: auth?.user ?? null,
      organisation: auth?.organisation ?? null,
      isAuthenticated: auth !== null,
      signIn,
      applyOrganisation,
      signOut,
    }),
    [auth, signIn, applyOrganisation, signOut],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
