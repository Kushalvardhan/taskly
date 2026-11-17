import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { Link, useLocation, useSearchParams } from 'react-router-dom'
import { Alert } from '@/components/common/Alert'
import { Button } from '@/components/common/Button'
import { FormField } from '@/components/common/FormField'
import { Input, PasswordInput } from '@/components/common/Input'
import { AuthLayout } from '@/components/layout/AuthLayout'
import { useAuth } from '@/hooks/useAuth'
import { paths } from '@/routes/paths'
import { authApi } from '@/services/api/authApi'
import { applyServerFieldErrors, describedBy } from '@/utils/forms'
import { loginSchema, type LoginFormValues } from './authSchemas'

interface LoginLocationState {
  registeredEmail?: string
}

export default function LoginPage() {
  const { signIn } = useAuth()
  const location = useLocation()
  const [searchParams] = useSearchParams()
  const registeredEmail = (location.state as LoginLocationState | null)?.registeredEmail
  const sessionExpired = searchParams.get('expired') === '1'

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: registeredEmail ?? '', password: '' },
  })

  const login = useMutation({
    mutationFn: authApi.login,
    // RequireGuest redirects once the session is stored.
    onSuccess: signIn,
    onError: (error) => {
      applyServerFieldErrors(error, setError, ['email', 'password'])
    },
  })

  const onSubmit = handleSubmit((values) => login.mutate(values))
  const generalError = login.error && !login.error.hasFieldErrors ? login.error.message : null

  return (
    <AuthLayout
      title="Sign in to Taskly"
      subtitle="Welcome back. Enter your details to continue."
      footer={
        <>
          New to Taskly?{' '}
          <Link to={paths.register} className="font-semibold text-neutral-900 hover:text-neutral-600 underline-offset-4 hover:underline">
            Create an account
          </Link>
        </>
      }
    >
      <form onSubmit={onSubmit} noValidate className="space-y-5">
        {generalError ? (
          <Alert variant="error">{generalError}</Alert>
        ) : registeredEmail ? (
          <Alert variant="success">Account created. Sign in to continue.</Alert>
        ) : sessionExpired ? (
          <Alert variant="info">Your session has expired. Please sign in again.</Alert>
        ) : null}

        <FormField id="email" label="Email" error={errors.email?.message}>
          <Input
            id="email"
            type="email"
            autoComplete="email"
            inputMode="email"
            autoFocus={!registeredEmail}
            invalid={!!errors.email}
            aria-describedby={describedBy('email', { error: errors.email?.message })}
            {...register('email')}
          />
        </FormField>

        <FormField id="password" label="Password" error={errors.password?.message}>
          <PasswordInput
            id="password"
            autoComplete="current-password"
            autoFocus={!!registeredEmail}
            invalid={!!errors.password}
            aria-describedby={describedBy('password', { error: errors.password?.message })}
            {...register('password')}
          />
        </FormField>

        <Button type="submit" fullWidth loading={login.isPending}>
          Sign in
        </Button>
      </form>
    </AuthLayout>
  )
}
