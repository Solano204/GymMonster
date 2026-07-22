import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { ClientsPage } from "@/pages/ClientsPage";
import { renderWithProviders } from "@/test/utils";

describe("ClientsPage", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
    mock.onGet("/api/page/allMemberships").reply(200, []);
  });

  afterEach(() => {
    mock.restore();
  });

  it("shows the placeholder card until a member is searched for", () => {
    renderWithProviders(<ClientsPage />);

    expect(
      screen.getByText(/Search for a member on the left, or register a new one/),
    ).toBeInTheDocument();
  });

  it("searching for a username shows that member's detail panel", async () => {
    mock.onGet("/api/page/clients/jdoe/allInformation").reply(200, { id: 1, username: "jdoe", email: "jdoe@test.com" });
    mock.onGet("/api/page/clients/jdoe/allClass").reply(200, []);

    renderWithProviders(<ClientsPage />);

    await userEvent.type(screen.getByLabelText("Username"), "jdoe");
    await userEvent.click(screen.getByRole("button", { name: "Look up member" }));

    await waitFor(() => expect(screen.getByText("@jdoe · jdoe@test.com")).toBeInTheDocument());
  });

  it("toggling '+ New member' reveals and hides the registration form", async () => {
    renderWithProviders(<ClientsPage />);

    expect(screen.queryByText("Register member")).not.toBeInTheDocument();

    await userEvent.click(screen.getByRole("button", { name: "+ New member" }));
    expect(screen.getByRole("button", { name: "Register member" })).toBeInTheDocument();

    await userEvent.click(screen.getByRole("button", { name: "Close" }));
    expect(screen.queryByText("Register member")).not.toBeInTheDocument();
  });

  it("successful registration closes the form and shows the new member's panel", async () => {
    // Overrides the file-level empty-list mock: the membership <select> is a required field,
    // so it needs a real option to select before the form can actually submit.
    mock.onGet("/api/page/allMemberships").reply(200, [
      { membershipType: "GOLD", description: "d", hasCardio: true, hasPool: true, hasFoodCourt: true },
    ]);
    mock.onPost("/api/page/registerClient").reply(201, { id: 1, username: "newuser" });
    // First allInformation call is useRegisterClient's own pre-registration
    // availability check (404 = username free) - subsequent calls are the
    // page fetching the newly-created member's profile to display it.
    mock.onGet("/api/page/clients/newuser/allInformation").replyOnce(404, { error: "Client not found" });
    mock.onGet("/api/page/clients/newuser/allInformation").reply(200, { id: 1, username: "newuser", email: "n@test.com" });
    mock.onGet("/api/page/clients/newuser/allClass").reply(200, []);

    renderWithProviders(<ClientsPage />);
    await userEvent.click(screen.getByRole("button", { name: "+ New member" }));

    // Scoped to the register form: ClientLookup (always rendered alongside it) has its own
    // "Username" label and its own <form>, so an unscoped query - or even just the first
    // <form> in document order - would match the wrong one.
    const form = screen.getByRole("button", { name: "Register member" }).closest("form") as HTMLFormElement;
    const fillByLabel = async (label: string, value: string) => {
      const labelEl = within(form).getByText(label);
      const input = labelEl.parentElement?.querySelector("input") as HTMLInputElement;
      await userEvent.type(input, value);
    };
    await fillByLabel("Username", "newuser");
    await fillByLabel("Password", "S3cur3P@ss!");
    await fillByLabel("Email", "n@test.com");
    await fillByLabel("First name", "New");
    await fillByLabel("Second name", "U");
    await fillByLabel("Paternal last name", "Ser");
    await fillByLabel("Maternal last name", "Name");
    await fillByLabel("Age", "25");
    await fillByLabel("Height (cm)", "170");
    await fillByLabel("Weight (kg)", "70");

    await waitFor(() => expect(within(form).getByRole("option", { name: "GOLD" })).toBeInTheDocument());
    await userEvent.selectOptions(within(form).getByRole("combobox"), "GOLD");

    await userEvent.click(screen.getByRole("button", { name: "Register member" }));

    await waitFor(() => expect(screen.getByText("@newuser · n@test.com")).toBeInTheDocument());
    expect(screen.queryByRole("button", { name: "Register member" })).not.toBeInTheDocument();
  });
});
