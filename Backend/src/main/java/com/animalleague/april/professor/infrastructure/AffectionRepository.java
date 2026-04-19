package com.animalleague.april.professor.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animalleague.april.professor.domain.Affection;

public interface AffectionRepository extends JpaRepository<Affection, UUID> {

    Optional<Affection> findByProfessorIdAndUserId(UUID professorId, UUID userId);
}
