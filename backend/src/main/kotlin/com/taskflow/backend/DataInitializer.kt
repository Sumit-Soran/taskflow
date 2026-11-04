package com.taskflow.backend

import com.taskflow.backend.model.*
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.findByEmail("demo@taskflow.com").isEmpty) {
            val demoUser = userRepository.save(
                User(
                    name = "Demo User",
                    email = "demo@taskflow.com",
                    password = passwordEncoder.encode("Password123!")
                )
            )

            val demoProject = projectRepository.save(
                Project(
                    name = "TaskFlow Showcase Project",
                    description = "Sample project pre-populated with tasks across all statuses and overdue checks.",
                    owner = demoUser
                )
            )

            taskRepository.save(
                Task(
                    title = "Setup Spring Boot Backend",
                    description = "Configure JWT security, Spring Data JPA, and REST endpoints.",
                    status = TaskStatus.DONE,
                    priority = TaskPriority.HIGH,
                    dueDate = LocalDate.now().minusDays(2),
                    assignee = demoUser,
                    project = demoProject
                )
            )

            taskRepository.save(
                Task(
                    title = "Integrate React Kanban Board",
                    description = "Implement column dragging, task filtering by priority, and summary stats.",
                    status = TaskStatus.IN_PROGRESS,
                    priority = TaskPriority.MEDIUM,
                    dueDate = LocalDate.now().plusDays(3),
                    assignee = demoUser,
                    project = demoProject
                )
            )

            taskRepository.save(
                Task(
                    title = "Fix Overdue Documentation Review",
                    description = "Review API payload shapes and overdue indicator logic.",
                    status = TaskStatus.TODO,
                    priority = TaskPriority.HIGH,
                    dueDate = LocalDate.now().minusDays(1),
                    assignee = demoUser,
                    project = demoProject
                )
            )
        }
    }
}
