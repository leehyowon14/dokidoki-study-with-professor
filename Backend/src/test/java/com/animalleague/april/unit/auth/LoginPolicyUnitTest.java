package com.animalleague.april.unit.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.animalleague.april.auth.application.LoginPolicy;
import com.animalleague.april.common.api.ApiException;

class LoginPolicyUnitTest {

    private LoginPolicy loginPolicy;

    @BeforeEach
    void setUp() {
        loginPolicy = new LoginPolicy();
    }

    @Test
    void invalidLoginIdIsRejected() {
        assertThatThrownBy(() -> loginPolicy.validate("ab!", "password123"))
            .isInstanceOf(ApiException.class)
            .hasMessage("로그인 ID는 영문 또는 숫자 4자 이상이어야 합니다.");
    }

    @Test
    void shortPasswordIsRejected() {
        assertThatThrownBy(() -> loginPolicy.validate("hong1234", "1234"))
            .isInstanceOf(ApiException.class)
            .hasMessage("비밀번호는 8자 이상이어야 합니다.");
    }
}
