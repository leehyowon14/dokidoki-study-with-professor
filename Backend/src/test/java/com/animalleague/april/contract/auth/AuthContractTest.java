package com.animalleague.april.contract.auth;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.auth.api.AuthController;
import com.animalleague.april.auth.api.SignupRequest;
import com.animalleague.april.auth.application.AuthService;
import com.animalleague.april.common.api.ApiException;
import com.animalleague.april.common.infrastructure.GlobalExceptionHandler;
import com.animalleague.april.contract.support.ApiContractTest;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthContractTest extends ApiContractTest {

    @MockBean
    private AuthService authService;

    @Test
    void signupValidationFailureReturns400() throws Exception {
        SignupRequest request = new SignupRequest("", "ab", "1234", LocalDate.now().minusDays(1));

        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content(json(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    void duplicateLoginIdReturns409() throws Exception {
        given(authService.signup(anyString(), anyString(), anyString(), any(LocalDate.class)))
            .willThrow(new ApiException(HttpStatus.CONFLICT, "DUPLICATE_LOGIN_ID", "이미 사용 중인 로그인 ID입니다."));

        SignupRequest request = new SignupRequest("홍길동", "hong1234", "password123", LocalDate.now().plusDays(30));

        mockMvc.perform(
                post("/api/auth/signup")
                    .contentType("application/json")
                    .content(json(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_LOGIN_ID"));
    }

    @Test
    void invalidCredentialsReturns401() throws Exception {
        given(authService.login(anyString(), anyString()))
            .willThrow(
                new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_CREDENTIALS",
                    "로그인 ID 또는 비밀번호가 올바르지 않습니다."
                )
            );

        mockMvc.perform(
                post("/api/auth/login")
                    .contentType("application/json")
                    .content("""
                        {
                          "loginId": "hong1234",
                          "password": "password123"
                        }
                        """)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
}
