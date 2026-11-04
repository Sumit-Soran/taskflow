package com.taskflow.backend

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
    }

    @Test
    fun shouldAuthenticateDemoUserSuccessfully() {
        val payload = """
            {
                "email": "demo@taskflow.com",
                "password": "Password123!"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        ).andExpect(status().isOk)
    }
}