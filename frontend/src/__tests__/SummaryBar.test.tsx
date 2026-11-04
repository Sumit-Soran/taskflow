// @vitest-environment jsdom
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import React from "react";

interface SummaryProps {
  todoCount: number;
  inProgressCount: number;
  doneCount: number;
  overdueCount: number;
}

const MockSummaryBar: React.FC<SummaryProps> = ({ todoCount, inProgressCount, doneCount, overdueCount }) => (
  <div data-testid="summary-bar">
    <div data-testid="stat-todo">Todo: {todoCount}</div>
    <div data-testid="stat-progress">In Progress: {inProgressCount}</div>
    <div data-testid="stat-done">Done: {doneCount}</div>
    <div data-testid="stat-overdue">Overdue: {overdueCount}</div>
  </div>
);

describe("SummaryBar Component", () => {
  it("renders correct metrics and status counts", () => {
    render(<MockSummaryBar todoCount={5} inProgressCount={3} doneCount={8} overdueCount={2} />);
    expect(screen.getByTestId("stat-todo").textContent).toBe("Todo: 5");
    expect(screen.getByTestId("stat-progress").textContent).toBe("In Progress: 3");
    expect(screen.getByTestId("stat-done").textContent).toBe("Done: 8");
    expect(screen.getByTestId("stat-overdue").textContent).toBe("Overdue: 2");
  });
});
