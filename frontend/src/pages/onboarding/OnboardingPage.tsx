import { useSearchParams } from 'react-router-dom'
import { TabPanel, Tabs, type TabItem } from '@/components/common/Tabs'
import { AuthLayout } from '@/components/layout/AuthLayout'
import { useAuth, useCurrentUser } from '@/hooks/useAuth'
import { CreateOrganisationForm } from './CreateOrganisationForm'
import { JoinOrganisationForm } from './JoinOrganisationForm'

type Mode = 'join' | 'create'

const MODES: readonly TabItem<Mode>[] = [
  { id: 'join', label: 'Join organisation' },
  { id: 'create', label: 'Create organisation' },
]

const TABS_ID = 'onboarding'

export default function OnboardingPage() {
  const user = useCurrentUser()
  const { signOut } = useAuth()
  const [searchParams, setSearchParams] = useSearchParams()

  // Kept in the URL (?mode=create) so the choice survives refresh and can be linked.
  const mode: Mode = searchParams.get('mode') === 'create' ? 'create' : 'join'
  const setMode = (next: Mode) => setSearchParams(next === 'join' ? {} : { mode: next }, { replace: true })

  return (
    <AuthLayout
      title="Set up your workspace"
      subtitle="Join your team's organisation, or create a new one."
      footer={
        <p>
          Signed in as <span className="font-medium text-neutral-900">{user.email}</span>.{' '}
          <button
            type="button"
            onClick={() => signOut()}
            className="font-semibold text-neutral-900 underline-offset-4 hover:underline"
          >
            Sign out
          </button>
        </p>
      }
    >
      <Tabs idPrefix={TABS_ID} label="Organisation setup" items={MODES} value={mode} onChange={setMode} />

      {/* key resets each form (values, errors) when switching tabs */}
      <TabPanel key={mode} idPrefix={TABS_ID} id={mode} className="mt-6">
        {mode === 'join' ? <JoinOrganisationForm /> : <CreateOrganisationForm />}
      </TabPanel>
    </AuthLayout>
  )
}
