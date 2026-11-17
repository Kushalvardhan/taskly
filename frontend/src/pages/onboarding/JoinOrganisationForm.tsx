import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { Alert } from '@/components/common/Alert'
import { Button } from '@/components/common/Button'
import { FormField } from '@/components/common/FormField'
import { Input } from '@/components/common/Input'
import { useAuth } from '@/hooks/useAuth'
import { organisationApi } from '@/services/api/organisationApi'
import { describedBy } from '@/utils/forms'
import { joinOrganisationSchema, type JoinOrganisationFormValues } from './organisationSchemas'

const JOIN_HINT = 'Codes are case-sensitive.'

export function JoinOrganisationForm() {
  const { applyOrganisation } = useAuth()

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<JoinOrganisationFormValues>({
    resolver: zodResolver(joinOrganisationSchema),
    defaultValues: { organisationCode: '' },
  })

  const joinOrganisation = useMutation({
    mutationFn: organisationApi.join,
    onSuccess: (data) => {
      toast.success(`Welcome to ${data.organisation.name}`)
      // RequireNoOrganisation redirects to /projects once the role is stored.
      applyOrganisation(data)
    },
    onError: (error) => {
      if (error.status === 404) {
        setError(
          'organisationCode',
          { type: 'server', message: 'No organisation matches this code. Check it and try again.' },
          { shouldFocus: true },
        )
      }
    },
  })

  const onSubmit = handleSubmit((values) => joinOrganisation.mutate(values))
  const error = joinOrganisation.error
  const showGeneralError = error && error.status !== 404

  return (
    <form onSubmit={onSubmit} noValidate className="space-y-5">
      <p className="text-sm text-neutral-600">
        Ask your organisation manager for the code. You will join as an{' '}
        <strong className="font-medium text-neutral-900">employee</strong> and can then join projects.
      </p>

      {showGeneralError && <Alert variant="error">{error.message}</Alert>}

      <FormField
        id="join-code"
        label="Organisation code"
        hint={JOIN_HINT}
        error={errors.organisationCode?.message}
      >
        <Input
          id="join-code"
          autoComplete="off"
          autoCapitalize="off"
          spellCheck={false}
          placeholder="e.g. ACME-HQ"
          className="font-mono tracking-wide"
          invalid={!!errors.organisationCode}
          aria-describedby={describedBy('join-code', {
            error: errors.organisationCode?.message,
            hint: JOIN_HINT,
          })}
          {...register('organisationCode')}
        />
      </FormField>

      <Button type="submit" fullWidth loading={joinOrganisation.isPending}>
        Join organisation
      </Button>
    </form>
  )
}
