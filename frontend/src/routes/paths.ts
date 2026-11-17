import type { User } from '@/types/auth'

export const paths = {
  login: '/login',
  register: '/register',
  onboarding: '/onboarding',
  projects: '/projects',
  project: (projectId: number | string) => `/projects/${projectId}`,
  projectMembers: (projectId: number | string) => `/projects/${projectId}/members`,
  task: (projectId: number | string, taskId: number | string) =>
    `/projects/${projectId}/tasks/${taskId}`,
} as const

/** Where a signed-in user belongs by default. */
export function homePathFor(user: User): string {
  return user.organisationId === null ? paths.onboarding : paths.projects
}
