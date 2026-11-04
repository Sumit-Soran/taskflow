package com.taskflow.backend

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskflow.backend.dto.AuthRequest
import com.taskflow.backend.dto.RegisterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `contextLoads and verifies authentication flow`() {
        // 1. Verify seed user login succeeds and returns JWT
        val loginRequest = AuthRequest(
            email = "demo@taskflow.com",
            password = "Password123!"
        )

        val result = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { exists() }
        }.andReturn()

        val token = objectMapper.readTree(result.response.contentAsString).get("token").asText()

        // 2. Fetch projects using the JWT
        mockMvc.get("/api/projects") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
        }
    }
}
