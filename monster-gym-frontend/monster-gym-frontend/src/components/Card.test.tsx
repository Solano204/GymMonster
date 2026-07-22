import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Card, PageHeader } from "@/components/Card";

describe("Card", () => {
  it("renders children", () => {
    render(<Card>Hello world</Card>);
    expect(screen.getByText("Hello world")).toBeInTheDocument();
  });

  it("renders the tab label when provided", () => {
    render(<Card tab="Edit profile">content</Card>);
    expect(screen.getByText("Edit profile")).toBeInTheDocument();
  });

  it("omits the tab element entirely when no tab prop is given", () => {
    const { container } = render(<Card>content</Card>);
    expect(container.querySelector(".plate-tab")).toBeNull();
  });

  it("appends the extra className onto the root element", () => {
    const { container } = render(<Card className="border-red-500">content</Card>);
    expect(container.firstElementChild).toHaveClass("border-red-500");
  });
});

describe("PageHeader", () => {
  it("renders eyebrow and title always", () => {
    render(<PageHeader eyebrow="Roster" title="Members" />);
    expect(screen.getByText("Roster")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Members" })).toBeInTheDocument();
  });

  it("renders the description only when provided", () => {
    const { rerender } = render(<PageHeader eyebrow="Roster" title="Members" description="A subtitle" />);
    expect(screen.getByText("A subtitle")).toBeInTheDocument();

    rerender(<PageHeader eyebrow="Roster" title="Members" />);
    expect(screen.queryByText("A subtitle")).not.toBeInTheDocument();
  });

  it("renders the action element when provided", () => {
    render(<PageHeader eyebrow="Roster" title="Members" action={<button>+ New</button>} />);
    expect(screen.getByRole("button", { name: "+ New" })).toBeInTheDocument();
  });
});
