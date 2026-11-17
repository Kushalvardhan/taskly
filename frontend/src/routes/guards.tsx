import { Navigate, Outlet, useLocation, type Location } from 'react-router-dom'
import { useAuth } from '@/hooks/useAuth'
import { homePathFor, paths } from './paths'

interface FromState {
  from?: Location
}

/** Signed-in users only; remembers where they were headed. */
export function RequireAuth() {
  const { isAuthenticated } = useAuth()
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to={paths.login} replace state={{ from: location } satisfies FromState} />
  }
  return <Outlet />
}

/** Signed-out users only (login/register); signed-in users are sent onwards. */
export function RequireGuest() {
  const { user } = useAuth()
  const location = useLocation()

  if (user) {
    const from = (location.state as FromState | null)?.from
    // Only return to the original page if the user can actually use it.
    const target =
      from && user.organisationId !== null ? `${from.pathname}${from.search}` : homePathFor(user)
    return <Navigate to={target} replace />
  }
  return <Outlet />
}

/** Users who already belong to an organisation. */
export function RequireOrganisation() {
  const { user } = useAuth()
  if (user?.organisationId === null) return <Navigate to={paths.onboarding} replace />
  return <Outlet />
}

/** Users who don't belong to an organisation yet (onboarding). */
export function RequireNoOrganisation() {
  const { user } = useAuth()
  if (user && user.organisationId !== null) return <Navigate to={paths.projects} replace />
  return <Outlet />
}
