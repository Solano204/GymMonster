import { useState, type FormEvent } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import { authApi } from "@/api/auth";
import { authStore, useAuth } from "@/lib/auth";
import { ApiError } from "@/lib/http";
import { ErrorBlock } from "@/components/StateViews";

export function LoginPage() {
  const user = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  if (user) {
    const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? "/";
    return <Navigate to={from} replace />;
  }

  async function submit(e: FormEvent) {
    e.preventDefault();
    setPending(true);
    setError(null);
    try {
      const token = await authApi.login(username, password);
      authStore.setSession({
        username: token.username,
        accessToken: token.accessToken,
        refreshToken: token.refreshToken,
      });
      const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? "/";
      navigate(from, { replace: true });
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Login failed.");
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[var(--color-surface)] px-4">
      <div className="w-full max-w-sm rounded-md border border-[var(--color-border)] bg-[var(--color-surface-raised)] p-6">
        <p className="font-[var(--font-display)] text-3xl leading-none tracking-wide">
          MONSTER<span className="text-[var(--color-hazard)]">GYM</span>
        </p>
        <p className="mt-1 font-mono text-[10px] uppercase tracking-[0.2em] text-[var(--color-muted)]">
          Ops Console
        </p>
        <div className="hazard-rule my-4 w-16" />

        <form onSubmit={submit} className="space-y-3">
          <label className="block">
            <span className="font-mono text-[11px] uppercase tracking-wider text-[var(--color-muted)]">
              Username
            </span>
            <input
              required
              autoFocus
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="focus-ring mt-1 w-full rounded border border-[var(--color-border)] bg-[var(--color-surface)] px-3 py-2 text-sm"
            />
          </label>
          <label className="block">
            <span className="font-mono text-[11px] uppercase tracking-wider text-[var(--color-muted)]">
              Password
            </span>
            <input
              required
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="focus-ring mt-1 w-full rounded border border-[var(--color-border)] bg-[var(--color-surface)] px-3 py-2 text-sm"
            />
          </label>

          {error && <ErrorBlock message={error} />}

          <button
            type="submit"
            disabled={pending}
            className="focus-ring w-full rounded bg-[var(--color-hazard)] px-4 py-2 font-mono text-xs uppercase tracking-wider text-[var(--color-ink)] disabled:opacity-50"
          >
            {pending ? "Signing in…" : "Sign in"}
          </button>
        </form>
      </div>
    </div>
  );
}
