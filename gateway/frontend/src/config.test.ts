import { test, beforeEach } from "node:test";
import assert from "node:assert/strict";
import {
  loadBaseUrl,
  saveBaseUrl,
  setSession,
  clearSession,
  setAccessToken,
  getAccessToken,
  getRefreshToken,
  getUsername,
} from "./config.js";
import { installMemoryStorage } from "./testSupport.js";

const storage = installMemoryStorage();

beforeEach(() => {
  storage.clear();
  clearSession();
});

test("loadBaseUrl returns the default gateway URL when nothing is stored", () => {
  assert.equal(loadBaseUrl(), "http://localhost:8081");
});

test("saveBaseUrl persists a value that loadBaseUrl then returns", () => {
  saveBaseUrl("http://localhost:9999");
  assert.equal(loadBaseUrl(), "http://localhost:9999");
});

test("setSession stores accessToken, refreshToken and username in memory", () => {
  setSession({ accessToken: "a-tok", refreshToken: "r-tok", username: "jdoe" });

  assert.equal(getAccessToken(), "a-tok");
  assert.equal(getRefreshToken(), "r-tok");
  assert.equal(getUsername(), "jdoe");
});

test("clearSession resets accessToken, refreshToken and username to empty strings", () => {
  setSession({ accessToken: "a", refreshToken: "r", username: "u" });

  clearSession();

  assert.equal(getAccessToken(), "");
  assert.equal(getRefreshToken(), "");
  assert.equal(getUsername(), "");
});

test("setAccessToken trims surrounding whitespace off the raw token", () => {
  setAccessToken("  raw-token  \n");

  assert.equal(getAccessToken(), "raw-token");
});

test("session values (access/refresh token, username) are never written to localStorage", () => {
  setSession({ accessToken: "secret-access", refreshToken: "secret-refresh", username: "jdoe" });
  setAccessToken("secret-access-2");

  assert.equal(storage.length, 0);
});

test("getAccessToken/getRefreshToken/getUsername default to empty strings before any session is set", () => {
  assert.equal(getAccessToken(), "");
  assert.equal(getRefreshToken(), "");
  assert.equal(getUsername(), "");
});
