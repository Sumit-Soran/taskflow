package com.taskflow.backend

import com.taskflow.backend.model.*
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TaskServiceTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Test
    fun `test seed data is loaded correctly`() {
        val user = userRepository.findByEmail("demo@taskflow.com")
        assert(user.isPresent) { "Seed demo user must exist" }

        val projects = projectRepository.findByOwnerId(user.get().id!!)
        assert(projects.isNotEmpty()) { "Demo project should be assigned to seed user" }

        val tasks = taskRepository.findByProjectId(projects[0].id!)
        assertEquals(3, tasks.size, "Should have 3 pre-seeded tasks")
    }
}
