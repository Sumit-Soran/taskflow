package com.taskflow.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ProjectCreateRequest(
    @field:NotBlank(message = "Project name is required")
    @field:Size(max = 100, message = "Project name cannot exceed 100 characters")
    val name: String,
    val description: String? = null
)

data class ProjectDto(
    val id: Long,
    val name: String,
    val description: String?,
    val ownerId: Long,
    val createdAt: LocalDateTime
)

data class ProjectSummaryDto(
    val totalTasks: Long,
    val todoCount: Long,
    val inProgressCount: Long,
    val doneCount: Long,
    val overdueCount: Long
)
