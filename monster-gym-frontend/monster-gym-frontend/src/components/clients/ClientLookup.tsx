import { useState, type FormEvent } from "react";

export function ClientLookup({ onFound }: { onFound: (username: string) => void }) {
  const [value, setValue] = useState("");

  const submit = (e: FormEvent) => {
    e.preventDefault();
    if (value.trim()) onFound(value.trim());
  };

  return (
    <form onSubmit={submit} className="space-y-3">
      <label className="block">
        <span className="font-mono text-xs uppercase tracking-wider text-[var(--color-muted)]">Username</span>
        <input
          value={value}
          onChange={(e) => setValue(e.target.value)}
          placeholder="e.g. jsmith"
          className="focus-ring mt-1 w-full rounded border border-[var(--color-border)] bg-[var(--color-surface-raised)] px-3 py-2 text-sm"
        />
      </label>
      <button
        type="submit"
        className="focus-ring w-full rounded border border-[var(--color-hazard)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-hazard)] hover:bg-[var(--color-hazard)]/10"
      >
        Look up member
      </button>
    </form>
  );
}
