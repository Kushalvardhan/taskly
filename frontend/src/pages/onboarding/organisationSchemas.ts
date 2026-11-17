import { z } from 'zod'

// The backend has no validation on these fields, so the rules live here.
const NAME_MIN = 2
const NAME_MAX = 100
const CODE_MIN = 3
const CODE_MAX = 30

export const createOrganisationSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Organisation name is required')
    .min(NAME_MIN, `Name must be at least ${NAME_MIN} characters`)
    .max(NAME_MAX, `Name must be at most ${NAME_MAX} characters`),
  code: z
    .string()
    .trim()
    .min(1, 'Organisation code is required')
    .min(CODE_MIN, `Code must be at least ${CODE_MIN} characters`)
    .max(CODE_MAX, `Code must be at most ${CODE_MAX} characters`)
    .regex(/^[A-Za-z0-9_-]+$/, 'Use only letters, numbers, hyphens (-) and underscores (_)'),
})

export type CreateOrganisationFormValues = z.infer<typeof createOrganisationSchema>

export const joinOrganisationSchema = z.object({
  organisationCode: z.string().trim().min(1, 'Enter the code your organisation manager shared'),
})

export type JoinOrganisationFormValues = z.infer<typeof joinOrganisationSchema>

export const CODE_HINT = `Your team uses this to join. ${CODE_MIN}–${CODE_MAX} letters, numbers, - or _. Case-sensitive.`
