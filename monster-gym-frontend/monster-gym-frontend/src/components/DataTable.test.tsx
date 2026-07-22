import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { DataTable, type Column } from "@/components/DataTable";

interface Row {
  id: number;
  name: string;
}

const columns: Column<Row>[] = [
  { header: "ID", cell: (r) => r.id, mono: true },
  { header: "Name", cell: (r) => r.name },
];

describe("DataTable", () => {
  it("renders one header cell per column", () => {
    render(<DataTable columns={columns} rows={[]} keyFor={(r) => r.id} />);
    expect(screen.getByText("ID")).toBeInTheDocument();
    expect(screen.getByText("Name")).toBeInTheDocument();
  });

  it("renders one row per item, with each column's cell function applied", () => {
    const rows: Row[] = [{ id: 1, name: "Olympic Pool" }, { id: 2, name: "Kids Pool" }];
    render(<DataTable columns={columns} rows={rows} keyFor={(r) => r.id} />);

    expect(screen.getByText("Olympic Pool")).toBeInTheDocument();
    expect(screen.getByText("Kids Pool")).toBeInTheDocument();
    expect(screen.getAllByRole("row")).toHaveLength(3); // 1 header + 2 data rows
  });

  it("renders no data rows for an empty array", () => {
    render(<DataTable columns={columns} rows={[]} keyFor={(r) => r.id} />);
    expect(screen.getAllByRole("row")).toHaveLength(1); // header only
  });
});
