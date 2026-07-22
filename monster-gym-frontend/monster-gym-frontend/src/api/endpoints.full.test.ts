import { describe, it, expect, beforeEach, afterEach } from "vitest";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { clientsApi, promotionsApi, workClassesApi, trainersApi } from "@/api/endpoints";

/**
 * Extends endpoints.test.ts (which pins clientsApi's write-contract fixes and the
 * "no phantom fields" read DTOs) with every remaining endpoint this frontend calls.
 */
describe("promotionsApi", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("current requests the currentDate path segment", async () => {
    mock.onGet("/api/page/promotions/currentPromotions/2026-01-01").reply(200, []);

    await promotionsApi.current("2026-01-01");

    expect(mock.history.get[0].url).toBe("/api/page/promotions/currentPromotions/2026-01-01");
  });

  it("byDate sends the date as BOTH a path segment and a query param, working around the backend's known bug", async () => {
    // RouterPromotion's route declares {date} as a path variable, but the handler
    // actually reads a `date` query param instead - see RouterPromotionTest on the
    // backend. Sending both keeps this working today AND if the backend is ever fixed.
    mock.onGet(/\/api\/page\/promotions\/specificDate\//).reply((config) => {
      expect(config.url).toBe("/api/page/promotions/specificDate/2026-02-01");
      expect(config.params).toEqual({ date: "2026-02-01" });
      return [200, []];
    });

    await promotionsApi.byDate("2026-02-01");
  });
});

describe("workClassesApi", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("list requests the fixed endpoint", async () => {
    mock.onGet("/api/page/workclasses").reply(200, [{ name: "Yoga", description: "d", duration: "60min" }]);

    const result = await workClassesApi.list();

    expect(result[0].name).toBe("Yoga");
  });

  it("schedules URL-encodes the work class name", async () => {
    mock.onGet("/api/page/workclasses/Advanced%20Yoga/schedules").reply(200, []);

    await workClassesApi.schedules("Advanced Yoga");

    expect(mock.history.get[0].url).toBe("/api/page/workclasses/Advanced%20Yoga/schedules");
  });

  it("clients defaults page/size to 0/10 when not provided", async () => {
    mock.onGet("/api/page/workclasses/Yoga/clients").reply((config) => {
      expect(config.params).toEqual({ page: 0, size: 10 });
      return [200, []];
    });

    await workClassesApi.clients("Yoga");
  });

  it("trainers passes through explicit page/size", async () => {
    mock.onGet("/api/page/workclasses/Yoga/trainers").reply((config) => {
      expect(config.params).toEqual({ page: 2, size: 25 });
      return [200, []];
    });

    await workClassesApi.trainers("Yoga", { page: 2, size: 25 });
  });
});

describe("trainersApi", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("list defaults page/size to 0/10", async () => {
    mock.onGet("/api/page/allTrainers").reply((config) => {
      expect(config.params).toEqual({ page: 0, size: 10 });
      return [200, []];
    });

    await trainersApi.list();
  });

  it("specialties URL-encodes the trainer username", async () => {
    mock.onGet("/api/page/trainers/coach%2099/specialties").reply(200, []);

    await trainersApi.specialties("coach 99");
  });
});

describe("clientsApi (remaining methods)", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("getInfo requests the client's allInformation endpoint", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, { id: 1, username: "jdoe" });

    const result = await clientsApi.getInfo("jdoe");

    expect(result.username).toBe("jdoe");
  });

  it("getClasses requests the client's allClass endpoint", async () => {
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);

    await clientsApi.getClasses("jdoe");

    expect(mock.history.get[0].url).toBe("/api/page/clients/jdoe/allClass");
  });

  it("updateAllInformation PUTs the detail payload as the body", async () => {
    const payload = { name: "John", secondName: "Q", lastNameM: "Doe", lastNameP: "Public", age: "30", weight: "80", height: "180" };
    mock.onPut("/api/page/clients/jdoe/changeInformation").reply((config) => {
      expect(JSON.parse(config.data)).toEqual(payload);
      return [200, "Updated"];
    });

    const result = await clientsApi.updateAllInformation("jdoe", payload);
    expect(result).toBe("Updated");
  });

  it("updateUsername PUTs to the old/new username path segments", async () => {
    mock.onPut("/api/page/clients/jdoe/jdoe2/changeUsername").reply(200, "Username changed");

    const result = await clientsApi.updateUsername("jdoe", "jdoe2");
    expect(result).toBe("Username changed");
  });

  it("updateEmail PUTs to the username/email path segments", async () => {
    mock.onPut("/api/page/clients/jdoe/new%40test.com/changeEmail").reply(200, "Email changed");

    const result = await clientsApi.updateEmail("jdoe", "new@test.com");
    expect(result).toBe("Email changed");
  });

  it("updateMembership PUTs to the username/membershipType path segments", async () => {
    mock.onPut("/api/page/clients/jdoe/GOLD/changeMembership").reply(200, "Membership changed");

    const result = await clientsApi.updateMembership("jdoe", "GOLD");
    expect(result).toBe("Membership changed");
  });

  it("updateTrainer PUTs to the username/trainer path segments", async () => {
    mock.onPut("/api/page/clients/jdoe/coach99/changeTrainer").reply(200, "Trainer changed");

    const result = await clientsApi.updateTrainer("jdoe", "coach99");
    expect(result).toBe("Trainer changed");
  });

  it("removeMembership DELETEs to the username/membershipType path segments", async () => {
    mock.onDelete("/api/page/clients/jdoe/GOLD/delete-membership").reply(200, "Membership removed");

    const result = await clientsApi.removeMembership("jdoe", "GOLD");
    expect(result).toBe("Membership removed");
  });

  it("removeTrainer DELETEs to the username/trainer path segments", async () => {
    mock.onDelete("/api/page/clients/jdoe/coach99/remove-trainer").reply(200, "Trainer removed");

    const result = await clientsApi.removeTrainer("jdoe", "coach99");
    expect(result).toBe("Trainer removed");
  });
});
