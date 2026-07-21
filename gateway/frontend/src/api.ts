import { loadBaseUrl, getAccessToken, getRefreshToken, getUsername } from "./config.js";
import type { Token, HttpMethod } from "./types.js";

export interface ApiCallLog {
  method: string;
  url: string;
  requestHeaders: Record<string, string>;
  requestBody: unknown;
  responseStatus: number | null;
  responseBody: unknown;
}

export type ApiLogListener = (log: ApiCallLog) => void;

let logListener: ApiLogListener | null = null;

export function onApiCall(listener: ApiLogListener): void {
  logListener = listener;
}

function stripTrailingSlash(url: string): string {
  return url.endsWith("/") ? url.slice(0, -1) : url;
}

async function rawFetch(
  method: string,
  path: string,
  headers: Record<string, string>,
  body?: string
): Promise<{ status: number; parsed: unknown }> {
  const url = `${stripTrailingSlash(loadBaseUrl())}${path}`;

  let response: Response;
  try {
    response = await fetch(url, { method, headers, body });
  } catch (error) {
    logListener?.({
      method,
      url,
      requestHeaders: headers,
      requestBody: body ?? null,
      responseStatus: null,
      responseBody: { networkError: error instanceof Error ? error.message : String(error) },
    });
    throw error;
  }

  const text = await response.text();
  let parsedBody: unknown = null;
  if (text) {
    try {
      parsedBody = JSON.parse(text);
    } catch {
      parsedBody = text;
    }
  }

  logListener?.({
    method,
    url,
    requestHeaders: headers,
    requestBody: body ?? null,
    responseStatus: response.status,
    responseBody: parsedBody,
  });

  return { status: response.status, parsed: parsedBody };
}

export async function login(username: string, password: string): Promise<Token> {
  const { status, parsed } = await rawFetch("POST", "/GymMonster/auth/login", { username, password });
  if (status < 200 || status >= 300) {
    throw new Error(typeof parsed === "string" ? parsed : `HTTP ${status} on login`);
  }
  return parsed as Token;
}

export async function logout(): Promise<void> {
  const headers: Record<string, string> = { refresh_token: getRefreshToken(), username: getUsername() };
  const { status, parsed } = await rawFetch("POST", "/GymMonster/auth/logout", headers);
  if (status < 200 || status >= 300) {
    throw new Error(typeof parsed === "string" ? parsed : `HTTP ${status} on logout`);
  }
}

export async function callEndpoint(method: HttpMethod, resolvedPath: string, bodyText: string): Promise<unknown> {
  const headers: Record<string, string> = {};
  const token = getAccessToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  let body: string | undefined;
  if (bodyText.trim()) {
    JSON.parse(bodyText); // throws with a clear message if malformed - caught by the caller
    headers["Content-Type"] = "application/json";
    body = bodyText;
  }

  const { status, parsed } = await rawFetch(method, resolvedPath, headers, body);
  if (status < 200 || status >= 300) {
    const message = parsed && typeof parsed === "object" && "message" in parsed ? String((parsed as { message: unknown }).message) : null;
    throw new Error(message ?? `HTTP ${status} calling ${resolvedPath}`);
  }
  return parsed;
}
