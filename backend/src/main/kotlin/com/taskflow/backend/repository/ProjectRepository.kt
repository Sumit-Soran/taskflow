package com.taskflow.backend.repository

import com.taskflow.backend.model.Project
import com.taskflow.backend.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByOwner(owner: User): List<Project>
}
