import { describe, it, expect, beforeEach, afterEach } from "vitest";
import MockAdapter from "axios-mock-adapter";
import { http, ApiError } from "@/lib/http";
import { authStore } from "@/lib/auth";

describe("http auth interceptors", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
    authStore.clear();
  });

  afterEach(() => {
    mock.restore();
    authStore.clear();
  });

  it("does not attach Authorization/username headers when logged out", async () => {
    mock.onGet("/api/page/allMemberships").reply((config) => {
      expect(config.headers?.Authorization).toBeUndefined();
      return [200, []];
    });
    await http.get("/api/page/allMemberships");
  });

  it("attaches Bearer token and username once logged in", async () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok123", refreshToken: "ref123" });
    mock.onGet("/api/page/clients/jdoe/allInformation").reply((config) => {
      expect(config.headers?.Authorization).toBe("Bearer tok123");
      expect(config.headers?.username).toBe("jdoe");
      return [200, {}];
    });
    await http.get("/api/page/clients/jdoe/allInformation");
  });

  it("clears the session and rejects with ApiError on a 401", async () => {
    authStore.setSession({ username: "jdoe", accessToken: "expired", refreshToken: "ref123" });
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(401, { error: "Token expired" });

    await expect(http.get("/api/page/clients/jdoe/allInformation")).rejects.toBeInstanceOf(ApiError);
    expect(authStore.getToken()).toBeNull();
  });
});
