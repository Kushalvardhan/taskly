import { z } from 'zod'

// Limits mirror CreateUserRequestDto on the backend.
const NAME_MIN = 5
const NAME_MAX = 30
const PASSWORD_MIN = 8
const PASSWORD_MAX = 20

const emailField = z
  .string()
  .trim()
  .min(1, 'Email is required')
  .pipe(z.email('Enter a valid email address'))

export const loginSchema = z.object({
  email: emailField,
  password: z.string().min(1, 'Password is required'),
})

export type LoginFormValues = z.infer<typeof loginSchema>

export const registerSchema = z
  .object({
    name: z
      .string()
      .trim()
      .min(1, 'Name is required')
      .min(NAME_MIN, `Name must be between ${NAME_MIN} and ${NAME_MAX} characters`)
      .max(NAME_MAX, `Name must be between ${NAME_MIN} and ${NAME_MAX} characters`),
    email: emailField,
    password: z
      .string()
      .min(1, 'Password is required')
      .min(PASSWORD_MIN, `Password must be between ${PASSWORD_MIN} and ${PASSWORD_MAX} characters`)
      .max(PASSWORD_MAX, `Password must be between ${PASSWORD_MIN} and ${PASSWORD_MAX} characters`),
    confirmPassword: z.string().min(1, 'Please confirm your password'),
  })
  .refine((values) => values.password === values.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Passwords do not match',
  })

export type RegisterFormValues = z.infer<typeof registerSchema>

export const PASSWORD_HINT = `${PASSWORD_MIN}–${PASSWORD_MAX} characters. Avoid passwords used on other sites.`
