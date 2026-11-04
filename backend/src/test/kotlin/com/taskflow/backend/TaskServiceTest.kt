package com.taskflow.backend

import com.taskflow.backend.dto.TaskCreateRequest
import com.taskflow.backend.exception.BadRequestException
import com.taskflow.backend.model.Project
import com.taskflow.backend.model.Task
import com.taskflow.backend.model.TaskPriority
import com.taskflow.backend.model.TaskStatus
import com.taskflow.backend.model.User
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import com.taskflow.backend.service.ProjectService
import com.taskflow.backend.service.TaskService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class TaskServiceTest {

    @Mock
    private lateinit var taskRepository: TaskRepository

    @Mock
    private lateinit var projectService: ProjectService

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var taskService: TaskService

    private val dummyUser = User(
        id = 1L,
        name = "Demo User",
        email = "demo@taskflow.com",
        password = "Password123!"
    )

    private val dummyProject = Project(
        id = 1L,
        name = "Demo Project",
        description = "Project for testing",
        owner = dummyUser
    )

    @Test
    fun shouldReturnTasksForProject() {
        val dummyTask = Task(
            id = 1L,
            title = "Test Task",
            description = "Testing service logic",
            status = TaskStatus.TODO,
            priority = TaskPriority.MEDIUM,
            dueDate = LocalDate.now().plusDays(2),
            project = dummyProject,
            assignee = dummyUser
        )

        `when`(projectService.getProjectByIdAndOwner(1L, dummyUser)).thenReturn(dummyProject)
        `when`(taskRepository.findAllByProject(dummyProject)).thenReturn(listOf(dummyTask))

        val result = taskService.getTasks(
            projectId = 1L,
            status = null,
            priority = null,
            assigneeId = null,
            currentUser = dummyUser,
            pageable = PageRequest.of(0, 10)
        )

        assertEquals(1, result.totalElements)
        assertEquals("Test Task", result.content[0].title)
    }

    @Test
    fun shouldThrowBadRequestWhenDueDateIsInThePast() {
        val request = TaskCreateRequest(
            title = "Past Due Task",
            description = "Testing invalid date",
            priority = TaskPriority.HIGH,
            dueDate = LocalDate.now().minusDays(1),
            assigneeId = null
        )

        `when`(projectService.getProjectByIdAndOwner(1L, dummyUser)).thenReturn(dummyProject)

        assertThrows(BadRequestException::class.java) {
            taskService.createTask(1L, request, dummyUser)
        }
    }
}