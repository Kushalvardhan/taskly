import type { ReactNode } from 'react'

interface PageHeaderProps {
  title: string
  description?: ReactNode
  /** Primary page actions, shown on the right (stacked below on mobile). */
  actions?: ReactNode
}

export function PageHeader({ title, description, actions }: PageHeaderProps) {
  return (
    <div className="flex flex-col gap-4 border-b border-neutral-200 pb-5 sm:flex-row sm:items-end sm:justify-between">
      <div className="min-w-0 space-y-1">
        <h1 className="text-2xl font-semibold tracking-tight text-neutral-900">{title}</h1>
        {description && <p className="text-sm text-neutral-600">{description}</p>}
      </div>
      {actions && <div className="flex shrink-0 flex-wrap gap-2">{actions}</div>}
    </div>
  )
}
