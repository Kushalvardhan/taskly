import { useContext } from 'react'
import { AuthContext, type AuthContextValue } from '@/context/authContext'

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used inside <AuthProvider>')
  }
  return context
}

/** For routes behind RequireAuth, where a user is guaranteed. */
export function useCurrentUser() {
  const { user } = useAuth()
  if (!user) {
    throw new Error('useCurrentUser must be used on an authenticated route')
  }
  return user
}
