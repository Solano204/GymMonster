import { Link } from "react-router-dom";
import { useMemberships, usePools, useSpecialties, useWorkClasses, useTrainers } from "@/hooks/useApi";
import { PageHeader, Card } from "@/components/Card";
import { LoadingBlock } from "@/components/StateViews";

export function DashboardPage() {
  const memberships = useMemberships();
  const pools = usePools();
  const specialties = useSpecialties();
  const workClasses = useWorkClasses();
  const trainers = useTrainers({ page: 0, size: 10 });

  const cards = [
    { label: "Membership tiers", query: memberships, to: "/memberships" },
    { label: "Pools", query: pools, to: "/pools" },
    { label: "Specialties", query: specialties, to: "/specialties" },
    { label: "Classes running", query: workClasses, to: "/workclasses" },
    { label: "Trainers (page 1)", query: trainers, to: "/trainers" },
  ];

  return (
    <div>
      <PageHeader
        eyebrow="Facility"
        title="Overview"
        description="A snapshot of Monster Gym's operations, pulled live from the API."
      />
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {cards.map((c) => (
          <Link key={c.label} to={c.to}>
            <Card className="transition-colors hover:border-[var(--color-hazard)]/60">
              <p className="font-mono text-xs uppercase tracking-wider text-[var(--color-muted)]">{c.label}</p>
              {c.query.isLoading ? (
                <LoadingBlock label="Loading" />
              ) : c.query.error ? (
                <p className="mt-2 text-sm text-[var(--color-iron)]">Unavailable</p>
              ) : (
                <p className="mt-1 font-[var(--font-display)] text-5xl tracking-wide text-[var(--color-hazard)]">
                  {c.query.data?.length ?? 0}
                </p>
              )}
            </Card>
          </Link>
        ))}
      </div>

      <div className="mt-8">
        <p className="font-mono text-xs uppercase tracking-wider text-[var(--color-muted)]">Quick actions</p>
        <div className="mt-2 flex flex-wrap gap-3">
          <Link
            to="/clients"
            className="focus-ring rounded bg-[var(--color-hazard)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-ink)]"
          >
            Register a member
          </Link>
          <Link
            to="/promotions"
            className="focus-ring rounded border border-[var(--color-border)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-muted)] hover:text-[var(--color-paper)]"
          >
            View today's promotions
          </Link>
        </div>
      </div>
    </div>
  );
}
