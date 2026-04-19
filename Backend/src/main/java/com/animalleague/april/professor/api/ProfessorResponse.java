package com.animalleague.april.professor.api;

import java.util.UUID;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.professor.domain.Professor;

public record ProfessorResponse(
    UUID id,
    String professorName,
    Gender gender,
    PersonalityType personalityType,
    String sourcePhotoUrl,
    CharacterAssetStatus characterAssetStatus,
    String representativeAssetUrl,
    boolean isDefaultCharacterAssets
) {

    public static ProfessorResponse from(Professor professor) {
        return new ProfessorResponse(
            professor.getId(),
            professor.getProfessorName(),
            professor.getGender(),
            professor.getPersonalityType(),
            professor.getSourcePhotoUrl(),
            professor.getCharacterAssetStatus(),
            professor.getRepresentativeAssetUrl(),
            professor.isDefaultCharacterAssets()
        );
    }
}
