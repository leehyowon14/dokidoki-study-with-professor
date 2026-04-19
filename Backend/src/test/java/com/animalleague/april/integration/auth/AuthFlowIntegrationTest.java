package com.animalleague.april.integration.auth;

import java.time.LocalDate;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.auth.domain.User;
import com.animalleague.april.auth.infrastructure.UserRepository;
import com.animalleague.april.integration.support.PostgresIntegrationTest;

@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.flyway.enabled=true")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AuthFlowIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearUsers() {
        userRepository.deleteAllInBatch();
    }

    @Test
    void signupThenLoginReturnsUserAndNullActiveSession() throws Exception {
        String loginId = newLoginId("signup");

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
                        """.formatted(loginId))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.user.name").value("홍길동"))
            .andExpect(jsonPath("$.user.loginId").value(loginId))
            .andExpect(jsonPath("$.user.examEndDate").value("2026-06-20"));

        User persistedUser = userRepository.findByLoginId(loginId).orElseThrow();
        assertThat(persistedUser.getId()).isNotNull();
        assertThat(persistedUser.getLoginId()).isEqualTo(loginId);
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
                        """.formatted(loginId))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.name").value("홍길동"))
            .andExpect(jsonPath("$.user.loginId").value(loginId))
            .andExpect(jsonPath("$.activeSession").value(Matchers.nullValue()))
            .andReturn();

        assertThat(loginResult.getRequest().getSession(false)).isNotNull();
    }

    @Test
    void duplicateLoginIdReturns409() throws Exception {
        String loginId = newLoginId("dup");

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
                        """.formatted(loginId))
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
                        """.formatted(loginId))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_LOGIN_ID"));
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        String loginId = newLoginId("wrong");

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
                        """.formatted(loginId))
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
                        """.formatted(loginId))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void loginWithUnknownUserReturns401() throws Exception {
        mockMvc.perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                        {
                          "loginId": "%s",
                          "password": "password123"
                        }
                        """.formatted(newLoginId("ghost")))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    private String newLoginId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
