import { zodResolver } from '@hookform/resolvers/zod'
import { useForm, useWatch } from 'react-hook-form'
import { Alert } from '@/components/common/Alert'
import { Button } from '@/components/common/Button'
import { FormField } from '@/components/common/FormField'
import { Input } from '@/components/common/Input'
import { Modal } from '@/components/common/Modal'
import { Textarea } from '@/components/common/Textarea'
import { useCreateProject } from '@/hooks/useProjects'
import {
  createProjectSchema,
  PROJECT_DESCRIPTION_MAX,
  type CreateProjectFormValues,
} from '@/pages/projects/projectSchemas'
import type { AppError } from '@/services/api/apiError'
import { applyServerFieldErrors, describedBy } from '@/utils/forms'

const FORM_ID = 'create-project-form'

interface CreateProjectModalProps {
  open: boolean
  onClose: () => void
}

export function CreateProjectModal({ open, onClose }: CreateProjectModalProps) {
  const createProject = useCreateProject()

  const close = () => {
    createProject.reset()
    onClose()
  }

  return (
    <Modal
      open={open}
      onClose={close}
      title="New project"
      description="You'll be the project manager. Employees in your organisation can then join it."
      preventClose={createProject.isPending}
      footer={
        <>
          <Button variant="secondary" onClick={close} disabled={createProject.isPending}>
            Cancel
          </Button>
          <Button type="submit" form={FORM_ID} loading={createProject.isPending}>
            Create project
          </Button>
        </>
      }
    >
      <CreateProjectForm
        onSubmit={(values, setServerErrors) =>
          createProject.mutate(
            { name: values.name, description: values.description || undefined },
            { onSuccess: close, onError: setServerErrors },
          )
        }
        generalError={
          createProject.error && !createProject.error.hasFieldErrors ? createProject.error.message : null
        }
      />
    </Modal>
  )
}

type ServerErrorHandler = (error: AppError) => void

/** Separate component so the form state lives only while the modal is open. */
function CreateProjectForm({
  onSubmit,
  generalError,
}: {
  onSubmit: (values: CreateProjectFormValues, setServerErrors: ServerErrorHandler) => void
  generalError: string | null
}) {
  const {
    register,
    handleSubmit,
    setError,
    control,
    formState: { errors },
  } = useForm<CreateProjectFormValues>({
    resolver: zodResolver(createProjectSchema),
    defaultValues: { name: '', description: '' },
  })

  const descriptionLength = useWatch({ control, name: 'description' })?.length ?? 0

  return (
    <form
      id={FORM_ID}
      noValidate
      className="space-y-5"
      onSubmit={handleSubmit((values) =>
        onSubmit(values, (error) => applyServerFieldErrors(error, setError, ['name', 'description'])),
      )}
    >
      {generalError && <Alert variant="error">{generalError}</Alert>}

      <FormField id="project-name" label="Project name" error={errors.name?.message}>
        <Input
          id="project-name"
          autoFocus
          autoComplete="off"
          placeholder="e.g. Website redesign"
          invalid={!!errors.name}
          aria-describedby={describedBy('project-name', { error: errors.name?.message })}
          {...register('name')}
        />
      </FormField>

      <FormField
        id="project-description"
        label="Description"
        error={errors.description?.message}
        labelAside={
          <span
            className={
              descriptionLength > PROJECT_DESCRIPTION_MAX ? 'text-xs text-red-600' : 'text-xs text-neutral-500'
            }
          >
            {descriptionLength}/{PROJECT_DESCRIPTION_MAX}
          </span>
        }
      >
        <Textarea
          id="project-description"
          rows={4}
          placeholder="What is this project about? (optional)"
          invalid={!!errors.description}
          aria-describedby={describedBy('project-description', { error: errors.description?.message })}
          {...register('description')}
        />
      </FormField>
    </form>
  )
}
