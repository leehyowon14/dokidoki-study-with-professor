package com.animalleague.april.integration.auth;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.auth.infrastructure.UserRepository;
import com.animalleague.april.integration.support.PostgresIntegrationTest;

@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.flyway.enabled=true")
class AuthFlowIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void signupThenLoginReturnsUserAndNullActiveSession() throws Exception {
        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "홍길동",
                          "loginId": "hong1234",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.user.name").value("홍길동"))
            .andExpect(jsonPath("$.user.loginId").value("hong1234"))
            .andExpect(jsonPath("$.user.examEndDate").value("2026-06-20"));

        assertThat(userRepository.findByLoginId("hong1234")).isPresent();

        MvcResult loginResult = mockMvc.perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                        {
                          "loginId": "hong1234",
                          "password": "password123"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(cookie().exists("JSESSIONID"))
            .andExpect(jsonPath("$.user.name").value("홍길동"))
            .andExpect(jsonPath("$.user.loginId").value("hong1234"))
            .andExpect(jsonPath("$.activeSession").value(Matchers.nullValue()))
            .andReturn();

        assertThat(loginResult.getRequest().getSession(false)).isNotNull();
    }

    @Test
    void duplicateLoginIdReturns409() throws Exception {
        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "홍길동",
                          "loginId": "hong1234",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """)
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "임꺽정",
                          "loginId": "hong1234",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_LOGIN_ID"));
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "홍길동",
                          "loginId": "hong1234",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """)
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                        {
                          "loginId": "hong1234",
                          "password": "wrongpass123"
                        }
                        """)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
}
