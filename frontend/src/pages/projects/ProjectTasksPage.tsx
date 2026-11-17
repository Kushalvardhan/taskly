import { ListTodo } from 'lucide-react'
import { EmptyState } from '@/components/common/EmptyState'

/** Phase 6 replaces this with the filterable task list. */
export default function ProjectTasksPage() {
  return (
    <EmptyState
      icon={ListTodo}
      title="Tasks are coming next"
      description="The task list with filters, sorting and pagination arrives in the next phase."
    />
  )
}
