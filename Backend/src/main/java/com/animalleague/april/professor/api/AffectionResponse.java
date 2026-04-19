package com.animalleague.april.professor.api;

import java.util.UUID;

import com.animalleague.april.professor.domain.Affection;

public record AffectionResponse(UUID professorId, int affectionScore) {

    public static AffectionResponse from(Affection affection) {
        return new AffectionResponse(affection.getProfessorId(), affection.getAffectionScore());
    }
}
