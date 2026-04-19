package com.animalleague.april.auth.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "로그인 ID는 비어 있을 수 없습니다.")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "로그인 ID는 영문 또는 숫자만 사용할 수 있습니다.")
    @Size(min = 4, message = "로그인 ID는 4자 이상이어야 합니다.")
    String loginId,
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    String password
) {}
