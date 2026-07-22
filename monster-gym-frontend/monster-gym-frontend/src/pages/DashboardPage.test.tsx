import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { DashboardPage } from "@/pages/DashboardPage";
import { renderWithProviders } from "@/test/utils";

describe("DashboardPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("renders a count card per resource once each query resolves", async () => {
    mock.onGet("/api/page/allMemberships").reply(200, [{}, {}]);
    mock.onGet("/api/page/allPools").reply(200, [{}]);
    mock.onGet("/api/page/allSpecialties").reply(200, []);
    mock.onGet("/api/page/workclasses").reply(200, [{}, {}, {}]);
    mock.onGet("/api/page/allTrainers").reply(200, [{}]);

    renderWithProviders(<DashboardPage />);

    await waitFor(() => expect(screen.getByText("Membership tiers")).toBeInTheDocument());
    await waitFor(() => {
      const counts = screen.getAllByText(/^[0-9]+$/).map((el) => el.textContent);
      expect(counts).toContain("2");
      expect(counts).toContain("1");
      expect(counts).toContain("0");
      expect(counts).toContain("3");
    });
  });

  it("shows 'Unavailable' for a card whose query fails, without breaking the others", async () => {
    mock.onGet("/api/page/allMemberships").reply(500);
    mock.onGet("/api/page/allPools").reply(200, [{}]);
    mock.onGet("/api/page/allSpecialties").reply(200, []);
    mock.onGet("/api/page/workclasses").reply(200, []);
    mock.onGet("/api/page/allTrainers").reply(200, []);

    renderWithProviders(<DashboardPage />);

    await waitFor(() => expect(screen.getByText("Unavailable")).toBeInTheDocument());
  });

  it("renders the quick-action links to /clients and /promotions", () => {
    mock.onGet("/api/page/allMemberships").reply(200, []);
    mock.onGet("/api/page/allPools").reply(200, []);
    mock.onGet("/api/page/allSpecialties").reply(200, []);
    mock.onGet("/api/page/workclasses").reply(200, []);
    mock.onGet("/api/page/allTrainers").reply(200, []);

    renderWithProviders(<DashboardPage />);

    expect(screen.getByRole("link", { name: "Register a member" })).toHaveAttribute("href", "/clients");
    expect(screen.getByRole("link", { name: "View today's promotions" })).toHaveAttribute("href", "/promotions");
  });
});
