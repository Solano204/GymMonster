import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { authStore } from "@/lib/auth";
import App from "@/App";
import { renderWithProviders } from "@/test/utils";

describe("App routing", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
    authStore.clear();
  });

  it("redirects an unauthenticated visitor at / to the login page", () => {
    renderWithProviders(<App />, { route: "/" });

    expect(screen.getByRole("button", { name: "Sign in" })).toBeInTheDocument();
  });

  it("shows the Layout and DashboardPage for an authenticated visitor at /", async () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    mock.onGet("/api/page/allMemberships").reply(200, []);
    mock.onGet("/api/page/allPools").reply(200, []);
    mock.onGet("/api/page/allSpecialties").reply(200, []);
    mock.onGet("/api/page/workclasses").reply(200, []);
    mock.onGet("/api/page/allTrainers").reply(200, []);

    renderWithProviders(<App />, { route: "/" });

    expect(screen.getByRole("link", { name: "Overview" })).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText("Membership tiers")).toBeInTheDocument());
  });

  it("redirects an unauthenticated visitor away from a protected route like /clients", () => {
    renderWithProviders(<App />, { route: "/clients" });

    expect(screen.getByRole("button", { name: "Sign in" })).toBeInTheDocument();
  });
});
