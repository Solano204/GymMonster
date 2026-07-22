import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { PromotionsPage } from "@/pages/PromotionsPage";
import { renderWithProviders } from "@/test/utils";

function todayIso() {
  return new Date().toISOString().slice(0, 10);
}

describe("PromotionsPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("defaults the date picker to today and loads today's promotions", async () => {
    mock.onGet(`/api/page/promotions/currentPromotions/${todayIso()}`).reply(200, [
      { description: "Summer sale", duration: "1 month", percentageDiscount: 20, startDate: "2026-01-01", endDate: "2026-02-01", active: true },
    ]);

    renderWithProviders(<PromotionsPage />);

    await waitFor(() => expect(screen.getByText("20% off")).toBeInTheDocument());
    expect(screen.getByText("Summer sale")).toBeInTheDocument();
    expect(screen.getByText("Live")).toBeInTheDocument();
  });

  it("shows 'Ended' tab for an inactive promotion", async () => {
    mock.onGet(`/api/page/promotions/currentPromotions/${todayIso()}`).reply(200, [
      { description: "Old sale", duration: "1 week", percentageDiscount: 10, startDate: "2025-01-01", endDate: "2025-01-08", active: false },
    ]);

    renderWithProviders(<PromotionsPage />);

    await waitFor(() => expect(screen.getByText("Ended")).toBeInTheDocument());
  });

  it("re-queries when the date input changes", async () => {
    mock.onGet(`/api/page/promotions/currentPromotions/${todayIso()}`).reply(200, []);
    mock.onGet("/api/page/promotions/currentPromotions/2026-06-01").reply(200, [
      { description: "Winter sale", duration: "1 month", percentageDiscount: 15, startDate: "2026-06-01", endDate: "2026-07-01", active: true },
    ]);

    renderWithProviders(<PromotionsPage />);
    await waitFor(() => expect(screen.getByText("No promotions active on this date.")).toBeInTheDocument());

    const dateInput = screen.getByLabelText("As of") as HTMLInputElement;
    await userEvent.clear(dateInput);
    await userEvent.type(dateInput, "2026-06-01");

    await waitFor(() => expect(screen.getByText("Winter sale")).toBeInTheDocument());
  });
});
