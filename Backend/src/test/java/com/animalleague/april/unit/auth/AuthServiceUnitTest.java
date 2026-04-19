package com.animalleague.april.unit.auth;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.animalleague.april.auth.application.AuthService;
import com.animalleague.april.auth.application.LoginPolicy;
import com.animalleague.april.auth.application.SignupPolicy;
import com.animalleague.april.auth.domain.User;
import com.animalleague.april.auth.infrastructure.UserRepository;
import com.animalleague.april.common.api.ApiException;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SignupPolicy signupPolicy;

    @Mock
    private LoginPolicy loginPolicy;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, signupPolicy, loginPolicy);
    }

    @Test
    void duplicateConstraintViolationReturns409WithoutFollowUpQuery() {
        given(userRepository.existsByLoginId("hong1234")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(userRepository.saveAndFlush(any(User.class)))
            .willThrow(
                new DataIntegrityViolationException(
                    "duplicate signup",
                    new RuntimeException("duplicate key value violates unique constraint \"uk_users_login_id\"")
                )
            );

        assertThatThrownBy(
            () -> authService.signup("홍길동", "hong1234", "password123", LocalDate.parse("2026-06-20"))
        )
            .isInstanceOfSatisfying(ApiException.class, exception -> {
                assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(exception.getCode()).isEqualTo("DUPLICATE_LOGIN_ID");
            });

        then(userRepository).should(times(1)).existsByLoginId("hong1234");
    }

    @Test
    void nonDuplicateIntegrityViolationReturns400() {
        given(userRepository.existsByLoginId("hong1234")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(userRepository.saveAndFlush(any(User.class)))
            .willThrow(
                new DataIntegrityViolationException(
                    "invalid signup input",
                    new RuntimeException("value too long for type character varying(100)")
                )
            );

        assertThatThrownBy(
            () -> authService.signup("홍길동", "hong1234", "password123", LocalDate.parse("2026-06-20"))
        )
            .isInstanceOfSatisfying(ApiException.class, exception -> {
                assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(exception.getCode()).isEqualTo("INVALID_SIGNUP_INPUT");
            });
    }
}
