package com.animalleague.april.professor.infrastructure;

import java.util.List;
import java.util.UUID;

import com.animalleague.april.common.domain.PersonalityType;

public interface NanobananaClient {

    GenerationTicket requestCharacterAssetGeneration(CharacterAssetGenerationRequest request);

    record CharacterAssetGenerationRequest(
        UUID professorId,
        PersonalityType personalityType,
        String sourcePhotoUrl,
        List<String> variantKeys
    ) {
    }

    record GenerationTicket(String requestId) {
    }
}
