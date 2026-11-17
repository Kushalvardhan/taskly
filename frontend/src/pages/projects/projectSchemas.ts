import { z } from 'zod'

// Limits mirror CreateProjReqDto on the backend.
export const PROJECT_NAME_MAX = 100
export const PROJECT_DESCRIPTION_MAX = 500

export const createProjectSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Project name is required')
    .max(PROJECT_NAME_MAX, `Project name cannot exceed ${PROJECT_NAME_MAX} characters`),
  description: z
    .string()
    .trim()
    .max(PROJECT_DESCRIPTION_MAX, `Description cannot exceed ${PROJECT_DESCRIPTION_MAX} characters`),
})

export type CreateProjectFormValues = z.infer<typeof createProjectSchema>
