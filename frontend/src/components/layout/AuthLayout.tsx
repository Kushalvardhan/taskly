import type { ReactNode } from 'react'
import { Logo } from '@/components/common/Logo'

interface AuthLayoutProps {
  title: string
  subtitle?: ReactNode
  children: ReactNode
  footer?: ReactNode
}

export function AuthLayout({ title, subtitle, children, footer }: AuthLayoutProps) {
  return (
    <div className="flex min-h-dvh flex-col justify-center px-4 py-10 sm:px-6">
      <div className="mx-auto w-full max-w-md">
        <div className="mb-6 flex justify-center">
          <Logo />
        </div>
        <div className="rounded-xl bg-white p-6 shadow-sm ring-1 ring-neutral-200 sm:p-8">
          <div className="mb-6 space-y-1 text-center">
            <h1 className="text-xl font-semibold tracking-tight text-neutral-900 sm:text-2xl">{title}</h1>
            {subtitle && <p className="text-sm text-neutral-600">{subtitle}</p>}
          </div>
          {children}
        </div>
        {footer && <div className="mt-6 text-center text-sm text-neutral-600">{footer}</div>}
      </div>
    </div>
  )
}
