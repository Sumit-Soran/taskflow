package com.taskflow.backend.service

import com.taskflow.backend.dto.*
import com.taskflow.backend.exception.BadRequestException
import com.taskflow.backend.exception.ResourceNotFoundException
import com.taskflow.backend.model.Task
import com.taskflow.backend.model.TaskPriority
import com.taskflow.backend.model.TaskStatus
import com.taskflow.backend.model.User
import com.taskflow.backend.repository.TaskRepository
import com.taskflow.backend.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val projectService: ProjectService,
    private val userRepository: UserRepository
) {
    fun getTasks(
        projectId: Long,
        status: TaskStatus?,
        priority: TaskPriority?,
        assigneeId: Long?,
        currentUser: User,
        pageable: Pageable
    ): Page<TaskDto> {
        val project = projectService.getProjectByIdAndOwner(projectId, currentUser)
        var tasks = taskRepository.findAllByProject(project)

        if (status != null) {
            tasks = tasks.filter { it.status == status }
        }
        if (priority != null) {
            tasks = tasks.filter { it.priority == priority }
        }
        if (assigneeId != null) {
            tasks = tasks.filter { it.assignee?.id == assigneeId }
        }

        val start = pageable.offset.toInt()
        val end = minOf((start + pageable.pageSize), tasks.size)
        val pagedList = if (start <= tasks.size) tasks.subList(start, end) else emptyList()

        return PageImpl(pagedList.map { it.toDto() }, pageable, tasks.size.toLong())
    }

    fun createTask(projectId: Long, request: TaskCreateRequest, currentUser: User): TaskDto {
        val project = projectService.getProjectByIdAndOwner(projectId, currentUser)

        if (request.dueDate != null && request.dueDate.isBefore(LocalDate.now())) {
            throw BadRequestException("Due date cannot be in the past")
        }

        val assignee = request.assigneeId?.let {
            userRepository.findById(it).orElseThrow { ResourceNotFoundException("Assignee user not found") }
        }

        val task = Task(
            title = request.title,
            description = request.description,
            priority = request.priority,
            dueDate = request.dueDate,
            assignee = assignee,
            project = project
        )

        return taskRepository.save(task).toDto()
    }

    fun updateTask(taskId: Long, request: TaskUpdateRequest, currentUser: User): TaskDto {
        val task = getTaskAndVerifyOwnership(taskId, currentUser)

        val assignee = request.assigneeId?.let {
            userRepository.findById(it).orElseThrow { ResourceNotFoundException("Assignee user not found") }
        }

        task.title = request.title
        task.description = request.description
        task.priority = request.priority
        task.dueDate = request.dueDate
        task.assignee = assignee
        task.updatedAt = LocalDateTime.now()

        return taskRepository.save(task).toDto()
    }

    fun updateTaskStatus(taskId: Long, newStatus: TaskStatus, currentUser: User): TaskDto {
        val task = getTaskAndVerifyOwnership(taskId, currentUser)
        task.status = newStatus
        task.updatedAt = LocalDateTime.now()
        return taskRepository.save(task).toDto()
    }

    fun deleteTask(taskId: Long, currentUser: User) {
        val task = getTaskAndVerifyOwnership(taskId, currentUser)
        taskRepository.delete(task)
    }

    private fun getTaskAndVerifyOwnership(taskId: Long, currentUser: User): Task {
        val task = taskRepository.findById(taskId)
            .orElseThrow { ResourceNotFoundException("Task not found with id: $taskId") }

        projectService.getProjectByIdAndOwner(task.project.id!!, currentUser)
        return task
    }

    private fun Task.toDto() = TaskDto(
        id = this.id!!,
        title = this.title,
        description = this.description,
        status = this.status,
        priority = this.priority,
        dueDate = this.dueDate,
        assignee = this.assignee?.let { UserSummaryDto(it.id!!, it.name, it.email) },
        projectId = this.project.id!!,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
