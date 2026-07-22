import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MockAdapter from "axios-mock-adapter";
import { http } from "@/lib/http";
import { ClientRegisterForm } from "@/components/clients/ClientRegisterForm";
import { renderWithProviders } from "@/test/utils";

describe("ClientRegisterForm", () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(http);
    mock.onGet("/api/page/allMemberships").reply(200, [
      { membershipType: "GOLD", description: "d", hasCardio: true, hasPool: true, hasFoodCourt: true },
    ]);
  });

  afterEach(() => {
    mock.restore();
  });

  it("renders an input for every required registration field, and the membership dropdown", async () => {
    renderWithProviders(<ClientRegisterForm onRegistered={() => {}} />);

    for (const label of [
      "Username",
      "Password",
      "Email",
      "First name",
      "Second name",
      "Paternal last name",
      "Maternal last name",
      "Age",
      "Height (cm)",
      "Weight (kg)",
    ]) {
      expect(screen.getByText(label)).toBeInTheDocument();
    }
    await waitFor(() => expect(screen.getByRole("option", { name: "GOLD" })).toBeInTheDocument());
  });

  it("submits the filled-in form and calls onRegistered with the new username", async () => {
    mock.onPost("/api/page/registerClient").reply(201, { id: 1, username: "newuser" });
    let registeredUsername: string | undefined;

    renderWithProviders(<ClientRegisterForm onRegistered={(u) => { registeredUsername = u; }} />);

    // Every field's label text is unique, so walking from the label's text node to its
    // sibling input is a simpler, equally-valid alternative to getByLabelText here.
    const fillByLabel = async (label: string, value: string) => {
      const labelEl = screen.getByText(label);
      const input = labelEl.parentElement?.querySelector("input") as HTMLInputElement;
      await userEvent.type(input, value);
    };

    await fillByLabel("Username", "newuser");
    await fillByLabel("Password", "S3cur3P@ss!");
    await fillByLabel("Email", "newuser@test.com");
    await fillByLabel("First name", "John");
    await fillByLabel("Second name", "Q");
    await fillByLabel("Paternal last name", "Doe");
    await fillByLabel("Maternal last name", "Public");
    await fillByLabel("Age", "30");
    await fillByLabel("Height (cm)", "180");
    await fillByLabel("Weight (kg)", "80");

    await waitFor(() => expect(screen.getByRole("option", { name: "GOLD" })).toBeInTheDocument());
    await userEvent.selectOptions(screen.getByRole("combobox"), "GOLD");

    await userEvent.click(screen.getByRole("button", { name: "Register member" }));

    await waitFor(() => expect(registeredUsername).toBe("newuser"));
  });

  it("age/height/weight strip non-numeric characters as they're typed", async () => {
    renderWithProviders(<ClientRegisterForm onRegistered={() => {}} />);
    const fieldFor = (label: string) => {
      const labelEl = screen.getByText(label);
      return labelEl.parentElement?.querySelector("input") as HTMLInputElement;
    };

    await userEvent.type(fieldFor("Age"), "3abc0");
    await userEvent.type(fieldFor("Height (cm)"), "1a8b0c.d5e.f");

    expect(fieldFor("Age").value).toBe("30");
    expect(fieldFor("Height (cm)").value).toBe("180.5"); // only the first "." is kept
  });

  it("rejects registering a username that's already taken, without ever POSTing", async () => {
    mock.onGet("/api/page/clients/taken/allInformation").reply(200, { id: 1, username: "taken" });
    const postSpy = vi.fn();
    mock.onPost("/api/page/registerClient").reply((config) => {
      postSpy(config);
      return [201, { id: 1, username: "taken" }];
    });

    renderWithProviders(<ClientRegisterForm onRegistered={() => {}} />);
    const fillByLabel = async (label: string, value: string) => {
      const labelEl = screen.getByText(label);
      const input = labelEl.parentElement?.querySelector("input") as HTMLInputElement;
      await userEvent.type(input, value);
    };
    await fillByLabel("Username", "taken");
    await fillByLabel("Password", "S3cur3P@ss!");
    await fillByLabel("Email", "newuser@test.com");
    await fillByLabel("First name", "John");
    await fillByLabel("Second name", "Q");
    await fillByLabel("Paternal last name", "Doe");
    await fillByLabel("Maternal last name", "Public");
    await fillByLabel("Age", "30");
    await fillByLabel("Height (cm)", "180");
    await fillByLabel("Weight (kg)", "80");
    await waitFor(() => expect(screen.getByRole("option", { name: "GOLD" })).toBeInTheDocument());
    await userEvent.selectOptions(screen.getByRole("combobox"), "GOLD");

    await userEvent.click(screen.getByRole("button", { name: "Register member" }));

    await waitFor(() => expect(screen.getByText(/already taken/)).toBeInTheDocument());
    expect(postSpy).not.toHaveBeenCalled();
  });

  it("shows an error message when registration fails", async () => {
    mock.onPost("/api/page/registerClient").reply(400, { error: "Username already exists" });

    renderWithProviders(<ClientRegisterForm onRegistered={() => {}} />);

    const fillByLabel = async (label: string, value: string) => {
      const labelEl = screen.getByText(label);
      const input = labelEl.parentElement?.querySelector("input") as HTMLInputElement;
      await userEvent.type(input, value);
    };
    await fillByLabel("Username", "newuser");
    await fillByLabel("Password", "S3cur3P@ss!");
    await fillByLabel("Email", "newuser@test.com");
    await fillByLabel("First name", "John");
    await fillByLabel("Second name", "Q");
    await fillByLabel("Paternal last name", "Doe");
    await fillByLabel("Maternal last name", "Public");
    await fillByLabel("Age", "30");
    await fillByLabel("Height (cm)", "180");
    await fillByLabel("Weight (kg)", "80");
    await waitFor(() => expect(screen.getByRole("option", { name: "GOLD" })).toBeInTheDocument());
    await userEvent.selectOptions(screen.getByRole("combobox"), "GOLD");

    await userEvent.click(screen.getByRole("button", { name: "Register member" }));

    await waitFor(() => expect(screen.getByText(/Username already exists/)).toBeInTheDocument());
  });
});
