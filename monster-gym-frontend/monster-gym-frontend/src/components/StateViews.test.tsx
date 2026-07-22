import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { LoadingBlock, ErrorBlock, EmptyBlock } from "@/components/StateViews";

describe("LoadingBlock", () => {
  it("renders the default label", () => {
    render(<LoadingBlock />);
    expect(screen.getByText("Loading…")).toBeInTheDocument();
    expect(screen.getByRole("status")).toBeInTheDocument();
  });

  it("renders a custom label", () => {
    render(<LoadingBlock label="Loading trainers" />);
    expect(screen.getByText("Loading trainers…")).toBeInTheDocument();
  });
});

describe("ErrorBlock", () => {
  it("renders the given message alongside the fixed lead-in text", () => {
    render(<ErrorBlock message="Network unreachable" />);
    expect(screen.getByText(/Couldn't load this\./)).toBeInTheDocument();
    expect(screen.getByText(/Network unreachable/)).toBeInTheDocument();
  });
});

describe("EmptyBlock", () => {
  it("renders its children", () => {
    render(<EmptyBlock>No records yet.</EmptyBlock>);
    expect(screen.getByText("No records yet.")).toBeInTheDocument();
  });
});
