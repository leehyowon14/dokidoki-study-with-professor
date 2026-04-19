package com.animalleague.april.professor.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animalleague.april.professor.domain.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {

    List<Professor> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Professor> findByIdAndUserId(UUID id, UUID userId);
}
