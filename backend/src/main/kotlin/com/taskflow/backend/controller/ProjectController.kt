package com.taskflow.backend.controller

import com.taskflow.backend.dto.*
import com.taskflow.backend.model.TaskPriority
import com.taskflow.backend.model.TaskStatus
import com.taskflow.backend.model.User
import com.taskflow.backend.service.ProjectService
import com.taskflow.backend.service.TaskService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val taskService: TaskService
) {
    @GetMapping
    fun getProjects(@AuthenticationPrincipal currentUser: User): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.getProjects(currentUser))
    }

    @PostMapping
    fun createProject(
        @Valid @RequestBody request: ProjectCreateRequest,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<ProjectDto> {
        val project = projectService.createProject(request, currentUser)
        return ResponseEntity.status(HttpStatus.CREATED).body(project)
    }

    @GetMapping("/{id}/summary")
    fun getSummary(
        @PathVariable id: Long,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<ProjectSummaryDto> {
        return ResponseEntity.ok(projectService.getProjectSummary(id, currentUser))
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable id: Long,
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) priority: TaskPriority?,
        @RequestParam(required = false) assigneeId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int,
        @RequestParam(defaultValue = "dueDate") sortBy: String,
        @RequestParam(defaultValue = "ASC") sortDir: String,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<Page<TaskDto>> {
        val sort = if (sortDir.equals("DESC", ignoreCase = true)) {
            Sort.by(sortBy).descending()
        } else {
            Sort.by(sortBy).ascending()
        }
        val pageable = PageRequest.of(page, size, sort)
        return ResponseEntity.ok(taskService.getTasks(id, status, priority, assigneeId, currentUser, pageable))
    }

    @PostMapping("/{id}/tasks")
    fun createTask(
        @PathVariable id: Long,
        @Valid @RequestBody request: TaskCreateRequest,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(id, request, currentUser))
    }
}
