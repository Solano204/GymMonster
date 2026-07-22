import { describe, it, expect, afterEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import { RequireAuth } from "@/components/RequireAuth";
import { authStore } from "@/lib/auth";

function renderProtected() {
  return render(
    <MemoryRouter initialEntries={["/clients"]}>
      <Routes>
        <Route path="/login" element={<div>login page</div>} />
        <Route element={<RequireAuth />}>
          <Route path="/clients" element={<div>clients page</div>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );
}

describe("RequireAuth", () => {
  afterEach(() => {
    authStore.clear();
  });

  it("redirects to /login when there is no session", () => {
    renderProtected();
    expect(screen.getByText("login page")).toBeInTheDocument();
    expect(screen.queryByText("clients page")).not.toBeInTheDocument();
  });

  it("renders the protected route once a session exists", () => {
    authStore.setSession({ username: "jdoe", accessToken: "tok", refreshToken: "ref" });
    renderProtected();
    expect(screen.getByText("clients page")).toBeInTheDocument();
  });
});
