export function qs<T extends Element>(selector: string, root: ParentNode = document): T {
  const el = root.querySelector<T>(selector);
  if (!el) throw new Error(`Element not found: ${selector}`);
  return el;
}

export function renderResult(container: HTMLElement, kind: "success" | "error", title: string, data?: unknown): void {
  container.className = `result result--${kind}`;
  container.textContent = "";

  const heading = document.createElement("p");
  heading.className = "result__title";
  heading.textContent = title;
  container.appendChild(heading);

  if (data !== undefined) {
    const pre = document.createElement("pre");
    pre.className = "result__data";
    pre.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
    container.appendChild(pre);
  }
}
