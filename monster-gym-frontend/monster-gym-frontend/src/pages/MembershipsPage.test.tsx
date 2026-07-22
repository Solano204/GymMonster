import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { MembershipsPage } from "@/pages/MembershipsPage";
import { renderWithProviders } from "@/test/utils";

describe("MembershipsPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("shows a loading state, then the membership cards with their perks", async () => {
    mock.onGet("/api/page/allMemberships").reply(200, [
      { membershipType: "GOLD", description: "Full access", hasCardio: true, hasPool: true, hasFoodCourt: false },
    ]);

    renderWithProviders(<MembershipsPage />);

    expect(screen.getByText(/Loading memberships/)).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText("GOLD")).toBeInTheDocument());
    expect(screen.getByText("Full access")).toBeInTheDocument();
    expect(screen.getByText("Cardio floor")).toBeInTheDocument();
    expect(screen.getByText("Pool access")).toBeInTheDocument();
  });

  it("shows an empty state when there are no membership tiers", async () => {
    mock.onGet("/api/page/allMemberships").reply(200, []);

    renderWithProviders(<MembershipsPage />);

    await waitFor(() => expect(screen.getByText("No membership tiers yet.")).toBeInTheDocument());
  });

  it("shows an error state when the request fails", async () => {
    mock.onGet("/api/page/allMemberships").reply(500, { error: "Server exploded" });

    renderWithProviders(<MembershipsPage />);

    await waitFor(() => expect(screen.getByText(/Server exploded/)).toBeInTheDocument());
  });
});
