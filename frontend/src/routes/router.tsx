import { createBrowserRouter, Navigate } from 'react-router-dom'
import { FullPageSpinner } from '@/components/common/Spinner'
import { AppLayout } from '@/components/layout/AppLayout'
import { RootLayout } from '@/components/layout/RootLayout'
import NotFoundPage from '@/pages/NotFoundPage'
import RouteErrorPage from '@/pages/RouteErrorPage'
import { RequireAuth, RequireGuest, RequireNoOrganisation, RequireOrganisation } from './guards'
import { paths } from './paths'

export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    errorElement: <RouteErrorPage />,
    hydrateFallbackElement: <FullPageSpinner />,
    children: [
      // Signed-out only
      {
        element: <RequireGuest />,
        children: [
          {
            path: paths.login,
            lazy: async () => ({ Component: (await import('@/pages/auth/LoginPage')).default }),
          },
          {
            path: paths.register,
            lazy: async () => ({ Component: (await import('@/pages/auth/RegisterPage')).default }),
          },
        ],
      },

      // Signed-in only
      {
        element: <RequireAuth />,
        children: [
          {
            element: <RequireNoOrganisation />,
            children: [
              {
                path: paths.onboarding,
                lazy: async () => ({
                  Component: (await import('@/pages/onboarding/OnboardingPage')).default,
                }),
              },
            ],
          },
          {
            element: <RequireOrganisation />,
            children: [
              {
                element: <AppLayout />,
                children: [
                  { index: true, element: <Navigate to={paths.projects} replace /> },
                  {
                    path: paths.projects,
                    lazy: async () => ({
                      Component: (await import('@/pages/projects/ProjectsPage')).default,
                    }),
                  },
                  {
                    path: paths.project(':projectId'),
                    lazy: async () => ({
                      Component: (await import('@/pages/projects/ProjectLayout')).default,
                    }),
                    children: [
                      {
                        index: true,
                        lazy: async () => ({
                          Component: (await import('@/pages/projects/ProjectTasksPage')).default,
                        }),
                      },
                      {
                        path: 'members',
                        lazy: async () => ({
                          Component: (await import('@/pages/projects/ProjectMembersPage')).default,
                        }),
                      },
                    ],
                  },
                ],
              },
            ],
          },
        ],
      },

      { path: '*', element: <NotFoundPage /> },
    ],
  },
])
