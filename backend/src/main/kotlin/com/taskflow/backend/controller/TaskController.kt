package com.taskflow.backend.controller

import com.taskflow.backend.dto.TaskDto
import com.taskflow.backend.dto.TaskStatusUpdateRequest
import com.taskflow.backend.dto.TaskUpdateRequest
import com.taskflow.backend.model.User
import com.taskflow.backend.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PutMapping("/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody request: TaskUpdateRequest,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.updateTask(id, request, currentUser))
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: TaskStatusUpdateRequest,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request.status, currentUser))
    }

    @DeleteMapping("/{id}")
    fun deleteTask(
        @PathVariable id: Long,
        @AuthenticationPrincipal currentUser: User
    ): ResponseEntity<Void> {
        taskService.deleteTask(id, currentUser)
        return ResponseEntity.noContent().build()
    }
}
