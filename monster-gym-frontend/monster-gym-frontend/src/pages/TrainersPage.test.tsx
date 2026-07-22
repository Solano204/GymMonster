import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { TrainersPage } from "@/pages/TrainersPage";
import { renderWithProviders } from "@/test/utils";

const TRAINER = {
  username: "coach99",
  email: "coach99@test.com",
  name: "Ana",
  secondName: "M",
  lastNameP: "Lopez",
  lastNameM: "Diaz",
  age: "35",
};

describe("TrainersPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("requests page 0 by default and renders the trainer roster", async () => {
    mock.onGet("/api/page/allTrainers").reply((config) => {
      expect(config.params).toEqual({ page: 0, size: 10 });
      return [200, [TRAINER]];
    });

    renderWithProviders(<TrainersPage />);

    await waitFor(() => expect(screen.getByText(/Ana M Lopez Diaz/)).toBeInTheDocument());
    expect(screen.getByText("Page 1")).toBeInTheDocument();
  });

  it("shows an empty state only on the first page", async () => {
    mock.onGet("/api/page/allTrainers").reply(200, []);

    renderWithProviders(<TrainersPage />);

    await waitFor(() => expect(screen.getByText("No trainers on record.")).toBeInTheDocument());
  });

  it("clicking Next requests the next page, and Prev is disabled on page 1", async () => {
    // Next is only enabled when the current page is full (data.length >= 10) - a partial page
    // is the "no more results" signal - so page 0 needs a full page of 10 for the click to work.
    const fullPage = Array.from({ length: 10 }, (_, i) => ({ ...TRAINER, username: `coach${i}` }));
    mock.onGet("/api/page/allTrainers").reply((config) => [200, config.params.page === 0 ? fullPage : []]);

    renderWithProviders(<TrainersPage />);
    await waitFor(() => expect(screen.getByText("Page 1")).toBeInTheDocument());
    expect(screen.getByRole("button", { name: /Prev/ })).toBeDisabled();

    await userEvent.click(screen.getByRole("button", { name: /Next/ }));

    await waitFor(() => expect(screen.getByText("Page 2")).toBeInTheDocument());
  });

  it("expanding a trainer row loads and shows their specialties", async () => {
    mock.onGet("/api/page/allTrainers").reply(200, [TRAINER]);
    mock.onGet("/api/page/trainers/coach99/specialties").reply(200, [{ name: "Yoga", description: "d" }]);

    renderWithProviders(<TrainersPage />);
    await waitFor(() => expect(screen.getByText(/Ana M Lopez Diaz/)).toBeInTheDocument());

    await userEvent.click(screen.getByText(/\+ specialties/));

    await waitFor(() => expect(screen.getByText("Yoga")).toBeInTheDocument());
  });
});
