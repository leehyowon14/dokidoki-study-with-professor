package com.animalleague.april.integration.auth;

import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.auth.domain.User;
import com.animalleague.april.auth.infrastructure.UserRepository;
import com.animalleague.april.integration.support.PostgresIntegrationTest;

@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.flyway.enabled=true")
class AuthFlowIntegrationTest extends PostgresIntegrationTest {

    private static final String SIGNUP_LOGIN_ID = "hong1234";
    private static final String DUPLICATE_LOGIN_ID = "hong1235";
    private static final String WRONG_PASSWORD_LOGIN_ID = "hong1236";

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
                          "loginId": "%s",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """.formatted(SIGNUP_LOGIN_ID))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.user.name").value("홍길동"))
            .andExpect(jsonPath("$.user.loginId").value(SIGNUP_LOGIN_ID))
            .andExpect(jsonPath("$.user.examEndDate").value("2026-06-20"));

        User persistedUser = userRepository.findByLoginId(SIGNUP_LOGIN_ID).orElseThrow();
        assertThat(persistedUser.getId()).isNotNull();
        assertThat(persistedUser.getLoginId()).isEqualTo(SIGNUP_LOGIN_ID);
        assertThat(persistedUser.getName()).isEqualTo("홍길동");
        assertThat(persistedUser.getExamEndDate()).isEqualTo(LocalDate.parse("2026-06-20"));
        assertThat(persistedUser.getPasswordHash()).isNotEqualTo("password123");
        assertThat(persistedUser.getCreatedAt()).isNotNull();
        assertThat(persistedUser.getUpdatedAt()).isNotNull();

        MvcResult loginResult = mockMvc.perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                        {
                          "loginId": "%s",
                          "password": "password123"
                        }
                        """.formatted(SIGNUP_LOGIN_ID))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.name").value("홍길동"))
            .andExpect(jsonPath("$.user.loginId").value(SIGNUP_LOGIN_ID))
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
                          "loginId": "%s",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """.formatted(DUPLICATE_LOGIN_ID))
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "임꺽정",
                          "loginId": "%s",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """.formatted(DUPLICATE_LOGIN_ID))
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
                          "loginId": "%s",
                          "password": "password123",
                          "examEndDate": "2026-06-20"
                        }
                        """.formatted(WRONG_PASSWORD_LOGIN_ID))
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                        {
                          "loginId": "%s",
                          "password": "wrongpass123"
                        }
                        """.formatted(WRONG_PASSWORD_LOGIN_ID))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
}
