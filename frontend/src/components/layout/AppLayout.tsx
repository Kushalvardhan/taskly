import { LogOut } from 'lucide-react'
import { Link, NavLink, Outlet } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { Logo } from '@/components/common/Logo'
import { useAuth, useCurrentUser } from '@/hooks/useAuth'
import { paths } from '@/routes/paths'
import { ORGANISATION_ROLE_LABELS } from '@/types/enums'
import { cn } from '@/utils/cn'
import { getInitials } from '@/utils/format'

export function AppLayout() {
  const user = useCurrentUser()
  const { signOut } = useAuth()
  const roleLabel = user.organisationRole ? ORGANISATION_ROLE_LABELS[user.organisationRole] : null

  return (
    <div className="flex min-h-dvh flex-col">
      <a
        href="#main"
        className="sr-only z-50 rounded-md bg-white px-3 py-2 text-sm font-medium focus:not-sr-only focus:fixed focus:top-2 focus:left-2"
      >
        Skip to content
      </a>

      <header className="sticky top-0 z-30 border-b border-neutral-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex h-14 max-w-6xl items-center justify-between gap-3 px-4 sm:h-16 sm:px-6 lg:px-8">
          <div className="flex min-w-0 items-center gap-4 sm:gap-8">
            <Link to={paths.projects} aria-label="Taskly home">
              <Logo />
            </Link>
            <nav aria-label="Main">
              <NavLink
                to={paths.projects}
                className={({ isActive }) =>
                  cn(
                    'rounded-md px-2 py-1.5 text-sm font-medium transition-colors sm:px-3',
                    isActive ? 'bg-neutral-100 text-neutral-900' : 'text-neutral-600 hover:text-neutral-900',
                  )
                }
              >
                Projects
              </NavLink>
            </nav>
          </div>

          <div className="flex items-center gap-2 sm:gap-3">
            <div className="hidden min-w-0 text-right md:block">
              <p className="truncate text-sm font-medium text-neutral-900">{user.name}</p>
              {roleLabel && <p className="truncate text-xs text-neutral-500">{roleLabel}</p>}
            </div>
            <span
              className="flex size-8 shrink-0 items-center justify-center rounded-full bg-neutral-200 text-xs font-semibold text-neutral-900"
              title={user.name}
              aria-hidden
            >
              {getInitials(user.name)}
            </span>
            <Button variant="ghost" size="sm" onClick={() => signOut()} aria-label="Sign out">
              <LogOut className="size-4" aria-hidden />
              <span className="hidden sm:inline">Sign out</span>
            </Button>
          </div>
        </div>
      </header>

      <main id="main" className="mx-auto w-full max-w-6xl flex-1 px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
        <Outlet />
      </main>
    </div>
  )
}
