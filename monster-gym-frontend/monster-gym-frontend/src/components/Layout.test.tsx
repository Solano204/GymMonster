import { describe, it, expect, afterEach, beforeEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { Routes, Route } from "react-router-dom";
import { Layout } from "@/components/Layout";
import { authStore } from "@/lib/auth";
import { http } from "@/lib/http";
import { renderWithProviders } from "@/test/utils";

function renderLayout() {
  return renderWithProviders(
    <Routes>
      <Route path="/login" element={<div>login page</div>} />
      <Route element={<Layout />}>
        <Route path="/" element={<div>dashboard content</div>} />
      </Route>
    </Routes>,
    { route: "/" },
  );
}

describe("Layout", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref-tok" });
  });

  afterEach(() => {
    mock.restore();
    authStore.clear();
  });

  it("renders every nav link", () => {
    renderLayout();

    ["Overview", "Members", "Trainers", "Classes", "Memberships", "Pools", "Specialties", "Promotions"].forEach(
      (label) => {
        expect(screen.getByRole("link", { name: label })).toBeInTheDocument();
      },
    );
  });

  it("renders the routed page content via the Outlet", () => {
    renderLayout();
    expect(screen.getByText("dashboard content")).toBeInTheDocument();
  });

  it("shows the logged-in user's username", () => {
    renderLayout();
    expect(screen.getByText("jdoe")).toBeInTheDocument();
  });

  it("logout calls authApi.logout, clears the session, and navigates to /login", async () => {
    mock.onPost("/GymMonster/auth/logout").reply(200, "Logout Successful");
    renderLayout();

    await userEvent.click(screen.getByRole("button", { name: "Logout" }));

    await waitFor(() => expect(screen.getByText("login page")).toBeInTheDocument());
    expect(authStore.getSnapshot()).toBeNull();
  });

  it("logout still clears the session and navigates away even if the backend call fails", async () => {
    mock.onPost("/GymMonster/auth/logout").reply(500);
    renderLayout();

    await userEvent.click(screen.getByRole("button", { name: "Logout" }));

    await waitFor(() => expect(screen.getByText("login page")).toBeInTheDocument());
    expect(authStore.getSnapshot()).toBeNull();
  });
});
