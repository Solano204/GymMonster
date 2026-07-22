import { useTheme } from "@/lib/theme";

export function ThemeToggle() {
  const { theme, setTheme } = useTheme();

  return (
    <button
      type="button"
      onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
      aria-label="Toggle light/dark theme"
      title="Toggle theme"
      className="focus-ring fixed bottom-5 left-5 z-50 flex h-11 w-11 items-center justify-center rounded-full border border-[var(--color-border)] bg-[var(--color-surface-raised)] text-lg shadow-lg transition-transform hover:scale-105"
    >
      {theme === "dark" ? "☀️" : "🌙"}
    </button>
  );
}
