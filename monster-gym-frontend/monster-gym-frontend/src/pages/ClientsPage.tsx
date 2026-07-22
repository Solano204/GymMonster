import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import { PageHeader, Card } from "@/components/Card";
import { ClientLookup } from "@/components/clients/ClientLookup";
import { ClientRegisterForm } from "@/components/clients/ClientRegisterForm";
import { ClientDetailPanel } from "@/components/clients/ClientDetailPanel";

export function ClientsPage() {
  const [params, setParams] = useSearchParams();
  const [showRegister, setShowRegister] = useState(false);
  const username = params.get("u") ?? "";

  const setUsername = (u: string) => {
    if (u) setParams({ u });
    else setParams({});
  };

  return (
    <div>
      <PageHeader
        eyebrow="Roster"
        title="Members"
        description="Look up a member by username, or register a new one. The API doesn't expose a full member list — search is by username."
        action={
          <button
            onClick={() => setShowRegister((v) => !v)}
            className="focus-ring rounded bg-[var(--color-hazard)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-ink)] hover:brightness-95"
          >
            {showRegister ? "Close" : "+ New member"}
          </button>
        }
      />

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_1.4fr]">
        <div className="space-y-6">
          <Card tab="Search">
            <ClientLookup onFound={setUsername} />
          </Card>
          {showRegister && (
            <Card tab="Register">
              <ClientRegisterForm onRegistered={(u) => { setUsername(u); setShowRegister(false); }} />
            </Card>
          )}
        </div>

        <div>
          {username ? (
            <ClientDetailPanel username={username} />
          ) : (
            <Card>
              <p className="text-sm text-[var(--color-muted)]">
                Search for a member on the left, or register a new one to see their profile here.
              </p>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}
