import { Outlet, ScrollRestoration } from 'react-router-dom'
import { AuthProvider } from '@/context/AuthProvider'

/** Top-level route element: providers that need the router live here. */
export function RootLayout() {
  return (
    <AuthProvider>
      <Outlet />
      <ScrollRestoration />
    </AuthProvider>
  )
}
