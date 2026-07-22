import { useState } from "react";
import {
  useWorkClasses,
  useWorkClassSchedules,
  useWorkClassClients,
  useWorkClassTrainers,
} from "@/hooks/useApi";
import { PageHeader, Card } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";

type Tab = "schedules" | "clients" | "trainers";

export function WorkClassesPage() {
  const { data, isLoading, error } = useWorkClasses();
  const [selected, setSelected] = useState<string | null>(null);

  return (
    <div>
      <PageHeader eyebrow="Programming" title="Classes" description="Every class on the floor and who's in it." />
      {isLoading && <LoadingBlock label="Loading classes" />}
      {error && <ErrorBlock message={error.message} />}
      {data && data.length === 0 && <EmptyBlock>No classes scheduled.</EmptyBlock>}

      {data && data.length > 0 && (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1.1fr_1fr]">
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            {data.map((wc) => (
              <button
                key={wc.name}
                onClick={() => setSelected(wc.name)}
                className={`focus-ring rounded-md border p-4 text-left transition-colors ${
                  selected === wc.name
                    ? "border-[var(--color-hazard)] bg-[var(--color-hazard)]/10"
                    : "border-[var(--color-border)] bg-[var(--color-surface)] hover:border-[var(--color-hazard)]/50"
                }`}
              >
                <h3 className="font-[var(--font-display)] text-xl tracking-wide">{wc.name}</h3>
                <p className="mt-1 text-xs text-[var(--color-muted)]">{wc.description}</p>
                <p className="mt-2 font-mono text-xs text-[var(--color-hazard)]">{wc.duration}</p>
              </button>
            ))}
          </div>

          <div>{selected ? <WorkClassDetail name={selected} /> : <EmptyBlock>Pick a class to see its schedule and roster.</EmptyBlock>}</div>
        </div>
      )}
    </div>
  );
}

function WorkClassDetail({ name }: { name: string }) {
  const [tab, setTab] = useState<Tab>("schedules");

  return (
    <Card>
      <h3 className="font-[var(--font-display)] text-2xl tracking-wide">{name}</h3>
      <div className="mt-3 flex gap-2 font-mono text-xs uppercase tracking-wider">
        {(["schedules", "clients", "trainers"] as Tab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`focus-ring rounded px-3 py-1.5 ${
              tab === t
                ? "bg-[var(--color-hazard)] text-[var(--color-ink)]"
                : "border border-[var(--color-border)] text-[var(--color-muted)]"
            }`}
          >
            {t}
          </button>
        ))}
      </div>
      <div className="mt-4">
        {tab === "schedules" && <SchedulesTab name={name} />}
        {tab === "clients" && <RosterTab name={name} kind="clients" />}
        {tab === "trainers" && <RosterTab name={name} kind="trainers" />}
      </div>
    </Card>
  );
}

function SchedulesTab({ name }: { name: string }) {
  const { data, isLoading, error } = useWorkClassSchedules(name);
  if (isLoading) return <LoadingBlock label="Loading schedule" />;
  if (error) return <ErrorBlock message={error.message} />;
  if (!data || data.length === 0) return <EmptyBlock>No sessions on the calendar.</EmptyBlock>;
  return (
    <ul className="space-y-2 font-mono text-sm">
      {data.map((s, i) => (
        <li key={i} className="flex justify-between border-b border-[var(--color-border)] pb-2 last:border-none">
          <span className="uppercase text-[var(--color-hazard)]">{s.day}</span>
          <span>
            {s.startTime}–{s.endTime}
          </span>
        </li>
      ))}
    </ul>
  );
}

function RosterTab({ name, kind }: { name: string; kind: "clients" | "trainers" }) {
  const clientsQuery = useWorkClassClients(kind === "clients" ? name : undefined);
  const trainersQuery = useWorkClassTrainers(kind === "trainers" ? name : undefined);
  const { data, isLoading, error } = kind === "clients" ? clientsQuery : trainersQuery;

  if (isLoading) return <LoadingBlock label={`Loading ${kind}`} />;
  if (error) return <ErrorBlock message={error.message} />;
  if (!data || data.length === 0) return <EmptyBlock>No {kind} enrolled yet.</EmptyBlock>;

  return (
    <ul className="space-y-2 text-sm">
      {data.map((p, i) => (
        <li key={i} className="flex items-center justify-between border-b border-[var(--color-border)] pb-2 last:border-none">
          <span>
            {p.name} {p.secondName} {p.lastNameP} {p.lastNameM}
          </span>
          <span className="font-mono text-xs text-[var(--color-muted)]">age {p.age}</span>
        </li>
      ))}
    </ul>
  );
}
