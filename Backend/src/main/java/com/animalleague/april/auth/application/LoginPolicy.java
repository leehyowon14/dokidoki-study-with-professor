package com.animalleague.april.auth.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.animalleague.april.common.api.ApiException;

@Component
public class LoginPolicy {

    public void validate(String loginId, String password) {
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
    }
}
