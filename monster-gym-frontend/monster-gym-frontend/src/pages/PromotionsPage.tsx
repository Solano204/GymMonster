import { useState } from "react";
import { useCurrentPromotions } from "@/hooks/useApi";
import { PageHeader, Card } from "@/components/Card";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";

function todayIso() {
  return new Date().toISOString().slice(0, 10);
}

export function PromotionsPage() {
  const [date, setDate] = useState(todayIso());
  const { data, isLoading, error } = useCurrentPromotions(date);

  return (
    <div>
      <PageHeader
        eyebrow="Offers"
        title="Promotions"
        description="Discounts active on a given date."
        action={
          <label className="flex flex-col gap-1 text-xs">
            <span className="font-mono uppercase tracking-wider text-[var(--color-muted)]">As of</span>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="focus-ring rounded border border-[var(--color-border)] bg-[var(--color-surface)] px-3 py-1.5 font-mono text-sm"
            />
          </label>
        }
      />
      {isLoading && <LoadingBlock label="Loading promotions" />}
      {error && <ErrorBlock message={error.message} />}
      {data && data.length === 0 && <EmptyBlock>No promotions active on this date.</EmptyBlock>}
      {data && data.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {data.map((promo, i) => (
            <Card key={i} tab={promo.active ? "Live" : "Ended"}>
              <p className="font-[var(--font-display)] text-3xl tracking-wide text-[var(--color-hazard)]">
                {promo.percentageDiscount}% off
              </p>
              <p className="mt-2 text-sm">{promo.description}</p>
              <p className="mt-3 font-mono text-xs text-[var(--color-muted)]">
                {promo.startDate} → {promo.endDate} · {promo.duration}
              </p>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
