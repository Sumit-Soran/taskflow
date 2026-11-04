# TaskFlow - Full-Stack Task & Project Management System

TaskFlow is a production-ready project and task management system built using Spring Boot 3 (Kotlin) and React (Vite + TypeScript).

## 🚀 Tech Stack

- **Backend**: Kotlin, Spring Boot 3.2.5, Spring Security, JWT (jjwt 0.11.5), Spring Data JPA, H2 Database, Jakarta Validation
- **Frontend**: React 18, Vite, TypeScript, Axios, Lucide React Icons
- **Design Patterns & Architecture**:
  - Layered Architecture (Controller -> Service -> Repository)[cite: 1]
  - Data Transfer Object (DTO) Pattern[cite: 1]
  - Global Exception Handling Pattern (@RestControllerAdvice)[cite: 1]
  - Stateless JWT Authentication via Filter Chain[cite: 1]

---

## 🛠 Features

1. **Authentication & Security**:
   - Stateless JWT authentication with BCrypt password hashing[cite: 1].
   - Custom JwtAuthenticationFilter with Bearer token resolution[cite: 1].
   - Frontend Axios interceptor automatically attaches authorization token[cite: 1].

2. **Project Isolation**:
   - Multi-tenant data segregation (users only view and manage their own projects and associated tasks)[cite: 1].
   - Real-time project summary metrics (Total, To-Do, In-Progress, Completed, Overdue)[cite: 1].

3. **Interactive Kanban Board**:
   - 3-column workflow: TODO, IN_PROGRESS, and DONE[cite: 1].
   - Task prioritization (LOW, MEDIUM, HIGH) with visual status tags[cite: 1].
   - Due date validation enforcing future deadlines and flagging overdue tasks[cite: 1].
   - Instant search and priority filter controls[cite: 1].

---

## 💻 Local Setup & Run

### Prerequisites
- Java 17+
- Node.js 18+

### 1. Run Backend
```bash
cd backend
./gradlew bootRun