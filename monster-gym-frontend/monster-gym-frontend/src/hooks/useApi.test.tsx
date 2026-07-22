import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { renderHook, waitFor } from "@testing-library/react";
import { QueryClientProvider } from "@tanstack/react-query";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import {
  useMemberships,
  useRegisterClient,
  useUpdateClientPassword,
  useClientInfo,
  useWorkClassSchedules,
} from "@/hooks/useApi";
import { newTestQueryClient } from "@/test/utils";

function wrapperFor(queryClient = newTestQueryClient()) {
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}

describe("useApi read hooks", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("useMemberships fetches and exposes the membership list", async () => {
    mock.onGet("/api/page/allMemberships").reply(200, [
      { membershipType: "GOLD", description: "d", hasCardio: true, hasPool: true, hasFoodCourt: true },
    ]);

    const { result } = renderHook(() => useMemberships(), { wrapper: wrapperFor() });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.[0].membershipType).toBe("GOLD");
  });

  it("useClientInfo does not fire the request when username is undefined", async () => {
    const { result } = renderHook(() => useClientInfo(undefined), { wrapper: wrapperFor() });

    // Should stay in an idle-ish pending state, never resolving/erroring, and never hit the mock.
    await new Promise((r) => setTimeout(r, 10));
    expect(result.current.fetchStatus).toBe("idle");
    expect(mock.history.get).toHaveLength(0);
  });

  it("useClientInfo fetches once a username is provided", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, { id: 1, username: "jdoe" });

    const { result } = renderHook(() => useClientInfo("jdoe"), { wrapper: wrapperFor() });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data?.username).toBe("jdoe");
  });

  it("useWorkClassSchedules is disabled when name is undefined", async () => {
    const { result } = renderHook(() => useWorkClassSchedules(undefined), { wrapper: wrapperFor() });

    await new Promise((r) => setTimeout(r, 10));
    expect(result.current.fetchStatus).toBe("idle");
  });
});

describe("useApi mutation hooks", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("useRegisterClient posts the payload and invalidates the clients query cache on success", async () => {
    mock.onPost("/api/page/registerClient").reply(201, { id: 1, username: "newuser" });
    const queryClient = newTestQueryClient();
    const invalidateSpy = vi.spyOn(queryClient, "invalidateQueries");

    const { result } = renderHook(() => useRegisterClient(), { wrapper: wrapperFor(queryClient) });

    result.current.mutate({
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
      membershiptype: "GOLD",
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ["clients"] });
  });

  it("useRegisterClient rejects a username that already exists, without ever POSTing to registerClient", async () => {
    mock.onGet("/api/page/clients/existing/allInformation").reply(200, { id: 1, username: "existing" });
    const queryClient = newTestQueryClient();
    const invalidateSpy = vi.spyOn(queryClient, "invalidateQueries");

    const { result } = renderHook(() => useRegisterClient(), { wrapper: wrapperFor(queryClient) });

    result.current.mutate({
      username: "existing",
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
      membershiptype: "GOLD",
    });

    await waitFor(() => expect(result.current.isError).toBe(true));
    expect(result.current.error?.message).toMatch(/already taken/);
    expect(mock.history.post).toHaveLength(0);
    expect(invalidateSpy).not.toHaveBeenCalled();
  });

  it("useUpdateClientPassword calls the endpoint with old/new password and does not invalidate any cache", async () => {
    mock.onPut("/api/page/clients/jdoe/changePassword").reply(200, "Password updated");
    const queryClient = newTestQueryClient();
    const invalidateSpy = vi.spyOn(queryClient, "invalidateQueries");

    const { result } = renderHook(() => useUpdateClientPassword("jdoe"), { wrapper: wrapperFor(queryClient) });

    result.current.mutate({ oldPassword: "old1", newPassword: "new1" });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));
    expect(result.current.data).toBe("Password updated");
    expect(invalidateSpy).not.toHaveBeenCalled();
  });

  it("useRegisterClient surfaces the error on failure without invalidating the cache", async () => {
    mock.onPost("/api/page/registerClient").reply(400, { error: "Username already exists" });
    const queryClient = newTestQueryClient();
    const invalidateSpy = vi.spyOn(queryClient, "invalidateQueries");

    const { result } = renderHook(() => useRegisterClient(), { wrapper: wrapperFor(queryClient) });

    result.current.mutate({
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
      membershiptype: "GOLD",
    });

    await waitFor(() => expect(result.current.isError).toBe(true));
    expect(invalidateSpy).not.toHaveBeenCalled();
  });
});
