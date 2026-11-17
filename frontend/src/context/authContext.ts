import { createContext } from 'react'
import type { LoginResponse, User } from '@/types/auth'
import type { Organisation, OrganisationAuthData } from '@/types/organisation'

export interface SignOutOptions {
  /** Show the "session expired" notice on the login page. */
  expired?: boolean
}

export interface AuthContextValue {
  user: User | null
  /** Only available after creating/joining an organisation in this browser. */
  organisation: Organisation | null
  isAuthenticated: boolean
  /** Stores the session returned by POST /auth/login. */
  signIn: (response: LoginResponse) => void
  /** Swaps in the new token + role returned by organisation register/join. */
  applyOrganisation: (data: OrganisationAuthData) => void
  signOut: (options?: SignOutOptions) => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)
