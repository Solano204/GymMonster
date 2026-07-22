import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { SpecialtiesPage } from "@/pages/SpecialtiesPage";
import { renderWithProviders } from "@/test/utils";

describe("SpecialtiesPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("renders specialty cards once loaded", async () => {
    mock.onGet("/api/page/allSpecialties").reply(200, [{ name: "Yoga", description: "Relaxing" }]);

    renderWithProviders(<SpecialtiesPage />);

    await waitFor(() => expect(screen.getByText("Yoga")).toBeInTheDocument());
    expect(screen.getByText("Relaxing")).toBeInTheDocument();
  });

  it("shows an empty state when there are no specialties", async () => {
    mock.onGet("/api/page/allSpecialties").reply(200, []);

    renderWithProviders(<SpecialtiesPage />);

    await waitFor(() => expect(screen.getByText("No specialties on record.")).toBeInTheDocument());
  });

  it("shows an error state when the request fails", async () => {
    mock.onGet("/api/page/allSpecialties").reply(500, { error: "Server exploded" });

    renderWithProviders(<SpecialtiesPage />);

    await waitFor(() => expect(screen.getByText(/Server exploded/)).toBeInTheDocument());
  });
});
