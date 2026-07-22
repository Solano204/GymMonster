import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ClientLookup } from "@/components/clients/ClientLookup";

describe("ClientLookup", () => {
  it("calls onFound with the trimmed input value on submit", async () => {
    const onFound = vi.fn();
    render(<ClientLookup onFound={onFound} />);

    await userEvent.type(screen.getByLabelText("Username"), "  jdoe  ");
    await userEvent.click(screen.getByRole("button", { name: "Look up member" }));

    expect(onFound).toHaveBeenCalledWith("jdoe");
  });

  it("does not call onFound when the input is empty or only whitespace", async () => {
    const onFound = vi.fn();
    render(<ClientLookup onFound={onFound} />);

    await userEvent.type(screen.getByLabelText("Username"), "   ");
    await userEvent.click(screen.getByRole("button", { name: "Look up member" }));

    expect(onFound).not.toHaveBeenCalled();
  });
});
