import { describe, it, expect, beforeEach, afterEach } from "vitest";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { clientsApi, membershipsApi, poolsApi, specialtiesApi } from "@/api/endpoints";

/**
 * Pins the JSON contract between this frontend and web-page's actual DTOs.
 * changePassword/deleteAccount regressed once already (backend moved from path
 * segments to a JSON body, RegisterClientHandler.java) - these tests fail loudly
 * if the request shape drifts from the backend again.
 */
describe("clientsApi", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("updatePassword sends credentials as a JSON body, not URL segments", async () => {
    mock.onPut("/api/page/clients/jdoe/changePassword").reply((config) => {
      expect(JSON.parse(config.data)).toEqual({ oldPassword: "old1", newPassword: "new1" });
      return [200, "Password updated"];
    });

    const result = await clientsApi.updatePassword("jdoe", "old1", "new1");
    expect(result).toBe("Password updated");
  });

  it("deleteAccount sends the password as a JSON body, not a URL segment", async () => {
    mock.onDelete("/api/page/clients/jdoe/deleteAccount").reply((config) => {
      expect(JSON.parse(config.data)).toEqual({ password: "secret1" });
      return [200, "Account deleted"];
    });

    const result = await clientsApi.deleteAccount("jdoe", "secret1");
    expect(result).toBe("Account deleted");
  });

  it("register posts to /api/page/registerClient with the full client payload", async () => {
    const payload = {
      username: "newuser",
      password: "pw",
      email: "a@b.com",
      trainername: null,
      name: "A",
      secondname: "B",
      lastnamep: "C",
      lastnamem: "D",
      age: "30",
      height: "180",
      weight: "80",
      membershiptype: "basic",
    };
    mock.onPost("/api/page/registerClient").reply((config) => {
      expect(JSON.parse(config.data)).toEqual(payload);
      return [201, { id: 1, ...payload }];
    });

    const result = await clientsApi.register(payload as never);
    expect(result.id).toBe(1);
  });
});

describe("read endpoints match the backend DTO shape (no phantom fields)", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("membershipsApi.list has no `id` field (DtoMembershipReciving has none)", async () => {
    mock.onGet("/api/page/allMemberships").reply(200, [
      { membershipType: "gold", description: "d", hasCardio: true, hasPool: true, hasFoodCourt: false },
    ]);
    const [m] = await membershipsApi.list();
    expect(m.membershipType).toBe("gold");
    expect((m as unknown as Record<string, unknown>).id).toBeUndefined();
  });

  it("specialtiesApi.list has no `id` field (DtoSpecialtyRecived has none)", async () => {
    mock.onGet("/api/page/allSpecialties").reply(200, [{ name: "yoga", description: "d" }]);
    const [s] = await specialtiesApi.list();
    expect(s.name).toBe("yoga");
    expect((s as unknown as Record<string, unknown>).id).toBeUndefined();
  });

  it("poolsApi.list only has id/name/description (DtoPoolReciving has no schedule fields)", async () => {
    mock.onGet("/api/page/allPools").reply(200, [{ id: 1, name: "Main pool", description: "d" }]);
    const [p] = await poolsApi.list();
    expect(p.name).toBe("Main pool");
    expect((p as unknown as Record<string, unknown>).dateClean).toBeUndefined();
  });
});
