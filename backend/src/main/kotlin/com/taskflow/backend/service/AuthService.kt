package com.taskflow.backend.service

import com.taskflow.backend.dto.AuthResponse
import com.taskflow.backend.dto.LoginRequest
import com.taskflow.backend.dto.RegisterRequest
import com.taskflow.backend.exception.BadRequestException
import com.taskflow.backend.model.User
import com.taskflow.backend.repository.UserRepository
import com.taskflow.backend.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw BadRequestException("Email already registered")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password)
        )
        val savedUser = userRepository.save(user)
        val token = jwtTokenProvider.generateToken(savedUser.email)

        return AuthResponse(
            token = token,
            userId = savedUser.id!!,
            name = savedUser.name,
            email = savedUser.email
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { BadRequestException("Invalid email or password") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadRequestException("Invalid email or password")
        }

        val token = jwtTokenProvider.generateToken(user.email)

        return AuthResponse(
            token = token,
            userId = user.id!!,
            name = user.name,
            email = user.email
        )
    }
}
