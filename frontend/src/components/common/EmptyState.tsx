import type { LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'
import { cn } from '@/utils/cn'

interface EmptyStateProps {
  icon: LucideIcon
  title: string
  description?: ReactNode
  action?: ReactNode
  className?: string
}

export function EmptyState({ icon: Icon, title, description, action, className }: EmptyStateProps) {
  return (
    <div
      className={cn(
        'flex flex-col items-center rounded-xl border border-dashed border-neutral-300 bg-white px-6 py-12 text-center',
        className,
      )}
    >
      <span className="flex size-12 items-center justify-center rounded-full bg-neutral-100">
        <Icon className="size-6 text-neutral-600" aria-hidden />
      </span>
      <h3 className="mt-4 text-sm font-semibold text-neutral-900">{title}</h3>
      {description && <p className="mt-1 max-w-sm text-sm text-neutral-600">{description}</p>}
      {action && <div className="mt-6">{action}</div>}
    </div>
  )
}
