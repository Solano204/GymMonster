import { describe, it, expect, afterEach, vi } from "vitest";
import { renderHook, act } from "@testing-library/react";
import { authStore, useAuth } from "@/lib/auth";

describe("authStore", () => {
  afterEach(() => {
    authStore.clear();
  });

  it("has no session by default", () => {
    expect(authStore.getSnapshot()).toBeNull();
    expect(authStore.getToken()).toBeNull();
    expect(authStore.getUsername()).toBeNull();
  });

  it("setSession makes the session available via every accessor", () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });

    expect(authStore.getSnapshot()).toEqual({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    expect(authStore.getToken()).toBe("tok");
    expect(authStore.getUsername()).toBe("jdoe");
  });

  it("clear resets every accessor back to null", () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    authStore.clear();

    expect(authStore.getSnapshot()).toBeNull();
    expect(authStore.getToken()).toBeNull();
    expect(authStore.getUsername()).toBeNull();
  });

  it("notifies subscribers on setSession and clear", () => {
    const listener = vi.fn();
    const unsubscribe = authStore.subscribe(listener);

    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    expect(listener).toHaveBeenCalledTimes(1);

    authStore.clear();
    expect(listener).toHaveBeenCalledTimes(2);

    unsubscribe();
  });

  it("stops notifying after unsubscribe", () => {
    const listener = vi.fn();
    const unsubscribe = authStore.subscribe(listener);
    unsubscribe();

    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });

    expect(listener).not.toHaveBeenCalled();
  });
});

describe("useAuth", () => {
  afterEach(() => {
    authStore.clear();
  });

  it("returns null when there is no session", () => {
    const { result } = renderHook(() => useAuth());
    expect(result.current).toBeNull();
  });

  it("re-renders with the new session after setSession", () => {
    const { result } = renderHook(() => useAuth());

    act(() => {
      authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    });

    expect(result.current).toEqual({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
  });

  it("returns null again after clear", () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    const { result } = renderHook(() => useAuth());

    act(() => {
      authStore.clear();
    });

    expect(result.current).toBeNull();
  });
});
