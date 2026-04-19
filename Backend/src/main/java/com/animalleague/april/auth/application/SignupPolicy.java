package com.animalleague.april.auth.application;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.animalleague.april.common.api.ApiException;

@Component
public class SignupPolicy {

    private final Clock clock;

    public SignupPolicy(Clock clock) {
        this.clock = clock;
    }

    public void validate(String name, String loginId, String password, LocalDate examEndDate) {
        if (name == null || name.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_NAME", "이름은 비어 있을 수 없습니다.");
        }

        if (loginId == null || !loginId.matches("[A-Za-z0-9]{4,}")) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "INVALID_LOGIN_ID",
                "로그인 ID는 영문 또는 숫자 4자 이상이어야 합니다."
            );
        }

        if (password == null || password.length() < 8) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "INVALID_PASSWORD",
                "비밀번호는 8자 이상이어야 합니다."
            );
        }

        if (examEndDate == null || examEndDate.isBefore(LocalDate.now(clock))) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "INVALID_EXAM_END_DATE",
                "시험 종료일은 오늘 이후 또는 오늘이어야 합니다."
            );
        }
    }
}
