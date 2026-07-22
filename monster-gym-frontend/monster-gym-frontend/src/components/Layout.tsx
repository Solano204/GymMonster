import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { authApi } from "@/api/auth";
import { authStore, useAuth } from "@/lib/auth";

const NAV = [
  { to: "/", label: "Overview", end: true },
  { to: "/clients", label: "Members" },
  { to: "/trainers", label: "Trainers" },
  { to: "/workclasses", label: "Classes" },
  { to: "/memberships", label: "Memberships" },
  { to: "/pools", label: "Pools" },
  { to: "/specialties", label: "Specialties" },
  { to: "/promotions", label: "Promotions" },
];

export function Layout() {
  const user = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    if (user) {
      try {
        await authApi.logout(user.username, user.refreshToken);
      } catch {
        // Best-effort: still clear the local session even if the backend call fails.
      }
    }
    authStore.clear();
    navigate("/login", { replace: true });
  }

  return (
    <div className="flex min-h-screen">
      <aside className="flex w-60 shrink-0 flex-col border-r border-[var(--color-border)] bg-[var(--color-surface)]">
        <div className="border-b border-[var(--color-border)] px-5 py-6">
          <p className="font-[var(--font-display)] text-2xl leading-none tracking-wide">
            MONSTER<span className="text-[var(--color-hazard)]">GYM</span>
          </p>
          <p className="mt-1 font-mono text-[10px] uppercase tracking-[0.2em] text-[var(--color-muted)]">
            Ops Console
          </p>
        </div>
        <nav className="flex-1 space-y-1 px-3 py-4">
          {NAV.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                `focus-ring block rounded px-3 py-2 font-mono text-xs uppercase tracking-wider transition-colors ${
                  isActive
                    ? "bg-[var(--color-hazard)] text-[var(--color-ink)]"
                    : "text-[var(--color-muted)] hover:bg-[var(--color-surface-raised)] hover:text-[var(--color-paper)]"
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="hazard-rule" />
      </aside>

      <div className="flex-1">
        <header className="flex items-center justify-between border-b border-[var(--color-border)] bg-[var(--color-surface)]/60 px-8 py-4 backdrop-blur">
          <p className="text-sm text-[var(--color-muted)]">
            Facility management &amp; membership operations
          </p>
          <div className="flex items-center gap-4 font-mono text-xs text-[var(--color-muted)]">
            <span className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-[var(--color-mint)]" />
              {user?.username}
            </span>
            <button
              type="button"
              onClick={handleLogout}
              className="focus-ring rounded border border-[var(--color-border)] px-2 py-1 uppercase tracking-wider hover:bg-[var(--color-surface-raised)]"
            >
              Logout
            </button>
          </div>
        </header>
        <main className="mx-auto max-w-6xl px-8 py-10">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
