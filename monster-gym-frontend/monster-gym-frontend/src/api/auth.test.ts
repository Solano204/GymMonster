import { describe, it, expect, beforeEach, afterEach } from "vitest";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { authApi } from "@/api/auth";

/**
 * SessionController (gateway) takes credentials as plain headers, not a JSON body -
 * these pin that contract from the frontend side.
 */
describe("authApi", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("login sends username/password as headers, with no request body", async () => {
    mock.onPost("/GymMonster/auth/login").reply((config) => {
      expect(config.headers?.username).toBe("jdoe");
      expect(config.headers?.password).toBe("s3cret");
      expect(config.data).toBeUndefined();
      return [
        200,
        {
          username: "jdoe",
          accessToken: "access-tok",
          refreshToken: "refresh-tok",
          expiresAt: "2026-01-01T00:00:00Z",
          refreshExpiresAt: "2026-01-02T00:00:00Z",
        },
      ];
    });

    const token = await authApi.login("jdoe", "s3cret");

    expect(token.username).toBe("jdoe");
    expect(token.accessToken).toBe("access-tok");
  });

  it("logout sends username and refresh_token as headers, with no request body", async () => {
    mock.onPost("/GymMonster/auth/logout").reply((config) => {
      expect(config.headers?.username).toBe("jdoe");
      expect(config.headers?.refresh_token).toBe("refresh-tok");
      expect(config.data).toBeUndefined();
      return [200, "Logout Successful"];
    });

    const result = await authApi.logout("jdoe", "refresh-tok");

    expect(result).toBe("Logout Successful");
  });
});
