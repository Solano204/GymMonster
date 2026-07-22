import { useSpecialties } from "@/hooks/useApi";
import { PageHeader, Card } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";

export function SpecialtiesPage() {
  const { data, isLoading, error } = useSpecialties();

  return (
    <div>
      <PageHeader
        eyebrow="Coaching"
        title="Specialties"
        description="Disciplines trainers are certified to coach — strength, mobility, conditioning, and more."
      />
      {isLoading && <LoadingBlock label="Loading specialties" />}
      {error && <ErrorBlock message={error.message} />}
      {data && data.length === 0 && <EmptyBlock>No specialties on record.</EmptyBlock>}
      {data && data.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {data.map((s) => (
            <Card key={s.name}>
              <h3 className="font-[var(--font-display)] text-2xl tracking-wide">{s.name}</h3>
              <p className="mt-2 text-sm text-[var(--color-muted)]">{s.description}</p>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
