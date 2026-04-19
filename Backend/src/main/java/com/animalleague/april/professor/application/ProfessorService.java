package com.animalleague.april.professor.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public ProfessorService(
        ProfessorRepository professorRepository,
        AffectionRepository affectionRepository
    ) {
        this.professorRepository = professorRepository;
        this.affectionRepository = affectionRepository;
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
        affectionRepository.save(Affection.create(currentUserId, savedProfessor.getId(), 0));

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }

        UUID principalId = extractPrincipalId(authentication.getPrincipal());
        if (principalId != null) {
            return principalId;
        }

        return UUID.nameUUIDFromBytes(authentication.getName().getBytes(StandardCharsets.UTF_8));
    }

    private UUID extractPrincipalId(Object principal) {
        if (principal == null) {
            return null;
        }

        if (principal instanceof UUID uuid) {
            return uuid;
        }

        if (principal instanceof CharSequence sequence) {
            try {
                return UUID.fromString(sequence.toString());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        for (String methodName : List.of("id", "getId")) {
            UUID resolved = invokeUuidAccessor(principal, methodName);
            if (resolved != null) {
                return resolved;
            }
        }

        return null;
    }

    private UUID invokeUuidAccessor(Object principal, String methodName) {
        try {
            Method method = principal.getClass().getMethod(methodName);
            Object result = method.invoke(principal);
            if (result instanceof UUID uuid) {
                return uuid;
            }
            if (result instanceof CharSequence sequence) {
                return UUID.fromString(sequence.toString());
            }
            return null;
        } catch (NoSuchMethodException ignored) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException exception) {
            return null;
        }
    }
}
