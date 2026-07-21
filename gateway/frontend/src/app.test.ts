import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { dirname, join } from "node:path";
import { JSDOM } from "jsdom";
import { installMemoryStorage } from "./testSupport.js";
import { loadBaseUrl, getUsername, getAccessToken } from "./config.js";
import { ENDPOINTS } from "./endpoints.js";

// app.ts wires up the whole page (settings form, session/login/logout, endpoint
// explorer, request/response inspector) as side effects at module-load time, reading
// real DOM elements from index.html via qs(). To exercise it as a whole we load the
// real index.html into jsdom, stub out localStorage/fetch, and dynamically import the
// compiled module once other globals are in place.
//
// The tests below deliberately share one page load and one module-level session/
// selected-endpoint state, same as a real user driving the page top to bottom - so
// they run in declaration order (Node's test runner default for a single file) and
// later tests (auth header attached, logout redaction) depend on earlier ones
// (login) having run first.

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

const htmlPath = join(dirname(fileURLToPath(import.meta.url)), "..", "index.html");
const dom = new JSDOM(readFileSync(htmlPath, "utf-8"), { url: "http://localhost/" });
const doc = dom.window.document;

(globalThis as unknown as { document: Document }).document = doc;
(globalThis as unknown as { FormData: typeof FormData }).FormData = dom.window.FormData as unknown as typeof FormData;

await import("./app.js");

function flush(ms = 0): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function findEndpointButton(pathSubstring: string): HTMLButtonElement {
  const buttons = Array.from(doc.querySelectorAll<HTMLButtonElement>("#endpointGroups button.endpoint-item"));
  const match = buttons.find((b) => b.textContent?.includes(pathSubstring));
  if (!match) throw new Error(`No endpoint button found containing: ${pathSubstring}`);
  return match;
}

function submit(form: HTMLFormElement): void {
  form.dispatchEvent(new dom.window.Event("submit", { bubbles: true, cancelable: true }));
}

test("renders the saved base URL into the settings input on load, defaulting to http://localhost:8081", () => {
  const baseUrlInput = doc.getElementById("baseUrl") as HTMLInputElement;
  assert.equal(baseUrlInput.value, "http://localhost:8081");
  assert.equal(baseUrlInput.value, loadBaseUrl());
});

test("before any endpoint is selected, the form panel shows a placeholder prompt", () => {
  const formContainer = doc.getElementById("endpointFormContainer") as HTMLElement;
  assert.equal(formContainer.textContent, "Elegi un endpoint de la lista.");
});

test("the endpoint explorer renders one group per distinct ENDPOINTS group, each labeled with its item count", () => {
  const groups = new Set(ENDPOINTS.map((e) => e.group));
  const details = doc.querySelectorAll("#endpointGroups details");
  assert.equal(details.length, groups.size);

  const membershipsGroupCount = ENDPOINTS.filter((e) => e.group === "Page - Membresias").length;
  const summaries = Array.from(doc.querySelectorAll("#endpointGroups summary")).map((s) => s.textContent);
  assert.ok(summaries.includes(`Page - Membresias (${membershipsGroupCount})`));
});

test("typing in the endpoint search filters the rendered groups down to matches", () => {
  const search = doc.getElementById("endpointSearch") as HTMLInputElement;

  search.value = "allmemberships";
  search.dispatchEvent(new dom.window.Event("input", { bubbles: true }));
  let details = doc.querySelectorAll("#endpointGroups details");
  assert.equal(details.length, 1);
  assert.equal(doc.querySelector("#endpointGroups summary")?.textContent, "Page - Membresias (1)");

  search.value = "no-such-endpoint-xyz";
  search.dispatchEvent(new dom.window.Event("input", { bubbles: true }));
  details = doc.querySelectorAll("#endpointGroups details");
  assert.equal(details.length, 0);

  // Reset for later tests, which look up endpoint buttons by text.
  search.value = "";
  search.dispatchEvent(new dom.window.Event("input", { bubbles: true }));
});

test("saving the settings form persists the trimmed URL and shows a message that clears after ~2s", (t) => {
  t.mock.timers.enable({ apis: ["setTimeout"] });
  const baseUrlInput = doc.getElementById("baseUrl") as HTMLInputElement;
  const saveBtn = doc.getElementById("saveConfigBtn") as HTMLButtonElement;
  const savedMsg = doc.getElementById("configSavedMsg") as HTMLElement;

  baseUrlInput.value = "  http://example.test:1234  ";
  saveBtn.click();

  assert.equal(loadBaseUrl(), "http://example.test:1234");
  assert.equal(savedMsg.textContent, "Configuracion guardada.");

  t.mock.timers.tick(2000);
  assert.equal(savedMsg.textContent, "");

  baseUrlInput.value = "http://localhost:8081";
  saveBtn.click();
  t.mock.timers.tick(2000);
});

test("selecting a no-param GET endpoint shows just a submit button, and submitting renders the response", async () => {
  findEndpointButton("/api/page/allMemberships").click();

  const formContainer = doc.getElementById("endpointFormContainer") as HTMLElement;
  assert.match(formContainer.textContent ?? "", /GET/);
  assert.equal(formContainer.querySelectorAll("input").length, 0);
  assert.equal(formContainer.querySelectorAll("textarea").length, 0);

  stubFetch(() => ({ status: 200, body: JSON.stringify([{ type: "Gold" }]) }));
  submit(doc.getElementById("callForm") as HTMLFormElement);
  await flush();

  const result = formContainer.querySelector('[data-result="call"]') as HTMLElement;
  assert.match(result.textContent ?? "", /Respuesta recibida\./);
  assert.equal(result.className, "result result--success");

  const lastRequest = JSON.parse((doc.getElementById("lastRequest") as HTMLElement).textContent ?? "{}");
  assert.equal(lastRequest.method, "GET");
  assert.match(lastRequest.url, /\/api\/page\/allMemberships$/);
});

test("selecting a POST endpoint shows a JSON body textarea, and its contents are sent as the request body", async () => {
  findEndpointButton("/api/page/registerClient").click();

  const formContainer = doc.getElementById("endpointFormContainer") as HTMLElement;
  const textarea = formContainer.querySelector("textarea") as HTMLTextAreaElement;
  assert.ok(textarea, "expected a JSON body textarea for a POST endpoint");

  textarea.value = '{"username":"newuser"}';
  stubFetch(() => ({ status: 201, body: JSON.stringify({ id: 1, username: "newuser" }) }));
  submit(doc.getElementById("callForm") as HTMLFormElement);
  await flush();

  const lastCall = calls[calls.length - 1];
  assert.equal(lastCall.init.body, '{"username":"newuser"}');
  assert.equal(lastCall.init.headers["Content-Type"], "application/json");

  const result = formContainer.querySelector('[data-result="call"]') as HTMLElement;
  assert.match(result.textContent ?? "", /Respuesta recibida\./);
});

test("selecting an endpoint with a path parameter renders a labeled input, whose value replaces the placeholder in the resolved URL", async () => {
  findEndpointButton("/api/page/clients/{username}/allInformation").click();

  const formContainer = doc.getElementById("endpointFormContainer") as HTMLElement;
  const label = Array.from(formContainer.querySelectorAll("label")).find((l) => l.textContent?.startsWith("username"));
  assert.ok(label, "expected a label for the 'username' path parameter");
  const usernameInput = label!.querySelector("input") as HTMLInputElement;
  usernameInput.value = "jdoe smith"; // deliberately includes a space to prove encodeURIComponent runs

  stubFetch(() => ({ status: 200, body: JSON.stringify({ id: 1, username: "jdoe smith" }) }));
  submit(doc.getElementById("callForm") as HTMLFormElement);
  await flush();

  const lastCall = calls[calls.length - 1];
  assert.match(lastCall.url, /\/api\/page\/clients\/jdoe%20smith\/allInformation$/);
});

test("a failed call renders the error message instead of a success result", async () => {
  findEndpointButton("/api/page/allMemberships").click();

  stubFetch(() => ({ status: 404, body: JSON.stringify({ message: "not found" }) }));
  submit(doc.getElementById("callForm") as HTMLFormElement);
  await flush();

  const formContainer = doc.getElementById("endpointFormContainer") as HTMLElement;
  const result = formContainer.querySelector('[data-result="call"]') as HTMLElement;
  assert.equal(result.className, "result result--error");
  assert.match(result.textContent ?? "", /not found/);
});

test("a failed login shows an error message and leaves the session badge unauthenticated", async () => {
  const loginForm = doc.getElementById("loginForm") as HTMLFormElement;
  (loginForm.elements.namedItem("username") as unknown as HTMLInputElement).value = "jdoe";
  (loginForm.elements.namedItem("password") as unknown as HTMLInputElement).value = "wrong";

  stubFetch(() => ({ status: 401, body: JSON.stringify("Invalid credentials") }));
  submit(loginForm);
  await flush();

  const loginResult = doc.querySelector('[data-result="login"]') as HTMLElement;
  assert.equal(loginResult.className, "result result--error");
  assert.match(loginResult.textContent ?? "", /Invalid credentials/);

  const badge = doc.getElementById("sessionBadge") as HTMLElement;
  assert.equal(badge.textContent, "Sin sesion");
  assert.equal(badge.className, "badge badge--off");
  assert.equal(getUsername(), "");
});

test("logging in successfully stores the session, updates the badge, and redacts the password in the request inspector", async () => {
  const loginForm = doc.getElementById("loginForm") as HTMLFormElement;
  (loginForm.elements.namedItem("username") as unknown as HTMLInputElement).value = "jdoe";
  (loginForm.elements.namedItem("password") as unknown as HTMLInputElement).value = "s3cret";

  stubFetch(() => ({
    status: 200,
    body: JSON.stringify({
      username: "jdoe",
      accessToken: "fake-access-token",
      refreshToken: "fake-refresh-token",
      expiresAt: "2026-01-01T00:00:00Z",
      refreshExpiresAt: "2026-01-02T00:00:00Z",
    }),
  }));
  submit(loginForm);
  await flush();

  const loginResult = doc.querySelector('[data-result="login"]') as HTMLElement;
  assert.equal(loginResult.className, "result result--success");
  assert.match(loginResult.textContent ?? "", /Sesion iniciada como jdoe\./);

  const badge = doc.getElementById("sessionBadge") as HTMLElement;
  assert.equal(badge.textContent, "Sesion: jdoe");
  assert.equal(badge.className, "badge badge--on");
  assert.equal(getUsername(), "jdoe");
  assert.equal(getAccessToken(), "fake-access-token");

  const lastRequest = JSON.parse((doc.getElementById("lastRequest") as HTMLElement).textContent ?? "{}");
  assert.equal(lastRequest.headers.username, "jdoe");
  assert.equal(lastRequest.headers.password, "<oculto>");
});

test("once logged in, calls attach a Bearer token, which the inspector also redacts", async () => {
  findEndpointButton("/api/page/allMemberships").click();
  stubFetch(() => ({ status: 200, body: "[]" }));
  submit(doc.getElementById("callForm") as HTMLFormElement);
  await flush();

  const lastCall = calls[calls.length - 1];
  assert.equal(lastCall.init.headers.Authorization, "Bearer fake-access-token");

  const lastRequest = JSON.parse((doc.getElementById("lastRequest") as HTMLElement).textContent ?? "{}");
  assert.equal(lastRequest.headers.Authorization, "Bearer <oculto>");
});

test("logging out clears the session, updates the badge, and redacts the refresh token in the inspector", async () => {
  stubFetch(() => ({ status: 200, body: "" }));
  (doc.getElementById("logoutBtn") as HTMLButtonElement).click();
  await flush();

  const logoutResult = doc.querySelector('[data-result="logout"]') as HTMLElement;
  assert.equal(logoutResult.className, "result result--success");
  assert.match(logoutResult.textContent ?? "", /Sesion cerrada\./);

  const badge = doc.getElementById("sessionBadge") as HTMLElement;
  assert.equal(badge.textContent, "Sin sesion");
  assert.equal(badge.className, "badge badge--off");
  assert.equal(getUsername(), "");
  assert.equal(getAccessToken(), "");

  const lastRequest = JSON.parse((doc.getElementById("lastRequest") as HTMLElement).textContent ?? "{}");
  assert.equal(lastRequest.headers.refresh_token, "<oculto>");

  // Subsequent calls no longer carry a Bearer token.
  findEndpointButton("/api/page/allMemberships").click();
  stubFetch(() => ({ status: 200, body: "[]" }));
  submit(doc.getElementById("callForm") as HTMLFormElement);
  await flush();
  assert.equal(calls[calls.length - 1].init.headers.Authorization, undefined);
});
