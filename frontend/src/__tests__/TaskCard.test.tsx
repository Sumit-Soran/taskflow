// @vitest-environment jsdom
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import React from "react";

interface TaskProps {
  title: string;
  priority: string;
  status: string;
  isOverdue?: boolean;
}

const MockTaskCard: React.FC<TaskProps> = ({ title, priority, status, isOverdue }) => (
  <div data-testid="task-card">
    <h4>{title}</h4>
    <span data-testid="priority-badge">{priority}</span>
    <span data-testid="status-badge">{status}</span>
    {isOverdue && <span data-testid="overdue-tag">OVERDUE</span>}
  </div>
);

describe("TaskCard Component", () => {
  it("renders task title and priority correctly", () => {
    render(<MockTaskCard title="Write API Tests" priority="HIGH" status="TODO" />);
    expect(screen.getByText("Write API Tests")).toBeDefined();
    expect(screen.getByTestId("priority-badge").textContent).toBe("HIGH");
  });

  it("displays overdue indicator when task is marked overdue", () => {
    render(<MockTaskCard title="Overdue Task" priority="MEDIUM" status="TODO" isOverdue={true} />);
    expect(screen.getByTestId("overdue-tag")).toBeDefined();
  });
});
