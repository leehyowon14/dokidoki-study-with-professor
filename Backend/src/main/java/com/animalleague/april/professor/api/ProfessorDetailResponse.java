package com.animalleague.april.professor.api;

import java.util.List;

public record ProfessorDetailResponse(
    ProfessorResponse professor,
    AffectionResponse affection,
    List<CharacterAssetResponse> characterAssets
) {
}
