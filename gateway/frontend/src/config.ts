// Single entry point: the gateway (server.port 8081 by default) proxies /api/admin/** to
// server-administrator and /api/page/** to web-page, and owns login/logout itself.
// Tokens are never persisted (not localStorage, not sessionStorage) - memory only, since
// they're real Keycloak-issued credentials.

const STORAGE_KEY = "gymmonster-panel:baseUrl";
const DEFAULT_BASE_URL = "http://localhost:8081";

let accessToken = "";
let refreshToken = "";
let username = "";

export function loadBaseUrl(): string {
  return localStorage.getItem(STORAGE_KEY) || DEFAULT_BASE_URL;
}

export function saveBaseUrl(baseUrl: string): void {
  localStorage.setItem(STORAGE_KEY, baseUrl);
}

export function setSession(token: { accessToken: string; refreshToken: string; username: string }): void {
  accessToken = token.accessToken;
  refreshToken = token.refreshToken;
  username = token.username;
}

export function clearSession(): void {
  accessToken = "";
  refreshToken = "";
  username = "";
}

export function setAccessToken(token: string): void {
  accessToken = token.trim();
}

export function getAccessToken(): string {
  return accessToken;
}

export function getRefreshToken(): string {
  return refreshToken;
}

export function getUsername(): string {
  return username;
}
