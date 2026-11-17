/*
 * UI permission helpers. They only decide what to show; the backend
 * enforces the real rules on every request.
 */
import type { User } from '@/types/auth'
import type { ProjectSummary } from '@/types/project'

export function isOrganisationManager(user: User): boolean {
  return user.organisationRole === 'ORGANISATION_MANAGER'
}

/** Only ORGANISATION_MANAGERs can create projects (POST /project/create). */
export function canCreateProject(user: User): boolean {
  return isOrganisationManager(user)
}

/** Only EMPLOYEEs can join (POST /project/{id}/join), and only once. */
export function canJoinProject(user: User, project: Pick<ProjectSummary, 'joined'>): boolean {
  return user.organisationRole === 'EMPLOYEE' && !project.joined
}

/** Only ORGANISATION_MANAGERs can change project roles (PATCH .../members/{id}/role). */
export function canManageMemberRoles(user: User): boolean {
  return isOrganisationManager(user)
}
