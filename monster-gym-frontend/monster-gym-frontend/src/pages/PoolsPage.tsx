import { usePools } from "@/hooks/useApi";
import { PageHeader } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";
import { DataTable, type Column } from "@/components/DataTable";
import type { Pool } from "@/types/api";

const columns: Column<Pool>[] = [
  { header: "Name", cell: (p) => <span className="font-medium">{p.name}</span> },
  { header: "Description", cell: (p) => p.description },
];

export function PoolsPage() {
  const { data, isLoading, error } = usePools();

  return (
    <div>
      <PageHeader eyebrow="Facilities" title="Pools" description="Swim schedules and maintenance windows." />
      {isLoading && <LoadingBlock label="Loading pools" />}
      {error && <ErrorBlock message={error.message} />}
      {data && data.length === 0 && <EmptyBlock>No pools on record.</EmptyBlock>}
      {data && data.length > 0 && <DataTable columns={columns} rows={data} keyFor={(p) => p.id} />}
    </div>
  );
}
