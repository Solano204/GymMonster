import { useSyncExternalStore } from "react";

/**
 * Session is kept in memory only (module-level variable), never in
 * localStorage/sessionStorage - it holds a live Keycloak-issued bearer token.
 */
export interface AuthSession {
  username: string;
  accessToken: string;
  refreshToken: string;
}

type Listener = () => void;

let session: AuthSession | null = null;
const listeners = new Set<Listener>();

function emit() {
  listeners.forEach((listener) => listener());
}

export const authStore = {
  getToken: () => session?.accessToken ?? null,
  getUsername: () => session?.username ?? null,
  getSnapshot: () => session,
  subscribe: (listener: Listener) => {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
  setSession: (next: AuthSession) => {
    session = next;
    emit();
  },
  clear: () => {
    session = null;
    emit();
  },
};

export function useAuth() {
  return useSyncExternalStore(authStore.subscribe, authStore.getSnapshot);
}
