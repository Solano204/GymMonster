import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { PoolsPage } from "@/pages/PoolsPage";
import { renderWithProviders } from "@/test/utils";

describe("PoolsPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("renders pools in a data table once loaded", async () => {
    mock.onGet("/api/page/allPools").reply(200, [{ id: 1, name: "Olympic Pool", description: "50m" }]);

    renderWithProviders(<PoolsPage />);

    await waitFor(() => expect(screen.getByText("Olympic Pool")).toBeInTheDocument());
    expect(screen.getByText("50m")).toBeInTheDocument();
  });

  it("shows an empty state when there are no pools", async () => {
    mock.onGet("/api/page/allPools").reply(200, []);

    renderWithProviders(<PoolsPage />);

    await waitFor(() => expect(screen.getByText("No pools on record.")).toBeInTheDocument());
  });

  it("shows an error state when the request fails", async () => {
    mock.onGet("/api/page/allPools").reply(500, { error: "Server exploded" });

    renderWithProviders(<PoolsPage />);

    await waitFor(() => expect(screen.getByText(/Server exploded/)).toBeInTheDocument());
  });
});
