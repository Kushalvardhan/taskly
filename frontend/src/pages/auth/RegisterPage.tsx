import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { Alert } from '@/components/common/Alert'
import { Button } from '@/components/common/Button'
import { FormField } from '@/components/common/FormField'
import { Input, PasswordInput } from '@/components/common/Input'
import { AuthLayout } from '@/components/layout/AuthLayout'
import { paths } from '@/routes/paths'
import { authApi } from '@/services/api/authApi'
import { applyServerFieldErrors, describedBy } from '@/utils/forms'
import { PASSWORD_HINT, registerSchema, type RegisterFormValues } from './authSchemas'

export default function RegisterPage() {
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { name: '', email: '', password: '', confirmPassword: '' },
  })

  const registration = useMutation({
    mutationFn: authApi.register,
    onSuccess: (_message, variables) => {
      navigate(paths.login, { state: { registeredEmail: variables.email } })
    },
    onError: (error) => {
      applyServerFieldErrors(error, setError, ['name', 'email', 'password'])
    },
  })

  const onSubmit = handleSubmit(({ name, email, password }) =>
    registration.mutate({ name, email, password }),
  )

  const generalError =
    registration.error && !registration.error.hasFieldErrors ? registration.error.message : null

  return (
    <AuthLayout
      title="Create your account"
      subtitle="Then create or join your organisation."
      footer={
        <>
          Already have an account?{' '}
          <Link to={paths.login} className="font-semibold text-neutral-900 hover:text-neutral-600 underline-offset-4 hover:underline">
            Sign in
          </Link>
        </>
      }
    >
      <form onSubmit={onSubmit} noValidate className="space-y-5">
        {generalError && <Alert variant="error">{generalError}</Alert>}

        <FormField id="name" label="Full name" error={errors.name?.message}>
          <Input
            id="name"
            autoComplete="name"
            autoFocus
            invalid={!!errors.name}
            aria-describedby={describedBy('name', { error: errors.name?.message })}
            {...register('name')}
          />
        </FormField>

        <FormField id="email" label="Email" error={errors.email?.message}>
          <Input
            id="email"
            type="email"
            autoComplete="email"
            inputMode="email"
            invalid={!!errors.email}
            aria-describedby={describedBy('email', { error: errors.email?.message })}
            {...register('email')}
          />
        </FormField>

        <FormField id="password" label="Password" hint={PASSWORD_HINT} error={errors.password?.message}>
          <PasswordInput
            id="password"
            autoComplete="new-password"
            invalid={!!errors.password}
            aria-describedby={describedBy('password', {
              error: errors.password?.message,
              hint: PASSWORD_HINT,
            })}
            {...register('password')}
          />
        </FormField>

        <FormField id="confirmPassword" label="Confirm password" error={errors.confirmPassword?.message}>
          <PasswordInput
            id="confirmPassword"
            autoComplete="new-password"
            invalid={!!errors.confirmPassword}
            aria-describedby={describedBy('confirmPassword', {
              error: errors.confirmPassword?.message,
            })}
            {...register('confirmPassword')}
          />
        </FormField>

        <Button type="submit" fullWidth loading={registration.isPending}>
          Create account
        </Button>
      </form>
    </AuthLayout>
  )
}
