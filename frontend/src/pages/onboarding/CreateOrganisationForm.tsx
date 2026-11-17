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
import { applyServerFieldErrors, describedBy } from '@/utils/forms'
import {
  CODE_HINT,
  createOrganisationSchema,
  type CreateOrganisationFormValues,
} from './organisationSchemas'

export function CreateOrganisationForm() {
  const { applyOrganisation } = useAuth()

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<CreateOrganisationFormValues>({
    resolver: zodResolver(createOrganisationSchema),
    defaultValues: { name: '', code: '' },
  })

  const createOrganisation = useMutation({
    mutationFn: organisationApi.create,
    onSuccess: (data) => {
      toast.success(`${data.organisation.name} is ready`, {
        description: `Share the code "${data.organisation.code}" with your team so they can join.`,
        duration: 10_000,
      })
      // RequireNoOrganisation redirects to /projects once the role is stored.
      applyOrganisation(data)
    },
    onError: (error) => {
      // e.g. { code: "This code is already in use" } -> shown under the field.
      applyServerFieldErrors(error, setError, ['name', 'code'])
    },
  })

  const onSubmit = handleSubmit((values) => createOrganisation.mutate(values))
  const error = createOrganisation.error
  const showGeneralError = error && !error.hasFieldErrors

  return (
    <form onSubmit={onSubmit} noValidate className="space-y-5">
      <p className="text-sm text-neutral-600">
        Start a new organisation. You will be its <strong className="font-medium text-neutral-900">organisation manager</strong>{' '}
        and can create projects for your team.
      </p>

      {showGeneralError && <Alert variant="error">{error.message}</Alert>}

      <FormField id="org-name" label="Organisation name" error={errors.name?.message}>
        <Input
          id="org-name"
          autoComplete="organization"
          placeholder="e.g. Acme Corporation"
          invalid={!!errors.name}
          aria-describedby={describedBy('org-name', { error: errors.name?.message })}
          {...register('name')}
        />
      </FormField>

      <FormField id="org-code" label="Organisation code" hint={CODE_HINT} error={errors.code?.message}>
        <Input
          id="org-code"
          autoComplete="off"
          autoCapitalize="characters"
          spellCheck={false}
          placeholder="e.g. ACME-HQ"
          className="font-mono tracking-wide"
          invalid={!!errors.code}
          aria-describedby={describedBy('org-code', { error: errors.code?.message, hint: CODE_HINT })}
          {...register('code')}
        />
      </FormField>

      <Button type="submit" fullWidth loading={createOrganisation.isPending}>
        Create organisation
      </Button>
    </form>
  )
}
