import type { IsoDateString } from './api'
import type {
  AssignmentStatus,
  ProjectRole,
  SortDirection,
  TaskPriority,
  TaskScope,
  TaskSortField,
  TaskStatus,
  TaskType,
} from './enums'

/** CreatedByDto */
export interface TaskCreator {
  userId: number
  name: string
}

/** AssignmentSummaryDto */
export interface AssignmentSummary {
  totalAssignees: number
  completed: number
  inProgress: number
  todo: number
  cancelled: number
}

/** MyAssignmentDto – `status` is null when not assigned. */
export interface MyAssignment {
  assigned: boolean
  status: TaskStatus | null
}

/** AssigneePreviewDto (max 3 per task in list responses) */
export interface AssigneePreview {
  userId: number
  name: string
}

/** TaskListItemResponseDto */
export interface TaskListItem {
  taskId: number
  title: string
  description: string | null
  type: TaskType
  priority: TaskPriority
  overallStatus: TaskStatus
  deadline: IsoDateString | null
  createdBy: TaskCreator
  assignmentSummary: AssignmentSummary
  myAssignment: MyAssignment
  assigneePreview: AssigneePreview[]
}

/** TaskAssigneeDetailsDto */
export interface TaskAssignee {
  userId: number
  name: string
  projectRole: ProjectRole
  status: TaskStatus
}

/** TaskDetailsResponseDto */
export interface TaskDetails extends Omit<TaskListItem, 'assigneePreview'> {
  startDate: IsoDateString | null
  assignees: TaskAssignee[]
}

/** Query parameters for GET /project/{id}/tasks */
export interface TaskListParams {
  scope?: TaskScope
  status?: TaskStatus
  myStatus?: TaskStatus
  priority?: TaskPriority
  type?: TaskType
  assigneeId?: number
  search?: string
  sortBy?: TaskSortField
  sortDirection?: SortDirection
  page?: number
  size?: number
}

/** CreateTaskRequestDto */
export interface CreateTaskRequest {
  title: string
  description?: string
  startDate?: IsoDateString
  deadline?: IsoDateString
  type: TaskType
  priority: TaskPriority
}

/** TaskResponseDto – returned by create. */
export interface CreatedTask {
  id: number
  projectId: number
  creatorId: number
  title: string
  description: string | null
  assigneeId: number | null
  startDate: IsoDateString | null
  deadline: IsoDateString | null
  type: TaskType
  priority: TaskPriority
  status: TaskStatus
  createdAt: IsoDateString
}

/** UpdateTaskRequestDto – partial; omitted fields are left unchanged. */
export type UpdateTaskRequest = Partial<CreateTaskRequest>

/** UpdateTaskResponseDto */
export interface UpdatedTask {
  id: number
  title: string
  description: string | null
  type: TaskType
  priority: TaskPriority
  startDate: IsoDateString | null
  deadline: IsoDateString | null
}

/** TaskAssignmentStatusDto – used by assignee options and group assignment. */
export interface AssignmentOption {
  userId: number
  name: string
  email: string
  projectRole: ProjectRole
  assigned: boolean
}

/** TaskSelfAssignmentDto */
export interface SelfAssignment {
  assignmentId: number
  taskId: number
  status: TaskStatus
}

/** UpdateAssignmentStatusRequestDto */
export interface UpdateAssignmentStatusRequest {
  status: AssignmentStatus
}

/** UpdateAssignmentStatusDto */
export interface AssignmentStatusUpdate {
  taskId: number
  userId: number
  status: TaskStatus
  updatedAt: IsoDateString | null
}
