import { loadBaseUrl, saveBaseUrl, setSession, clearSession, setAccessToken, getAccessToken, getUsername } from "./config.js";
import { login, logout, callEndpoint, onApiCall, type ApiCallLog } from "./api.js";
import { qs, renderResult } from "./dom.js";
import { ENDPOINTS, isPublicPath } from "./endpoints.js";
import { pathParams } from "./pathParams.js";
import type { EndpointDef } from "./types.js";

let selected: EndpointDef | null = null;

function setupSettingsForm(): void {
  const baseUrlInput = qs<HTMLInputElement>("#baseUrl");
  const saveBtn = qs<HTMLButtonElement>("#saveConfigBtn");
  const savedMsg = qs<HTMLElement>("#configSavedMsg");

  baseUrlInput.value = loadBaseUrl();

  saveBtn.addEventListener("click", () => {
    saveBaseUrl(baseUrlInput.value.trim());
    savedMsg.textContent = "Configuracion guardada.";
    setTimeout(() => (savedMsg.textContent = ""), 2000);
  });
}

function setupInspector(): void {
  const requestPre = qs<HTMLElement>("#lastRequest");
  const responsePre = qs<HTMLElement>("#lastResponse");

  onApiCall((log: ApiCallLog) => {
    const headers = { ...log.requestHeaders };
    if (headers.Authorization) headers.Authorization = "Bearer <oculto>";
    if (headers.password) headers.password = "<oculto>";
    if (headers.refresh_token) headers.refresh_token = "<oculto>";
    requestPre.textContent = JSON.stringify({ method: log.method, url: log.url, headers, body: log.requestBody }, null, 2);
    responsePre.textContent = JSON.stringify({ status: log.responseStatus, body: log.responseBody }, null, 2);
  });
}

function updateSessionBadge(): void {
  const badge = qs<HTMLElement>("#sessionBadge");
  const username = getUsername();
  badge.textContent = username ? `Sesion: ${username}` : "Sin sesion";
  badge.className = username ? "badge badge--on" : "badge badge--off";
}

function setupAuthForms(): void {
  const loginForm = qs<HTMLFormElement>("#loginForm");
  const loginResult = qs<HTMLElement>('[data-result="login"]');
  const logoutBtn = qs<HTMLButtonElement>("#logoutBtn");
  const logoutResult = qs<HTMLElement>('[data-result="logout"]');

  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const data = new FormData(loginForm);
    try {
      const token = await login(String(data.get("username")), String(data.get("password")));
      setSession(token);
      updateSessionBadge();
      renderResult(loginResult, "success", `Sesion iniciada como ${token.username}.`, token);
    } catch (error) {
      renderResult(loginResult, "error", error instanceof Error ? error.message : "Error desconocido.");
    }
  });

  logoutBtn.addEventListener("click", async () => {
    try {
      await logout();
      clearSession();
      updateSessionBadge();
      renderResult(logoutResult, "success", "Sesion cerrada.");
    } catch (error) {
      renderResult(logoutResult, "error", error instanceof Error ? error.message : "Error desconocido.");
    }
  });
}


function renderEndpointList(filter: string): void {
  const container = qs<HTMLElement>("#endpointGroups");
  container.textContent = "";

  const term = filter.trim().toLowerCase();
  const groups = new Map<string, EndpointDef[]>();
  for (const ep of ENDPOINTS) {
    if (term && !ep.path.toLowerCase().includes(term) && !ep.group.toLowerCase().includes(term) && !ep.method.toLowerCase().includes(term)) {
      continue;
    }
    if (!groups.has(ep.group)) groups.set(ep.group, []);
    groups.get(ep.group)!.push(ep);
  }

  for (const [groupName, endpoints] of groups) {
    const details = document.createElement("details");
    details.open = term.length > 0;
    const summary = document.createElement("summary");
    summary.textContent = `${groupName} (${endpoints.length})`;
    details.appendChild(summary);

    const list = document.createElement("div");
    list.className = "endpoint-list";
    for (const ep of endpoints) {
      const item = document.createElement("button");
      item.type = "button";
      item.className = "endpoint-item";
      const isSelected = selected === ep;
      if (isSelected) item.classList.add("endpoint-item--selected");
      const publicTag = isPublicPath(ep.method, ep.path) ? " (publico)" : "";
      item.innerHTML = `<span class="endpoint-item__method endpoint-item__method--${ep.method.toLowerCase()}">${ep.method}</span> <span class="endpoint-item__path">${ep.path}${publicTag}</span>`;
      item.addEventListener("click", () => {
        selected = ep;
        renderEndpointList(qs<HTMLInputElement>("#endpointSearch").value);
        renderSelectedForm();
      });
      list.appendChild(item);
    }
    details.appendChild(list);
    container.appendChild(details);
  }
}

function renderSelectedForm(): void {
  const container = qs<HTMLElement>("#endpointFormContainer");
  container.textContent = "";
  if (!selected) {
    container.textContent = "Elegi un endpoint de la lista.";
    return;
  }

  const ep = selected;
  const title = document.createElement("p");
  title.className = "selected-endpoint";
  title.innerHTML = `<span class="endpoint-item__method endpoint-item__method--${ep.method.toLowerCase()}">${ep.method}</span> <code>${ep.path}</code>`;
  container.appendChild(title);

  const form = document.createElement("form");
  form.id = "callForm";

  const params = pathParams(ep.path);
  const paramInputs: Record<string, HTMLInputElement> = {};
  for (const param of params) {
    const label = document.createElement("label");
    label.textContent = param;
    const input = document.createElement("input");
    input.name = param;
    input.required = true;
    label.appendChild(input);
    form.appendChild(label);
    paramInputs[param] = input;
  }

  let bodyTextarea: HTMLTextAreaElement | null = null;
  if (ep.method === "POST" || ep.method === "PUT" || ep.method === "PATCH") {
    const label = document.createElement("label");
    label.textContent = "Cuerpo JSON (si aplica)";
    bodyTextarea = document.createElement("textarea");
    bodyTextarea.rows = 6;
    bodyTextarea.placeholder = "{}";
    label.appendChild(bodyTextarea);
    form.appendChild(label);
  }

  const submitBtn = document.createElement("button");
  submitBtn.type = "submit";
  submitBtn.textContent = "Enviar";
  form.appendChild(submitBtn);

  const result = document.createElement("div");
  result.className = "result";
  result.dataset.result = "call";

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    let resolvedPath = ep.path;
    for (const param of params) {
      resolvedPath = resolvedPath.replace(`{${param}}`, encodeURIComponent(paramInputs[param].value));
    }
    try {
      const data = await callEndpoint(ep.method, resolvedPath, bodyTextarea?.value ?? "");
      renderResult(result, "success", "Respuesta recibida.", data);
    } catch (error) {
      renderResult(result, "error", error instanceof Error ? error.message : "Error desconocido.");
    }
  });

  container.appendChild(form);
  container.appendChild(result);
}

function setupExplorer(): void {
  const search = qs<HTMLInputElement>("#endpointSearch");
  search.addEventListener("input", () => renderEndpointList(search.value));
  renderEndpointList("");
  renderSelectedForm();
}

setupSettingsForm();
setupInspector();
setupAuthForms();
setupExplorer();
updateSessionBadge();
