import type { ReactNode } from "react";

export function Card({
  children,
  tab,
  className = "",
}: {
  children: ReactNode;
  tab?: string;
  className?: string;
}) {
  return (
    <div
      className={`relative rounded-md border border-[var(--color-border)] bg-[var(--color-surface)] p-5 ${className}`}
    >
      {tab && (
        <span className="plate-tab absolute -top-px right-4 -translate-y-1/2 bg-[var(--color-hazard)] text-[var(--color-ink)]">
          {tab}
        </span>
      )}
      {children}
    </div>
  );
}

export function PageHeader({
  eyebrow,
  title,
  description,
  action,
}: {
  eyebrow: string;
  title: string;
  description?: string;
  action?: ReactNode;
}) {
  return (
    <header className="mb-6 flex flex-wrap items-end justify-between gap-4">
      <div>
        <p className="font-mono text-xs uppercase tracking-[0.2em] text-[var(--color-hazard)]">{eyebrow}</p>
        <h1 className="font-[var(--font-display)] text-4xl leading-none tracking-wide text-[var(--color-paper)]">
          {title}
        </h1>
        {description && <p className="mt-2 max-w-2xl text-sm text-[var(--color-muted)]">{description}</p>}
        <div className="hazard-rule mt-3 w-16" />
      </div>
      {action}
    </header>
  );
}
