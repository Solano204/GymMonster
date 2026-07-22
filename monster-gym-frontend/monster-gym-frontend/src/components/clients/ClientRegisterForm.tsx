import { useState, type FormEvent } from "react";
import { useMemberships, useRegisterClient } from "@/hooks/useApi";
import type { ClientRegisterInput } from "@/types/api";
import { ErrorBlock } from "@/components/StateViews";

const EMPTY: ClientRegisterInput = {
  username: "",
  password: "",
  email: "",
  trainername: null,
  name: "",
  secondname: "",
  lastnamep: "",
  lastnamem: "",
  age: "",
  height: "",
  weight: "",
  membershiptype: "",
};

const FIELD_LABELS: Record<string, string> = {
  username: "Username",
  password: "Password",
  email: "Email",
  name: "First name",
  secondname: "Second name",
  lastnamep: "Paternal last name",
  lastnamem: "Maternal last name",
  age: "Age",
  height: "Height (cm)",
  weight: "Weight (kg)",
};

export function ClientRegisterForm({ onRegistered }: { onRegistered: (username: string) => void }) {
  const [form, setForm] = useState<ClientRegisterInput>(EMPTY);
  const { data: memberships } = useMemberships();
  const mutation = useRegisterClient();

  const set = (key: keyof ClientRegisterInput) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }));

  // age/height/weight are typed as `string` end-to-end (the backend DTO
  // expects strings here too, see types/api.ts) but must still only ever
  // contain digits - filtered live as the user types rather than validated
  // after the fact, same as the phone-number fields elsewhere in this pass.
  // Age has no fractional part; height/weight allow one decimal point.
  const NUMERIC_FIELDS = new Set(["age", "height", "weight"]);
  const setNumeric = (key: "age" | "height" | "weight") => (e: React.ChangeEvent<HTMLInputElement>) => {
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

  const submit = (e: FormEvent) => {
    e.preventDefault();
    mutation.mutate(form, { onSuccess: (client) => onRegistered(client.username) });
  };

  return (
    <form onSubmit={submit} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        {(
          [
            "username",
            "password",
            "email",
            "name",
            "secondname",
            "lastnamep",
            "lastnamem",
            "age",
            "height",
            "weight",
          ] as const
        ).map((key) => (
          <label key={key} className="block">
            <span className="font-mono text-[11px] uppercase tracking-wider text-[var(--color-muted)]">
              {FIELD_LABELS[key]}
            </span>
            <input
              required
              type={key === "password" ? "password" : "text"}
              inputMode={NUMERIC_FIELDS.has(key) ? "decimal" : undefined}
              value={form[key] ?? ""}
              onChange={NUMERIC_FIELDS.has(key) ? setNumeric(key as "age" | "height" | "weight") : set(key)}
              className="focus-ring mt-1 w-full rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
            />
          </label>
        ))}
      </div>

      <label className="block">
        <span className="font-mono text-[11px] uppercase tracking-wider text-[var(--color-muted)]">
          Membership
        </span>
        <select
          required
          value={form.membershiptype}
          onChange={(e) => setForm((f) => ({ ...f, membershiptype: e.target.value }))}
          className="focus-ring mt-1 w-full rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
        >
          <option value="" disabled>
            Select a tier…
          </option>
          {memberships?.map((m) => (
            <option key={m.membershipType} value={m.membershipType}>
              {m.membershipType}
            </option>
          ))}
        </select>
      </label>

      {mutation.error && <ErrorBlock message={mutation.error.message} />}

      <button
        type="submit"
        disabled={mutation.isPending}
        className="focus-ring w-full rounded bg-[var(--color-hazard)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-ink)] disabled:opacity-50"
      >
        {mutation.isPending ? "Registering…" : "Register member"}
      </button>
    </form>
  );
}
