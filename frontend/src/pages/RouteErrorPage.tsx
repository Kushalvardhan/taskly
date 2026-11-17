import { isRouteErrorResponse, useRouteError } from 'react-router-dom'
import { Button } from '@/components/common/Button'

/** Shown when rendering a route throws (last-resort error boundary). */
export default function RouteErrorPage() {
  const error = useRouteError()
  const detail = isRouteErrorResponse(error)
    ? `${error.status} ${error.statusText}`
    : error instanceof Error
      ? error.message
      : null

  return (
    <div className="flex min-h-dvh flex-col items-center justify-center px-4 text-center">
      <h1 className="text-2xl font-semibold text-neutral-900">Something went wrong</h1>
      <p className="mt-2 max-w-md text-sm text-neutral-600">
        An unexpected error occurred. Reloading the page usually fixes it.
      </p>
      {import.meta.env.DEV && detail && (
        <pre className="mt-4 max-w-full overflow-x-auto rounded-lg bg-neutral-100 p-3 text-left text-xs text-neutral-700">
          {detail}
        </pre>
      )}
      <Button className="mt-6" onClick={() => window.location.reload()}>
        Reload page
      </Button>
    </div>
  )
}
