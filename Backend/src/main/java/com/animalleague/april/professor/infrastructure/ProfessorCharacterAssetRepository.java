package com.animalleague.april.professor.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animalleague.april.professor.domain.ProfessorCharacterAsset;

public interface ProfessorCharacterAssetRepository extends JpaRepository<ProfessorCharacterAsset, UUID> {

    void deleteAllByProfessorId(UUID professorId);

    List<ProfessorCharacterAsset> findAllByProfessorIdOrderByVariantKey(UUID professorId);
}
