import { test, before, beforeEach } from "node:test";
import assert from "node:assert/strict";
import { JSDOM } from "jsdom";
import { qs, renderResult } from "./dom.js";

let dom: JSDOM;

before(() => {
  dom = new JSDOM("<!doctype html><html><body></body></html>");
  (globalThis as unknown as { document: Document }).document = dom.window.document;
});

beforeEach(() => {
  dom.window.document.body.innerHTML = "";
});

test("qs finds an element matching the given selector", () => {
  dom.window.document.body.innerHTML = `<div id="target" class="foo">hi</div>`;

  const el = qs<HTMLDivElement>("#target");

  assert.equal(el.textContent, "hi");
});

test("qs searches within a given root instead of the whole document when one is passed", () => {
  dom.window.document.body.innerHTML = `<div id="outer"><span id="inner">x</span></div><span id="not-inner">y</span>`;
  const outer = qs<HTMLDivElement>("#outer");

  const inner = qs<HTMLSpanElement>("span", outer);

  assert.equal(inner.id, "inner");
});

test("qs throws a descriptive error when no element matches", () => {
  assert.throws(() => qs("#does-not-exist"), /Element not found: #does-not-exist/);
});

test("renderResult sets the result/result--<kind> class and renders the title", () => {
  const container = dom.window.document.createElement("div");

  renderResult(container, "success", "It worked");

  assert.equal(container.className, "result result--success");
  const title = container.querySelector("p.result__title");
  assert.equal(title?.textContent, "It worked");
  assert.equal(container.querySelector("pre.result__data"), null);
});

test("renderResult JSON-stringifies non-string data into a pre.result__data block", () => {
  const container = dom.window.document.createElement("div");

  renderResult(container, "error", "It failed", { code: 400, message: "bad" });

  assert.equal(container.className, "result result--error");
  const pre = container.querySelector("pre.result__data");
  assert.equal(pre?.textContent, JSON.stringify({ code: 400, message: "bad" }, null, 2));
});

test("renderResult renders string data as-is, without JSON-stringifying it", () => {
  const container = dom.window.document.createElement("div");

  renderResult(container, "success", "Raw text", "plain string body");

  assert.equal(container.querySelector("pre.result__data")?.textContent, "plain string body");
});

test("renderResult omits the data block entirely when data is undefined", () => {
  const container = dom.window.document.createElement("div");

  renderResult(container, "success", "No data here");

  assert.equal(container.children.length, 1);
  assert.equal(container.querySelector("pre.result__data"), null);
});

test("renderResult clears any previous content before rendering", () => {
  const container = dom.window.document.createElement("div");
  container.textContent = "stale content";

  renderResult(container, "success", "Fresh");

  assert.equal(container.children.length, 1);
  assert.equal(container.querySelector("p.result__title")?.textContent, "Fresh");
});
