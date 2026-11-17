import { useRef, type KeyboardEvent, type ReactNode } from 'react'
import { cn } from '@/utils/cn'

export interface TabItem<T extends string> {
  id: T
  label: ReactNode
}

interface TabsProps<T extends string> {
  /** Prefix for generated element ids (must be unique on the page). */
  idPrefix: string
  label: string
  items: readonly TabItem<T>[]
  value: T
  onChange: (value: T) => void
  className?: string
}

const tabId = (prefix: string, id: string) => `${prefix}-tab-${id}`
const panelId = (prefix: string, id: string) => `${prefix}-panel-${id}`

/**
 * Segmented tab list following the WAI-ARIA tabs pattern
 * (arrow keys / Home / End move between tabs).
 * Render the matching <TabPanel> yourself.
 */
export function Tabs<T extends string>({ idPrefix, label, items, value, onChange, className }: TabsProps<T>) {
  const refs = useRef<Array<HTMLButtonElement | null>>([])

  const focusTab = (index: number) => {
    const next = (index + items.length) % items.length
    onChange(items[next].id)
    refs.current[next]?.focus()
  }

  const onKeyDown = (event: KeyboardEvent<HTMLButtonElement>, index: number) => {
    if (event.key === 'ArrowRight') focusTab(index + 1)
    else if (event.key === 'ArrowLeft') focusTab(index - 1)
    else if (event.key === 'Home') focusTab(0)
    else if (event.key === 'End') focusTab(items.length - 1)
    else return
    event.preventDefault()
  }

  return (
    <div
      role="tablist"
      aria-label={label}
      className={cn('grid auto-cols-fr grid-flow-col gap-1 rounded-md bg-neutral-100 p-1', className)}
    >
      {items.map((item, index) => {
        const selected = item.id === value
        return (
          <button
            key={item.id}
            ref={(element) => {
              refs.current[index] = element
            }}
            type="button"
            role="tab"
            id={tabId(idPrefix, item.id)}
            aria-selected={selected}
            aria-controls={panelId(idPrefix, item.id)}
            tabIndex={selected ? 0 : -1}
            onClick={() => onChange(item.id)}
            onKeyDown={(event) => onKeyDown(event, index)}
            className={cn(
              'rounded px-3 py-2 text-sm font-medium transition-colors',
              'focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-neutral-900',
              selected
                ? 'bg-white text-neutral-900 shadow-xs ring-1 ring-neutral-200'
                : 'text-neutral-600 hover:text-neutral-900',
            )}
          >
            {item.label}
          </button>
        )
      })}
    </div>
  )
}

export function TabPanel({
  idPrefix,
  id,
  children,
  className,
}: {
  idPrefix: string
  id: string
  children: ReactNode
  className?: string
}) {
  return (
    <div
      role="tabpanel"
      id={panelId(idPrefix, id)}
      aria-labelledby={tabId(idPrefix, id)}
      tabIndex={0}
      className={cn('focus-visible:outline-none', className)}
    >
      {children}
    </div>
  )
}
