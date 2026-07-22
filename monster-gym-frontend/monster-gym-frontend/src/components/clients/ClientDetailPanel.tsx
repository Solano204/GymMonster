import { useState } from "react";
import {
  useClientInfo,
  useClientClasses,
  useMemberships,
  useUpdateClientInfo,
  useUpdateClientMembership,
  useUpdateClientTrainer,
  useRemoveClientTrainer,
  useRemoveClientMembership,
  useDeleteClientAccount,
} from "@/hooks/useApi";
import { Card } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";
import type { ClientDetailUpdateInput } from "@/types/api";

export function ClientDetailPanel({ username }: { username: string }) {
  const { data: info, isLoading, error } = useClientInfo(username);
  const { data: classes } = useClientClasses(username);

  if (isLoading) return <LoadingBlock label="Looking up member" />;
  if (error) return <ErrorBlock message={`No member found for "${username}". ${error.message}`} />;
  if (!info) return <EmptyBlock>No member found.</EmptyBlock>;

  return (
    <div className="space-y-4">
      <Card tab={`#${info.id}`}>
        <h3 className="font-[var(--font-display)] text-3xl tracking-wide">
          {info.name} {info.secondname} {info.lastnamep} {info.lastnamem}
        </h3>
        <p className="mt-1 font-mono text-sm text-[var(--color-muted)]">
          @{info.username} · {info.email}
        </p>
        <dl className="mt-4 grid grid-cols-3 gap-3 font-mono text-xs">
          <Stat label="Age" value={info.age} />
          <Stat label="Height" value={info.height} />
          <Stat label="Weight" value={info.weight} />
        </dl>
      </Card>

      <Card tab="Classes">
        {!classes || classes.length === 0 ? (
          <p className="text-sm text-[var(--color-muted)]">Not enrolled in any classes.</p>
        ) : (
          <ul className="space-y-1.5 text-sm">
            {classes.map((c, i) => (
              <li key={i} className="flex justify-between border-b border-[var(--color-border)] pb-1.5 last:border-none">
                <span>{c.name}</span>
                <span className="font-mono text-xs text-[var(--color-muted)]">{c.duration}</span>
              </li>
            ))}
          </ul>
        )}
      </Card>

      <EditInfoCard username={username} info={info} />
      <MembershipCard username={username} currentMembership={info.membershiptype} />
      <TrainerCard username={username} currentTrainer={info.trainername} />
      <DangerZoneCard username={username} />
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-[var(--color-muted)] uppercase tracking-wider">{label}</dt>
      <dd className="mt-0.5 text-[var(--color-paper)]">{value}</dd>
    </div>
  );
}

function EditInfoCard({
  username,
  info,
}: {
  username: string;
  info: { name: string; secondname: string; lastnamep: string; lastnamem: string; age: string; height: string; weight: string };
}) {
  const [form, setForm] = useState<ClientDetailUpdateInput>({
    name: info.name,
    secondName: info.secondname,
    lastNameM: info.lastnamem,
    lastNameP: info.lastnamep,
    age: info.age,
    height: info.height,
    weight: info.weight,
  });
  const mutation = useUpdateClientInfo(username);

  // Same reasoning as ClientRegisterForm: age/height/weight stay `string`
  // end-to-end (backend DTO expects strings) but must only ever contain
  // digits - filtered live rather than validated after submit. Age has no
  // fractional part; height/weight allow one decimal point.
  const NUMERIC_FIELDS = new Set<keyof ClientDetailUpdateInput>(["age", "height", "weight"]);
  const handleChange = (key: keyof ClientDetailUpdateInput) => (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!NUMERIC_FIELDS.has(key)) {
      setForm((f) => ({ ...f, [key]: e.target.value }));
      return;
    }
    const pattern = key === "age" ? /\D/g : /[^\d.]/g;
    let value = e.target.value.replace(pattern, "");
    if (key !== "age") {
      const firstDot = value.indexOf(".");
      if (firstDot !== -1) {
        value = value.slice(0, firstDot + 1) + value.slice(firstDot + 1).replace(/\./g, "");
      }
    }
    setForm((f) => ({ ...f, [key]: value }));
  };

  return (
    <Card tab="Edit profile">
      <form
        onSubmit={(e) => {
          e.preventDefault();
          mutation.mutate(form);
        }}
        className="grid grid-cols-2 gap-3"
      >
        {(Object.keys(form) as (keyof ClientDetailUpdateInput)[]).map((key) => (
          <label key={key} className="block">
            <span className="font-mono text-[11px] uppercase tracking-wider text-[var(--color-muted)]">{key}</span>
            <input
              value={form[key]}
              inputMode={NUMERIC_FIELDS.has(key) ? "decimal" : undefined}
              onChange={handleChange(key)}
              className="focus-ring mt-1 w-full rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
            />
          </label>
        ))}
        <div className="col-span-2 mt-1 flex items-center gap-3">
          <button
            type="submit"
            disabled={mutation.isPending}
            className="focus-ring rounded bg-[var(--color-hazard)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-ink)] disabled:opacity-50"
          >
            {mutation.isPending ? "Saving…" : "Save changes"}
          </button>
          {mutation.isSuccess && <span className="font-mono text-xs text-[var(--color-mint)]">Saved</span>}
        </div>
        {mutation.error && <div className="col-span-2"><ErrorBlock message={mutation.error.message} /></div>}
      </form>
    </Card>
  );
}

function MembershipCard({ username, currentMembership }: { username: string; currentMembership: string }) {
  const { data: memberships } = useMemberships();
  const [selected, setSelected] = useState(currentMembership);
  const update = useUpdateClientMembership(username);
  const remove = useRemoveClientMembership(username);

  return (
    <Card tab="Membership">
      <p className="text-sm text-[var(--color-muted)]">
        Current tier: <span className="text-[var(--color-paper)]">{currentMembership || "—"}</span>
      </p>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        <select
          value={selected}
          onChange={(e) => setSelected(e.target.value)}
          className="focus-ring rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
        >
          {memberships?.map((m) => (
            <option key={m.membershipType} value={m.membershipType}>
              {m.membershipType}
            </option>
          ))}
        </select>
        <button
          onClick={() => update.mutate(selected)}
          disabled={update.isPending || !selected}
          className="focus-ring rounded border border-[var(--color-hazard)] px-3 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-hazard)] disabled:opacity-50"
        >
          Switch
        </button>
        <button
          onClick={() => remove.mutate(currentMembership)}
          disabled={remove.isPending || !currentMembership}
          className="focus-ring rounded border border-[var(--color-iron)] px-3 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-iron)] disabled:opacity-50"
        >
          Remove current
        </button>
      </div>
      {(update.error || remove.error) && <div className="mt-2"><ErrorBlock message={(update.error ?? remove.error)!.message} /></div>}
    </Card>
  );
}

function TrainerCard({ username, currentTrainer }: { username: string; currentTrainer: string | null }) {
  const [value, setValue] = useState("");
  const assign = useUpdateClientTrainer(username);
  const remove = useRemoveClientTrainer(username);

  return (
    <Card tab="Trainer">
      <p className="text-sm text-[var(--color-muted)]">
        Current trainer: <span className="text-[var(--color-paper)]">{currentTrainer || "Unassigned"}</span>
      </p>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        <input
          value={value}
          onChange={(e) => setValue(e.target.value)}
          placeholder="trainer username"
          className="focus-ring rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
        />
        <button
          onClick={() => assign.mutate(value)}
          disabled={assign.isPending || !value}
          className="focus-ring rounded border border-[var(--color-hazard)] px-3 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-hazard)] disabled:opacity-50"
        >
          Assign
        </button>
        <button
          onClick={() => currentTrainer && remove.mutate(currentTrainer)}
          disabled={remove.isPending || !currentTrainer}
          className="focus-ring rounded border border-[var(--color-iron)] px-3 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-iron)] disabled:opacity-50"
        >
          Remove
        </button>
      </div>
      {(assign.error || remove.error) && <div className="mt-2"><ErrorBlock message={(assign.error ?? remove.error)!.message} /></div>}
    </Card>
  );
}

function DangerZoneCard({ username }: { username: string }) {
  const [password, setPassword] = useState("");
  const del = useDeleteClientAccount();

  return (
    <Card tab="Danger zone" className="border-[var(--color-iron)]/50">
      <p className="text-sm text-[var(--color-muted)]">Deleting an account is permanent and cannot be undone.</p>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="confirm password"
          className="focus-ring rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
        />
        <button
          onClick={() => password && del.mutate({ username, password })}
          disabled={del.isPending || !password}
          className="focus-ring rounded bg-[var(--color-iron)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-paper)] disabled:opacity-50"
        >
          Delete account
        </button>
      </div>
      {del.error && <div className="mt-2"><ErrorBlock message={del.error.message} /></div>}
      {del.isSuccess && <p className="mt-2 font-mono text-xs text-[var(--color-mint)]">Account deleted.</p>}
    </Card>
  );
}
