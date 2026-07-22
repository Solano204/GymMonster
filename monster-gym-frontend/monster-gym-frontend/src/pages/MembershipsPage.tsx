import { useMemberships } from "@/hooks/useApi";
import { PageHeader, Card } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";

export function MembershipsPage() {
  const { data, isLoading, error } = useMemberships();

  return (
    <div>
      <PageHeader
        eyebrow="Plans"
        title="Memberships"
        description="Tiers members can enroll in, and which facilities each one unlocks."
      />
      {isLoading && <LoadingBlock label="Loading memberships" />}
      {error && <ErrorBlock message={error.message} />}
      {data && data.length === 0 && <EmptyBlock>No membership tiers yet.</EmptyBlock>}
      {data && data.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {data.map((m) => (
            <Card key={m.membershipType}>
              <h3 className="font-[var(--font-display)] text-2xl tracking-wide">{m.membershipType}</h3>
              <p className="mt-2 text-sm text-[var(--color-muted)]">{m.description}</p>
              <ul className="mt-4 space-y-1.5 font-mono text-xs uppercase tracking-wider">
                <Perk label="Cardio floor" included={m.hasCardio} />
                <Perk label="Pool access" included={m.hasPool} />
                <Perk label="Food court" included={m.hasFoodCourt} />
              </ul>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

function Perk({ label, included }: { label: string; included: boolean }) {
  return (
    <li className="flex items-center gap-2">
      <span
        className={`h-1.5 w-1.5 rounded-full ${included ? "bg-[var(--color-mint)]" : "bg-[var(--color-border)]"}`}
      />
      <span className={included ? "text-[var(--color-paper)]" : "text-[var(--color-muted)] line-through"}>
        {label}
      </span>
    </li>
  );
}
