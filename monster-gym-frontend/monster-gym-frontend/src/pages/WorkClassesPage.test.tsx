import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { WorkClassesPage } from "@/pages/WorkClassesPage";
import { renderWithProviders } from "@/test/utils";

describe("WorkClassesPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
  });

  afterEach(() => {
    mock.restore();
  });

  it("shows a placeholder until a class is selected", async () => {
    mock.onGet("/api/page/workclasses").reply(200, [{ name: "Yoga", description: "d", duration: "60min" }]);

    renderWithProviders(<WorkClassesPage />);

    await waitFor(() => expect(screen.getByText("Yoga")).toBeInTheDocument());
    expect(screen.getByText("Pick a class to see its schedule and roster.")).toBeInTheDocument();
  });

  it("selecting a class shows its detail card with a default 'schedules' tab", async () => {
    mock.onGet("/api/page/workclasses").reply(200, [{ name: "Yoga", description: "d", duration: "60min" }]);
    mock.onGet("/api/page/workclasses/Yoga/schedules").reply(200, [{ day: "MONDAY", startTime: "08:00", endTime: "09:00" }]);

    renderWithProviders(<WorkClassesPage />);
    await waitFor(() => expect(screen.getByText("Yoga")).toBeInTheDocument());

    await userEvent.click(screen.getByRole("button", { name: /Yoga/ }));

    await waitFor(() => expect(screen.getByText("MONDAY")).toBeInTheDocument());
    expect(screen.getByText("08:00–09:00")).toBeInTheDocument();
  });

  it("switching to the clients tab loads the client roster", async () => {
    mock.onGet("/api/page/workclasses").reply(200, [{ name: "Yoga", description: "d", duration: "60min" }]);
    mock.onGet("/api/page/workclasses/Yoga/schedules").reply(200, []);
    mock.onGet("/api/page/workclasses/Yoga/clients").reply(200, [
      { name: "John", secondName: "Q", lastNameM: "Doe", lastNameP: "Public", age: "30" },
    ]);

    renderWithProviders(<WorkClassesPage />);
    await waitFor(() => expect(screen.getByText("Yoga")).toBeInTheDocument());
    await userEvent.click(screen.getByRole("button", { name: /Yoga/ }));
    await waitFor(() => expect(screen.getByText("No sessions on the calendar.")).toBeInTheDocument());

    await userEvent.click(screen.getByRole("button", { name: "clients" }));

    await waitFor(() => expect(screen.getByText(/John Q Public Doe/)).toBeInTheDocument());
  });

  it("shows an empty state when there are no work classes at all", async () => {
    mock.onGet("/api/page/workclasses").reply(200, []);

    renderWithProviders(<WorkClassesPage />);

    await waitFor(() => expect(screen.getByText("No classes scheduled.")).toBeInTheDocument());
  });
});
