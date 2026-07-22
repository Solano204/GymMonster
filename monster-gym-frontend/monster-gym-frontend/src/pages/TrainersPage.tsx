import { useState } from "react";
import { useTrainers, useTrainerSpecialties } from "@/hooks/useApi";
import { PageHeader } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";

export function TrainersPage() {
  const [page, setPage] = useState(0);
  const [expanded, setExpanded] = useState<string | null>(null);
  const { data, isLoading, error } = useTrainers({ page, size: 10 });

  return (
    <div>
      <PageHeader eyebrow="Staff" title="Trainers" description="Coaching roster, one page of ten at a time." />
      {isLoading && <LoadingBlock label="Loading trainers" />}
      {error && <ErrorBlock message={error.message} />}
      {data && data.length === 0 && page === 0 && <EmptyBlock>No trainers on record.</EmptyBlock>}

      {data && data.length > 0 && (
        <div className="space-y-2">
          {data.map((t) => (
            <TrainerRow
              key={t.username}
              trainer={t}
              expanded={expanded === t.username}
              onToggle={() => setExpanded(expanded === t.username ? null : t.username)}
            />
          ))}
        </div>
      )}

      <div className="mt-6 flex items-center gap-3 font-mono text-xs uppercase tracking-wider">
        <button
          className="focus-ring rounded border border-[var(--color-border)] px-3 py-1.5 disabled:opacity-30"
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page === 0}
        >
          ← Prev
        </button>
        <span className="text-[var(--color-muted)]">Page {page + 1}</span>
        <button
          className="focus-ring rounded border border-[var(--color-border)] px-3 py-1.5 disabled:opacity-30"
          onClick={() => setPage((p) => p + 1)}
          disabled={!data || data.length < 10}
        >
          Next →
        </button>
      </div>
    </div>
  );
}

function TrainerRow({
  trainer,
  expanded,
  onToggle,
}: {
  trainer: { username: string; email: string; name: string; secondName: string; lastNameP: string; lastNameM: string; age: string };
  expanded: boolean;
  onToggle: () => void;
}) {
  const { data: specialties, isLoading } = useTrainerSpecialties(expanded ? trainer.username : undefined);

  return (
    <div className="rounded-md border border-[var(--color-border)] bg-[var(--color-surface)]">
      <button
        onClick={onToggle}
        className="focus-ring flex w-full items-center justify-between px-4 py-3 text-left"
      >
        <div>
          <p className="font-medium">
            {trainer.name} {trainer.secondName} {trainer.lastNameP} {trainer.lastNameM}
          </p>
          <p className="font-mono text-xs text-[var(--color-muted)]">
            @{trainer.username} · {trainer.email} · age {trainer.age}
          </p>
        </div>
        <span className="font-mono text-xs text-[var(--color-hazard)]">{expanded ? "−" : "+"} specialties</span>
      </button>
      {expanded && (
        <div className="border-t border-[var(--color-border)] px-4 py-3">
          {isLoading && <LoadingBlock label="Loading specialties" />}
          {specialties && specialties.length === 0 && (
            <p className="text-sm text-[var(--color-muted)]">No specialties assigned.</p>
          )}
          {specialties && specialties.length > 0 && (
            <ul className="flex flex-wrap gap-2">
              {specialties.map((s) => (
                <li
                  key={s.name}
                  className="rounded-full border border-[var(--color-hazard)]/40 bg-[var(--color-hazard)]/10 px-3 py-1 text-xs"
                >
                  {s.name}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}
