package com.animalleague.april.unit.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.animalleague.april.auth.application.SignupPolicy;
import com.animalleague.april.common.api.ApiException;

class SignupPolicyUnitTest {

    private SignupPolicy signupPolicy;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-05-01T00:00:00Z"), ZoneOffset.UTC);
        signupPolicy = new SignupPolicy(fixedClock);
    }

    @Test
    void validSignupDoesNotThrowException() {
        assertThatCode(
            () -> signupPolicy.validate("홍길동", "hong1234", "password123", LocalDate.parse("2026-06-20"))
        ).doesNotThrowAnyException();
    }

    @Test
    void blankNameIsRejected() {
        assertThatThrownBy(() -> signupPolicy.validate("", "hong1234", "password123", LocalDate.parse("2026-06-20")))
            .isInstanceOf(ApiException.class)
            .hasMessage("이름은 비어 있을 수 없습니다.");
    }

    @Test
    void longNameIsRejected() {
        assertThatThrownBy(
            () -> signupPolicy.validate("가".repeat(101), "hong1234", "password123", LocalDate.parse("2026-06-20"))
        )
            .isInstanceOf(ApiException.class)
            .hasMessage("이름은 100자 이하여야 합니다.");
    }

    @Test
    void invalidLoginIdIsRejected() {
        assertThatThrownBy(
            () -> signupPolicy.validate("홍길동", "ab!", "password123", LocalDate.parse("2026-06-20"))
        )
            .isInstanceOf(ApiException.class)
            .hasMessage("로그인 ID는 영문 또는 숫자 4자 이상이어야 합니다.");
    }

    @Test
    void longLoginIdIsRejected() {
        assertThatThrownBy(
            () -> signupPolicy.validate("홍길동", "a".repeat(51), "password123", LocalDate.parse("2026-06-20"))
        )
            .isInstanceOf(ApiException.class)
            .hasMessage("로그인 ID는 50자 이하여야 합니다.");
    }

    @Test
    void shortPasswordIsRejected() {
        assertThatThrownBy(
            () -> signupPolicy.validate("홍길동", "hong1234", "1234", LocalDate.parse("2026-06-20"))
        )
            .isInstanceOf(ApiException.class)
            .hasMessage("비밀번호는 8자 이상이어야 합니다.");
    }

    @Test
    void pastExamEndDateIsRejected() {
        assertThatThrownBy(
            () -> signupPolicy.validate("홍길동", "hong1234", "password123", LocalDate.parse("2026-04-30"))
        )
            .isInstanceOf(ApiException.class)
            .hasMessage("시험 종료일은 오늘 이후 또는 오늘이어야 합니다.");
    }
}
