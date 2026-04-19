package com.animalleague.april.professor.application;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.professor.api.AffectionResponse;
import com.animalleague.april.professor.api.ProfessorCreateRequest;
import com.animalleague.april.professor.api.ProfessorCreateResponse;
import com.animalleague.april.professor.api.ProfessorDetailResponse;
import com.animalleague.april.professor.api.ProfessorListResponse;
import com.animalleague.april.professor.api.ProfessorResponse;
import com.animalleague.april.professor.domain.Affection;
import com.animalleague.april.professor.domain.Professor;
import com.animalleague.april.professor.infrastructure.AffectionRepository;
import com.animalleague.april.professor.infrastructure.ProfessorRepository;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final AffectionRepository affectionRepository;
    private final CurrentUserProvider currentUserProvider;

    public ProfessorService(
        ProfessorRepository professorRepository,
        AffectionRepository affectionRepository,
        CurrentUserProvider currentUserProvider
    ) {
        this.professorRepository = professorRepository;
        this.affectionRepository = affectionRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public ProfessorCreateResponse createProfessor(ProfessorCreateRequest request) {
        UUID currentUserId = resolveCurrentUserId();
        Professor professor = Professor.create(
            currentUserId,
            request.professorName(),
            Gender.fromValue(request.gender()),
            PersonalityType.fromValue(request.personalityType()),
            request.sourcePhotoUrl()
        );

        Professor savedProfessor = professorRepository.save(professor);
        professorRepository.flush();

        UUID professorId = savedProfessor.getId();
        if (professorId == null) {
            throw new IllegalStateException("교수 ID 생성에 실패했습니다.");
        }

        affectionRepository.save(Affection.create(currentUserId, professorId, 0));

        return new ProfessorCreateResponse(ProfessorResponse.from(savedProfessor));
    }

    @Transactional(readOnly = true)
    public ProfessorListResponse listProfessors() {
        UUID currentUserId = resolveCurrentUserId();
        List<ProfessorResponse> professors = professorRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId)
            .stream()
            .map(ProfessorResponse::from)
            .toList();
        return new ProfessorListResponse(professors);
    }

    @Transactional(readOnly = true)
    public ProfessorDetailResponse getProfessorDetail(UUID professorId) {
        UUID currentUserId = resolveCurrentUserId();
        Professor professor = professorRepository.findByIdAndUserId(professorId, currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교수를 찾을 수 없습니다."));

        Affection affection = affectionRepository.findByProfessorIdAndUserId(professorId, currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "호감도 정보를 찾을 수 없습니다."));

        return new ProfessorDetailResponse(
            ProfessorResponse.from(professor),
            AffectionResponse.from(affection),
            List.of()
        );
    }

    private UUID resolveCurrentUserId() {
        UUID currentUserId = currentUserProvider.currentUserId();
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        return currentUserId;
    }
}
