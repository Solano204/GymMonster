import { test } from "node:test";
import assert from "node:assert/strict";
import { pathParams } from "./pathParams.js";

test("pathParams extracts every {segment} in order", () => {
  assert.deepEqual(
    pathParams("/api/admin/clients/{username}/{membershipType}/change-membership"),
    ["username", "membershipType"],
  );
});

test("pathParams returns an empty array when there are no placeholders", () => {
  assert.deepEqual(pathParams("/api/page/allMemberships"), []);
});
