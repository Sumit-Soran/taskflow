package com.taskflow.backend.dto

import com.taskflow.backend.model.TaskPriority
import com.taskflow.backend.model.TaskStatus
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskCreateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 120, message = "Title cannot exceed 120 characters")
    val title: String,

    val description: String? = null,

    val priority: TaskPriority = TaskPriority.MEDIUM,

    @field:FutureOrPresent(message = "Due date cannot be in the past")
    val dueDate: LocalDate? = null,

    val assigneeId: Long? = null
)

data class TaskUpdateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 120, message = "Title cannot exceed 120 characters")
    val title: String,

    val description: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: LocalDate? = null,
    val assigneeId: Long? = null
)

data class TaskStatusUpdateRequest(
    @field:NotNull(message = "Status is required")
    val status: TaskStatus
)

data class TaskDto(
    val id: Long,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: LocalDate?,
    val assignee: UserSummaryDto?,
    val projectId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
