import { useOutletContext } from 'react-router-dom'
import type { ProjectRole } from '@/types/enums'
import type { ProjectDetails, ProjectMember } from '@/types/project'

/** Shared by every page nested under /projects/:projectId. */
export interface ProjectContextValue {
  projectId: number
  project: ProjectDetails
  /** Empty until loaded; check `membersLoaded`. */
  members: ProjectMember[]
  membersLoaded: boolean
  /** The current user's role in this project, or null if not a member. */
  myRole: ProjectRole | null
  isMember: boolean
}

export function useProjectContext(): ProjectContextValue {
  return useOutletContext<ProjectContextValue>()
}

/** Route params are strings; accept only positive integer ids. */
export function parseProjectId(value: string | undefined): number | null {
  if (!value || !/^\d+$/.test(value)) return null
  const id = Number(value)
  return Number.isSafeInteger(id) && id > 0 ? id : null
}
