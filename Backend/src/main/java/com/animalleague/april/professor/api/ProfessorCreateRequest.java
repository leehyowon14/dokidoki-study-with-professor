package com.animalleague.april.professor.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfessorCreateRequest(
    @NotBlank(message = "교수명은 비어 있을 수 없습니다.")
    String professorName,
    @NotBlank(message = "성별은 비어 있을 수 없습니다.")
    @Pattern(regexp = "male|female", message = "성별은 male 또는 female 이어야 합니다.")
    String gender,
    @NotBlank(message = "성격 유형은 비어 있을 수 없습니다.")
    @Pattern(
        regexp = "gentle|tsundere|english_mix|shy",
        message = "성격 유형은 gentle, tsundere, english_mix, shy 중 하나여야 합니다."
    )
    String personalityType,
    String sourcePhotoUrl
) {
}
