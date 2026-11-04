package com.taskflow.backend.service

import com.taskflow.backend.dto.ProjectCreateRequest
import com.taskflow.backend.dto.ProjectDto
import com.taskflow.backend.dto.ProjectSummaryDto
import com.taskflow.backend.exception.AccessDeniedException
import com.taskflow.backend.exception.ResourceNotFoundException
import com.taskflow.backend.model.Project
import com.taskflow.backend.model.TaskStatus
import com.taskflow.backend.model.User
import com.taskflow.backend.repository.ProjectRepository
import com.taskflow.backend.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) {
    fun getProjects(currentUser: User): List<ProjectDto> {
        return projectRepository.findAllByOwner(currentUser).map { it.toDto() }
    }

    fun createProject(request: ProjectCreateRequest, currentUser: User): ProjectDto {
        val project = Project(
            name = request.name,
            description = request.description,
            owner = currentUser
        )
        return projectRepository.save(project).toDto()
    }

    fun getProjectByIdAndOwner(id: Long, currentUser: User): Project {
        val project = projectRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Project not found with id: $id") }

        if (project.owner.id != currentUser.id) {
            throw AccessDeniedException("Access denied: You do not own this project")
        }
        return project
    }

    fun getProjectSummary(projectId: Long, currentUser: User): ProjectSummaryDto {
        val project = getProjectByIdAndOwner(projectId, currentUser)
        val allTasks = taskRepository.findAllByProject(project)

        val todoCount = allTasks.count { it.status == TaskStatus.TODO }.toLong()
        val inProgressCount = allTasks.count { it.status == TaskStatus.IN_PROGRESS }.toLong()
        val doneCount = allTasks.count { it.status == TaskStatus.DONE }.toLong()
        val overdueCount = allTasks.count {
            it.status != TaskStatus.DONE && it.dueDate != null && it.dueDate!!.isBefore(LocalDate.now())
        }.toLong()

        return ProjectSummaryDto(
            totalTasks = allTasks.size.toLong(),
            todoCount = todoCount,
            inProgressCount = inProgressCount,
            doneCount = doneCount,
            overdueCount = overdueCount
        )
    }

    private fun Project.toDto() = ProjectDto(
        id = this.id!!,
        name = this.name,
        description = this.description,
        ownerId = this.owner.id!!,
        createdAt = this.createdAt
    )
}
