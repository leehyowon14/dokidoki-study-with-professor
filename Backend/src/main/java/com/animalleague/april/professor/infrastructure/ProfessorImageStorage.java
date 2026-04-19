package com.animalleague.april.professor.infrastructure;

import java.util.UUID;

public interface ProfessorImageStorage {

    String storeCharacterAsset(
        UUID professorId,
        String variantKey,
        byte[] imageBytes,
        String contentType
    );
}
