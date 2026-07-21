import { test, beforeEach } from "node:test";
import assert from "node:assert/strict";
import { login, logout, callEndpoint, onApiCall, type ApiCallLog } from "./api.js";
import { saveBaseUrl, setAccessToken, setSession, clearSession, getUsername } from "./config.js";
import { installMemoryStorage } from "./testSupport.js";

installMemoryStorage();

type FetchCall = { url: string; init: { method: string; headers: Record<string, string>; body?: string } };
type Responder = (call: FetchCall) => { status: number; body: string };

const calls: FetchCall[] = [];
let responder: Responder = () => ({ status: 200, body: "" });

function stubFetch(handler: Responder): void {
  responder = handler;
}

(globalThis as unknown as { fetch: typeof fetch }).fetch = (async (url: string, init: FetchCall["init"]) => {
  calls.push({ url, init });
  const { status, body } = responder({ url, init });
  return { status, text: async () => body } as unknown as Response;
}) as unknown as typeof fetch;

beforeEach(() => {
  calls.length = 0;
  saveBaseUrl("http://gateway.test");
  clearSession();
  onApiCall(() => {});
  responder = () => ({ status: 200, body: "" });
});

test("login POSTs username/password as headers with no body, and returns the parsed token", async () => {
  stubFetch(() => ({
    status: 200,
    body: JSON.stringify({
      username: "jdoe",
      accessToken: "a",
      refreshToken: "r",
      expiresAt: "2026-01-01T00:00:00Z",
      refreshExpiresAt: "2026-01-02T00:00:00Z",
    }),
  }));

  const token = await login("jdoe", "s3cret");

  assert.equal(calls.length, 1);
  assert.equal(calls[0].url, "http://gateway.test/GymMonster/auth/login");
  assert.equal(calls[0].init.method, "POST");
  assert.deepEqual(calls[0].init.headers, { username: "jdoe", password: "s3cret" });
  assert.equal(calls[0].init.body, undefined);
  assert.equal(token.username, "jdoe");
  assert.equal(token.accessToken, "a");
});

test("login throws using the response body as the message on a non-2xx status", async () => {
  stubFetch(() => ({ status: 401, body: JSON.stringify("Invalid credentials") }));

  await assert.rejects(() => login("jdoe", "wrong"), /Invalid credentials/);
});

test("login throws a generic HTTP-status message when the error body isn't a plain string", async () => {
  stubFetch(() => ({ status: 500, body: JSON.stringify({ error: "boom" }) }));

  await assert.rejects(() => login("jdoe", "x"), /HTTP 500 on login/);
});

test("logout sends the in-memory refresh token and username as headers", async () => {
  setSession({ accessToken: "a", refreshToken: "r-tok", username: "jdoe" });
  stubFetch(() => ({ status: 200, body: "" }));

  await logout();

  assert.equal(calls[0].url, "http://gateway.test/GymMonster/auth/logout");
  assert.deepEqual(calls[0].init.headers, { refresh_token: "r-tok", username: "jdoe" });
});

test("logout throws using the response body as the message on a non-2xx status", async () => {
  stubFetch(() => ({ status: 400, body: JSON.stringify("bad refresh token") }));

  await assert.rejects(() => logout(), /bad refresh token/);
});

test("callEndpoint attaches an Authorization: Bearer header only once an access token is set", async () => {
  stubFetch(() => ({ status: 200, body: "null" }));

  await callEndpoint("GET", "/api/page/allMemberships", "");
  assert.equal(calls[0].init.headers.Authorization, undefined);

  setAccessToken("tok-123");
  await callEndpoint("GET", "/api/page/allMemberships", "");
  assert.equal(calls[1].init.headers.Authorization, "Bearer tok-123");
});

test("callEndpoint only sends a body and Content-Type header when bodyText is non-blank", async () => {
  stubFetch(() => ({ status: 200, body: "{}" }));

  await callEndpoint("POST", "/api/page/registerClient", "   ");
  assert.equal(calls[0].init.body, undefined);
  assert.equal(calls[0].init.headers["Content-Type"], undefined);

  await callEndpoint("POST", "/api/page/registerClient", '{"a":1}');
  assert.equal(calls[1].init.body, '{"a":1}');
  assert.equal(calls[1].init.headers["Content-Type"], "application/json");
});

test("callEndpoint throws (from JSON.parse) and never calls fetch when bodyText is malformed JSON", async () => {
  await assert.rejects(() => callEndpoint("POST", "/api/page/registerClient", "{not json"));

  assert.equal(calls.length, 0);
});

test("callEndpoint returns the parsed response body on a 2xx status", async () => {
  stubFetch(() => ({ status: 200, body: JSON.stringify({ id: 1 }) }));

  const result = await callEndpoint("GET", "/api/page/clients/jdoe/allInformation", "");

  assert.deepEqual(result, { id: 1 });
});

test("callEndpoint throws the response's message field on a non-2xx status", async () => {
  stubFetch(() => ({ status: 404, body: JSON.stringify({ message: "not found" }) }));

  await assert.rejects(
    () => callEndpoint("GET", "/api/page/clients/ghost/allInformation", ""),
    /not found/,
  );
});

test("callEndpoint falls back to a generic HTTP-status message when the error body has no message field", async () => {
  stubFetch(() => ({ status: 500, body: "" }));

  await assert.rejects(
    () => callEndpoint("GET", "/api/page/allMemberships", ""),
    /HTTP 500 calling \/api\/page\/allMemberships/,
  );
});

test("onApiCall receives a log entry with status and parsed request/response bodies for every call", async () => {
  const logs: ApiCallLog[] = [];
  onApiCall((log) => logs.push(log));
  stubFetch(() => ({ status: 200, body: JSON.stringify({ ok: true }) }));

  await callEndpoint("GET", "/api/page/allMemberships", "");

  assert.equal(logs.length, 1);
  assert.equal(logs[0].method, "GET");
  assert.equal(logs[0].url, "http://gateway.test/api/page/allMemberships");
  assert.equal(logs[0].responseStatus, 200);
  assert.deepEqual(logs[0].responseBody, { ok: true });
});

test("onApiCall receives a networkError log entry and the error is rethrown when fetch itself fails", async () => {
  const logs: ApiCallLog[] = [];
  onApiCall((log) => logs.push(log));
  stubFetch(() => {
    throw new Error("connection refused");
  });

  await assert.rejects(
    () => callEndpoint("GET", "/api/page/allMemberships", ""),
    /connection refused/,
  );

  assert.equal(logs.length, 1);
  assert.equal(logs[0].responseStatus, null);
  assert.deepEqual(logs[0].responseBody, { networkError: "connection refused" });
});

test("logout reads username from getUsername(), which reflects the session set via setSession", async () => {
  setSession({ accessToken: "a", refreshToken: "r", username: "coach99" });
  stubFetch(() => ({ status: 200, body: "" }));

  await logout();

  assert.equal(getUsername(), "coach99");
  assert.equal(calls[0].init.headers.username, "coach99");
});
