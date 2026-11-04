package com.taskflow.backend.repository

import com.taskflow.backend.model.Project
import com.taskflow.backend.model.Task
import com.taskflow.backend.model.TaskPriority
import com.taskflow.backend.model.TaskStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<Task, Long> {
    fun findAllByProject(project: Project, pageable: Pageable): Page<Task>
    fun countByProjectAndStatus(project: Project, status: TaskStatus): Long
    fun findAllByProject(project: Project): List<Task>
}
