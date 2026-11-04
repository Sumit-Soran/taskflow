# TaskFlow - Full-Stack Task & Project Management System

TaskFlow is a robust, production-grade project and task tracking application built with a **Spring Boot 3 (Kotlin)** REST API backend and a **React 18 (TypeScript + Vite)** frontend.

---

## 🌟 Features & Highlights

- **JWT Authentication & Security:** Secure token-based access with ownership isolation (users only access their own projects and associated tasks).
- **Interactive Kanban Board:** Drag-and-drop / status progression (`TODO`, `IN_PROGRESS`, `DONE`) with real-time UI updates.
- **Dynamic Task Filters & Overdue Highlights:** Filter by priority, assignee, and sort by due date with visual overdue warnings.
- **Project Summary Dashboard:** Instant status counts and overdue metrics per project.
- **SQL Logging & Pre-configured H2 Console:** Formatted SQL query logs enabled for database transparency.
- **Automated Seed Data:** Automatically populates demo users, projects, and active/overdue tasks upon application start.

---

## 🛠️ Tech Stack

- **Backend:** Kotlin, Spring Boot 3.2.5, Spring Security, JWT (`jjwt 0.11.5`), Spring Data JPA, H2 Database, Jakarta Validation
- **Frontend:** React 18, Vite, TypeScript, Axios, Lucide React Icons, Vitest, React Testing Library
- **Database:** H2 In-Memory Database (with formatted SQL statement logging)

---

## 🏗️ Architecture & Design Patterns

1. **Layered Architecture:** Strict separation of concerns: `Controller` → `Service` → `Repository`.
2. **Repository Pattern:** Encapsulates data access logic using Spring Data JPA abstractions for domain aggregates.
3. **Data Transfer Object (DTO) Pattern:** Decouples API contract representations from database entities to prevent over-posting and protect internal schema.
4. **Global Exception Handling:** Centralized `@RestControllerAdvice` delivering standardized JSON error structures with field-level validation constraints.
5. **Strategy & Filter Pattern:** Dynamic predicate evaluation on tasks for filtering by status, priority, and overdue dates.

### State Management Choice
We chose standard **React Context + Hooks** with centralized Axios API clients. This avoids unnecessary external boilerplate (such as heavy Redux setup) while maintaining predictable state, route isolation, and responsive UI updates across the Kanban board.

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 17+
- Node.js 18+ and npm

### 1. Pre-seeded Demo Credentials
Upon running the backend, default data is populated automatically:
- **Email:** `demo@taskflow.com`
- **Password:** `Password123!`

---

### 2. Run the Backend

```bash
cd backend
./gradlew bootRun