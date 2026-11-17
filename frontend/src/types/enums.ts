/*
 * Backend enums as `as const` arrays + derived union types.
 * (TypeScript `enum` is not allowed under `erasableSyntaxOnly`.)
 * The arrays double as option lists for selects and filters.
 */

export const ORGANISATION_ROLES = ['ORGANISATION_MANAGER', 'EMPLOYEE'] as const
export type OrganisationRole = (typeof ORGANISATION_ROLES)[number]

export const PROJECT_ROLES = ['MEMBER', 'PROJECT_MANAGER'] as const
export type ProjectRole = (typeof PROJECT_ROLES)[number]

export const TASK_STATUSES = ['TODO', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'] as const
export type TaskStatus = (typeof TASK_STATUSES)[number]

/** Statuses an assignee may set on their own assignment (CANCELLED is rejected). */
export const ASSIGNMENT_STATUSES = ['TODO', 'IN_PROGRESS', 'COMPLETED'] as const
export type AssignmentStatus = (typeof ASSIGNMENT_STATUSES)[number]

export const TASK_PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] as const
export type TaskPriority = (typeof TASK_PRIORITIES)[number]

export const TASK_TYPES = ['TASK', 'BUG', 'FEATURE', 'IMPROVEMENT'] as const
export type TaskType = (typeof TASK_TYPES)[number]

export const TASK_SCOPES = ['ALL', 'MY_TASKS', 'CREATED_BY_ME'] as const
export type TaskScope = (typeof TASK_SCOPES)[number]

export const TASK_SORT_FIELDS = ['deadline', 'createdAt', 'priority', 'title', 'status'] as const
export type TaskSortField = (typeof TASK_SORT_FIELDS)[number]

export const SORT_DIRECTIONS = ['ASC', 'DESC'] as const
export type SortDirection = (typeof SORT_DIRECTIONS)[number]

export const ORGANISATION_ROLE_LABELS: Record<OrganisationRole, string> = {
  ORGANISATION_MANAGER: 'Organisation manager',
  EMPLOYEE: 'Employee',
}

export const PROJECT_ROLE_LABELS: Record<ProjectRole, string> = {
  MEMBER: 'Member',
  PROJECT_MANAGER: 'Project manager',
}

export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'To do',
  IN_PROGRESS: 'In progress',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
}

export const TASK_PRIORITY_LABELS: Record<TaskPriority, string> = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'High',
  CRITICAL: 'Critical',
}

export const TASK_TYPE_LABELS: Record<TaskType, string> = {
  TASK: 'Task',
  BUG: 'Bug',
  FEATURE: 'Feature',
  IMPROVEMENT: 'Improvement',
}

export const TASK_SCOPE_LABELS: Record<TaskScope, string> = {
  ALL: 'All tasks',
  MY_TASKS: 'Assigned to me',
  CREATED_BY_ME: 'Created by me',
}
