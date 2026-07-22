import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { Routes, Route } from "react-router-dom";
import { http } from "@/lib/http";
import { authStore } from "@/lib/auth";
import { LoginPage } from "@/pages/LoginPage";
import { renderWithProviders } from "@/test/utils";

function renderLoginRoute() {
  return renderWithProviders(
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<div>dashboard</div>} />
    </Routes>,
    { route: "/login" },
  );
}

describe("LoginPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
    authStore.clear();
  });

  it("redirects to / immediately if a session already exists", () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });

    renderLoginRoute();

    expect(screen.getByText("dashboard")).toBeInTheDocument();
  });

  it("submits credentials, stores the session, and navigates to / on success", async () => {
    mock.onPost("/GymMonster/auth/login").reply(200, {
      username: "jdoe",
      accessToken: "access-tok",
      refreshToken: "refresh-tok",
      expiresAt: "2026-01-01T00:00:00Z",
      refreshExpiresAt: "2026-01-02T00:00:00Z",
    });

    renderLoginRoute();

    await userEvent.type(screen.getByLabelText("Username"), "jdoe");
    await userEvent.type(screen.getByLabelText("Password"), "s3cret");
    await userEvent.click(screen.getByRole("button", { name: "Sign in" }));

    await waitFor(() => expect(screen.getByText("dashboard")).toBeInTheDocument());
    expect(authStore.getSnapshot()).toEqual({
      username: "jdoe",
      accessToken: "access-tok",
      refreshToken: "refresh-tok",
    });
  });

  it("shows an error message and does not navigate when login fails", async () => {
    mock.onPost("/GymMonster/auth/login").reply(401, { error: "Invalid credentials" });

    renderLoginRoute();

    await userEvent.type(screen.getByLabelText("Username"), "jdoe");
    await userEvent.type(screen.getByLabelText("Password"), "wrong");
    await userEvent.click(screen.getByRole("button", { name: "Sign in" }));

    await waitFor(() => expect(screen.getByText(/Invalid credentials/)).toBeInTheDocument());
    expect(screen.queryByText("dashboard")).not.toBeInTheDocument();
    expect(authStore.getSnapshot()).toBeNull();
  });
});
