import type { ReactNode } from "react";

export interface Column<T> {
  header: string;
  cell: (row: T) => ReactNode;
  mono?: boolean;
}

export function DataTable<T>({
  columns,
  rows,
  keyFor,
}: {
  columns: Column<T>[];
  rows: T[];
  keyFor: (row: T) => string | number;
}) {
  return (
    <div className="overflow-hidden rounded-md border border-[var(--color-border)]">
      <table className="w-full text-left text-sm">
        <thead>
          <tr className="border-b border-[var(--color-border)] bg-[var(--color-surface-raised)]">
            {columns.map((col) => (
              <th
                key={col.header}
                className="px-4 py-3 font-mono text-[11px] uppercase tracking-wider text-[var(--color-muted)]"
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr
              key={keyFor(row)}
              className="border-b border-[var(--color-border)] last:border-none hover:bg-[var(--color-surface-raised)]/50"
            >
              {columns.map((col) => (
                <td
                  key={col.header}
                  className={`px-4 py-3 ${col.mono ? "font-mono text-[13px]" : ""}`}
                >
                  {col.cell(row)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
