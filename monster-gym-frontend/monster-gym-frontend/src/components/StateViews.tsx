import type { ReactNode } from "react";

export function LoadingBlock({ label = "Loading" }: { label?: string }) {
  return (
    <div className="flex items-center gap-3 py-10 text-sm text-[var(--color-muted)]" role="status">
      <span className="h-2 w-2 animate-pulse rounded-full bg-[var(--color-hazard)]" />
      <span className="font-mono uppercase tracking-wider">{label}…</span>
    </div>
  );
}

export function ErrorBlock({ message }: { message: string }) {
  return (
    <div className="flex items-start gap-3 rounded-md border border-[var(--color-iron)]/50 bg-[var(--color-iron)]/10 px-4 py-3 text-sm text-[var(--color-paper)]">
      <span className="mt-0.5 h-2 w-2 shrink-0 rounded-full bg-[var(--color-iron)]" />
      <p>
        <span className="font-semibold text-[var(--color-iron)]">Couldn't load this. </span>
        {message}
      </p>
    </div>
  );
}

export function EmptyBlock({ children }: { children: ReactNode }) {
  return (
    <div className="rounded-md border border-dashed border-[var(--color-border)] px-4 py-10 text-center text-sm text-[var(--color-muted)]">
      {children}
    </div>
  );
}
