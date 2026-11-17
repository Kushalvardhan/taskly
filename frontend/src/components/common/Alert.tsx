import { CircleAlert, CircleCheck, Info } from 'lucide-react'
import type { ReactNode } from 'react'
import { cn } from '@/utils/cn'

const variants = {
  error: { icon: CircleAlert, className: 'bg-red-50 text-red-800 ring-red-200' },
  info: { icon: Info, className: 'bg-neutral-50 text-neutral-800 ring-neutral-200' },
  success: { icon: CircleCheck, className: 'bg-neutral-50 text-neutral-800 ring-neutral-200' },
} as const

interface AlertProps {
  variant?: keyof typeof variants
  title?: string
  children?: ReactNode
  className?: string
}

export function Alert({ variant = 'info', title, children, className }: AlertProps) {
  const { icon: Icon, className: variantClass } = variants[variant]
  return (
    <div
      role={variant === 'error' ? 'alert' : 'status'}
      className={cn('flex gap-3 rounded-md p-3 text-sm ring-1 ring-inset', variantClass, className)}
    >
      <Icon className="mt-0.5 size-4 shrink-0" aria-hidden />
      <div className="min-w-0 space-y-1">
        {title && <p className="font-semibold">{title}</p>}
        {children && <div>{children}</div>}
      </div>
    </div>
  )
}
