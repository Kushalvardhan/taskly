import { Badge } from '@/components/common/Badge'
import { PROJECT_ROLE_LABELS, type ProjectRole } from '@/types/enums'

/** The current user's relationship to a project. */
export function ProjectRoleBadge({ joined, role }: { joined: boolean; role: ProjectRole | null }) {
  if (!joined || !role) return <Badge tone="muted">Not joined</Badge>
  return <Badge tone={role === 'PROJECT_MANAGER' ? 'solid' : 'outline'}>{PROJECT_ROLE_LABELS[role]}</Badge>
}
