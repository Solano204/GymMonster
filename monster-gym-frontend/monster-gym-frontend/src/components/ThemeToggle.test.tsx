import { describe, it, expect, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { ThemeProvider } from "@/lib/theme";
import { ThemeToggle } from "@/components/ThemeToggle";

beforeEach(() => {
  window.localStorage.clear();
  document.documentElement.classList.remove("dark", "light");
});

describe("ThemeToggle", () => {
  it("shows a sun icon (switch-to-light affordance) while in dark mode, moon while in light", () => {
    render(
      <ThemeProvider>
        <ThemeToggle />
      </ThemeProvider>,
    );
    expect(screen.getByRole("button")).toHaveTextContent("☀️");

    fireEvent.click(screen.getByRole("button"));

    expect(screen.getByRole("button")).toHaveTextContent("🌙");
  });
});
