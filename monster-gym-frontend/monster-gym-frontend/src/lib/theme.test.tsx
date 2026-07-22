import { describe, it, expect, beforeEach, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { ThemeProvider, useTheme } from "@/lib/theme";

function Probe() {
  const { theme, setTheme } = useTheme();
  return (
    <button onClick={() => setTheme(theme === "dark" ? "light" : "dark")}>{theme}</button>
  );
}

beforeEach(() => {
  window.localStorage.clear();
  document.documentElement.classList.remove("dark", "light");
});

describe("ThemeProvider / useTheme", () => {
  it("defaults to dark - this app's branded identity - when nothing is stored", () => {
    render(
      <ThemeProvider>
        <Probe />
      </ThemeProvider>,
    );
    expect(screen.getByText("dark")).toBeInTheDocument();
    expect(document.documentElement.classList.contains("dark")).toBe(true);
  });

  it("restores a previously chosen theme from localStorage", () => {
    window.localStorage.setItem("monster-gym-theme", "light");

    render(
      <ThemeProvider>
        <Probe />
      </ThemeProvider>,
    );

    expect(screen.getByText("light")).toBeInTheDocument();
    expect(document.documentElement.classList.contains("light")).toBe(true);
  });

  it("switching theme updates the <html> class and persists the choice", () => {
    render(
      <ThemeProvider>
        <Probe />
      </ThemeProvider>,
    );

    fireEvent.click(screen.getByText("dark"));

    expect(screen.getByText("light")).toBeInTheDocument();
    expect(document.documentElement.classList.contains("light")).toBe(true);
    expect(document.documentElement.classList.contains("dark")).toBe(false);
    expect(window.localStorage.getItem("monster-gym-theme")).toBe("light");
  });

  it("useTheme throws outside a ThemeProvider - fails loudly instead of silently no-op", () => {
    const spy = vi.spyOn(console, "error").mockImplementation(() => {});
    expect(() => render(<Probe />)).toThrow("useTheme must be used within a ThemeProvider");
    spy.mockRestore();
  });
});
