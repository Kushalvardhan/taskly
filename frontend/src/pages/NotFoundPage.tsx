import { ArrowLeft } from 'lucide-react'
import { Link } from 'react-router-dom'

export default function NotFoundPage() {
  return (
    <div className="flex min-h-dvh flex-col items-center justify-center px-4 text-center">
      <p className="text-sm font-semibold text-neutral-900">404</p>
      <h1 className="mt-2 text-2xl font-semibold text-neutral-900 sm:text-3xl">Page not found</h1>
      <p className="mt-2 text-sm text-neutral-600">The page you're looking for doesn't exist or has moved.</p>
      <Link
        to="/"
        className="mt-6 inline-flex items-center gap-2 text-sm font-semibold text-neutral-900 hover:text-neutral-700"
      >
        <ArrowLeft className="size-4" aria-hidden />
        Back to Taskly
      </Link>
    </div>
  )
}
