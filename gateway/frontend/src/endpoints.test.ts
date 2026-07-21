import { test } from "node:test";
import assert from "node:assert/strict";
import { ENDPOINTS, isPublicPath } from "./endpoints.js";
import { pathParams } from "./pathParams.js";

// Regression coverage for the changePassword/deleteAccount contract fix:
// RegisterClientHandler.java moved these from path segments to a JSON body
// (a password in the URL ends up in browser history/access logs/Referer
// headers). This explorer's endpoint list had drifted and still declared the
// old path-param routes, which would 404 against the real backend.
test("changePassword and deleteAccount no longer declare password as a path segment", () => {
  const changePassword = ENDPOINTS.find(
    (e) => e.method === "PUT" && e.path === "/api/page/clients/{username}/changePassword",
  );
  const deleteAccount = ENDPOINTS.find(
    (e) => e.method === "DELETE" && e.path === "/api/page/clients/{username}/deleteAccount",
  );

  assert.ok(changePassword, "expected the current PUT .../changePassword route to be listed");
  assert.ok(deleteAccount, "expected the current DELETE .../deleteAccount route to be listed");
  assert.deepEqual(pathParams(changePassword!.path), ["username"]);
  assert.deepEqual(pathParams(deleteAccount!.path), ["username"]);
});

test("every declared endpoint's path starts with /api/", () => {
  for (const ep of ENDPOINTS) {
    assert.ok(ep.path.startsWith("/api/"), `${ep.method} ${ep.path} should start with /api/`);
  }
});

// Only web-page's client-facing routes (/api/page/**) were moved off credential-in-URL
// this session - RegisterClientHandler.java. server-administrator's /api/admin/**
// equivalents (e.g. change-password/{oldPassword}/{newPassword}, trainer password/delete
// routes) still carry credentials in the path and were intentionally left alone (broader,
// separate-service change - flagged, not fixed). This test only pins the routes actually
// fixed, so it doesn't start failing for a gap that was never in scope.
test("/api/page/** client routes never carry a password in the path", () => {
  for (const ep of ENDPOINTS.filter((e) => e.path.startsWith("/api/page/"))) {
    for (const param of pathParams(ep.path)) {
      assert.doesNotMatch(param, /password/i, `${ep.method} ${ep.path} should not carry a password path segment`);
    }
  }
});

test("isPublicPath matches the exact public GET/POST list, nothing broader", () => {
  assert.equal(isPublicPath("GET", "/api/page/allMemberships"), true);
  assert.equal(isPublicPath("GET", "/api/page/allPools"), true);
  assert.equal(isPublicPath("GET", "/api/page/allSpecialties"), true);
  assert.equal(isPublicPath("GET", "/api/page/promotions/currentPromotions/2026-01-01"), true);
  assert.equal(isPublicPath("GET", "/api/page/workclasses"), true);
  assert.equal(isPublicPath("POST", "/api/page/registerClient"), true);

  assert.equal(isPublicPath("GET", "/api/page/clients/jdoe/allInformation"), false);
  assert.equal(isPublicPath("PUT", "/api/page/clients/jdoe/changePassword"), false);
  assert.equal(isPublicPath("GET", "/api/admin/clients"), false);
});
