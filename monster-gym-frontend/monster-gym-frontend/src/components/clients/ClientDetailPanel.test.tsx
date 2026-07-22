import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { ClientDetailPanel } from "@/components/clients/ClientDetailPanel";
import { renderWithProviders } from "@/test/utils";

const CLIENT_INFO = {
  id: 1,
  username: "jdoe",
  email: "jdoe@test.com",
  name: "John",
  secondname: "Q",
  lastnamep: "Doe",
  lastnamem: "Public",
  age: "30",
  height: "180",
  weight: "80",
  membershiptype: "GOLD",
  trainername: "coach99",
};

describe("ClientDetailPanel", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
    mock.onGet("/api/page/allMemberships").reply(200, [
      { membershipType: "GOLD", description: "d", hasCardio: true, hasPool: true, hasFoodCourt: true },
      { membershipType: "SILVER", description: "d", hasCardio: true, hasPool: false, hasFoodCourt: false },
    ]);
  });

  afterEach(() => {
    mock.restore();
  });

  it("shows a loading state before the client info resolves", () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(() => new Promise(() => {}));
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);

    renderWithProviders(<ClientDetailPanel username="jdoe" />);

    expect(screen.getByText(/Looking up member/)).toBeInTheDocument();
  });

  it("shows an error state when the lookup fails (e.g. member not found)", async () => {
    mock.onGet("/api/page/clients/ghost/allInformation").reply(404, { error: "Client not found" });
    mock.onGet("/api/page/clients/ghost/allClass").reply(200, []);

    renderWithProviders(<ClientDetailPanel username="ghost" />);

    await waitFor(() => expect(screen.getByText(/No member found for "ghost"/)).toBeInTheDocument());
  });

  it("renders the client's profile, classes, and membership once loaded", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, CLIENT_INFO);
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, [
      { name: "Yoga", description: "d", duration: "60min" },
    ]);

    renderWithProviders(<ClientDetailPanel username="jdoe" />);

    await waitFor(() => expect(screen.getByText(/John Q Doe Public/)).toBeInTheDocument());
    expect(screen.getByText("@jdoe · jdoe@test.com")).toBeInTheDocument();
    expect(screen.getByText("Yoga")).toBeInTheDocument();
    expect(screen.getByText("GOLD")).toBeInTheDocument();
    expect(screen.getByText("coach99")).toBeInTheDocument();
  });

  it("shows 'Not enrolled in any classes' when the class list is empty", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, CLIENT_INFO);
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);

    renderWithProviders(<ClientDetailPanel username="jdoe" />);

    await waitFor(() => expect(screen.getByText("Not enrolled in any classes.")).toBeInTheDocument());
  });

  it("the edit-profile age/height/weight fields strip non-numeric input as it's typed, other fields don't", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, CLIENT_INFO);
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);

    renderWithProviders(<ClientDetailPanel username="jdoe" />);
    await waitFor(() => expect(screen.getByText(/John Q Doe Public/)).toBeInTheDocument());

    const fieldFor = (key: string) => {
      const labelEl = screen.getByText(key);
      return labelEl.parentElement?.querySelector("input") as HTMLInputElement;
    };

    await userEvent.clear(fieldFor("age"));
    await userEvent.type(fieldFor("age"), "4a0!");
    await userEvent.clear(fieldFor("height"));
    await userEvent.type(fieldFor("height"), "1a7b5c.d5e.f");
    await userEvent.clear(fieldFor("name"));
    await userEvent.type(fieldFor("name"), "Jane99");

    expect(fieldFor("age").value).toBe("40");
    expect(fieldFor("height").value).toBe("175.5");
    expect(fieldFor("name").value).toBe("Jane99"); // free text field - untouched
  });

  it("switching membership calls the update endpoint with the selected tier", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, CLIENT_INFO);
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);
    mock.onPut("/api/page/clients/jdoe/SILVER/changeMembership").reply(200, "Membership changed");

    renderWithProviders(<ClientDetailPanel username="jdoe" />);
    await waitFor(() => expect(screen.getByText("GOLD")).toBeInTheDocument());
    // The membership select's options come from a separate query (useMemberships) than the
    // client info used above - wait for it to resolve too before interacting with the select.
    await waitFor(() => expect(screen.getByRole("option", { name: "SILVER" })).toBeInTheDocument());

    const selects = screen.getAllByRole("combobox");
    await userEvent.selectOptions(selects[0], "SILVER");
    await userEvent.click(screen.getByRole("button", { name: "Switch" }));

    await waitFor(() => expect(mock.history.put.some((r) => r.url === "/api/page/clients/jdoe/SILVER/changeMembership")).toBe(true));
  });

  it("deleting the account is disabled until a confirmation password is entered", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, CLIENT_INFO);
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);

    renderWithProviders(<ClientDetailPanel username="jdoe" />);
    await waitFor(() => expect(screen.getByText("GOLD")).toBeInTheDocument());

    const deleteButton = screen.getByRole("button", { name: "Delete account" });
    expect(deleteButton).toBeDisabled();

    await userEvent.type(screen.getByPlaceholderText("confirm password"), "s3cret");
    expect(deleteButton).toBeEnabled();
  });
});
