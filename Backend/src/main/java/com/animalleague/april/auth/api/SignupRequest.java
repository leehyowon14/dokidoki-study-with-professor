package com.animalleague.april.auth.api;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "이름은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    String name,
    @NotBlank(message = "로그인 ID는 비어 있을 수 없습니다.")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "로그인 ID는 영문 또는 숫자만 사용할 수 있습니다.")
    @Size(min = 4, max = 50, message = "로그인 ID는 4자 이상 50자 이하여야 합니다.")
    String loginId,
    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    String password,
    @NotNull(message = "시험 종료일은 필수입니다.")
    @FutureOrPresent(message = "시험 종료일은 오늘 이후 또는 오늘이어야 합니다.")
    LocalDate examEndDate
) {}
