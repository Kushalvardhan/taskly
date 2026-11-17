/**
 * Central TanStack Query keys. Hierarchical so that invalidating
 * `queryKeys.projects.all` also refreshes every project-scoped query.
 */
export const queryKeys = {
  projects: {
    all: ['projects'] as const,
    list: () => [...queryKeys.projects.all, 'list'] as const,
    detail: (projectId: number) => [...queryKeys.projects.all, 'detail', projectId] as const,
    members: (projectId: number) => [...queryKeys.projects.all, 'members', projectId] as const,
  },
}
