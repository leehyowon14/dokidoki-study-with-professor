package com.animalleague.april.unit.professor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.professor.api.ProfessorCreateRequest;
import com.animalleague.april.professor.api.ProfessorCreateResponse;
import com.animalleague.april.professor.application.ProfessorService;
import com.animalleague.april.professor.domain.Affection;
import com.animalleague.april.professor.domain.Professor;
import com.animalleague.april.professor.infrastructure.AffectionRepository;
import com.animalleague.april.professor.infrastructure.ProfessorRepository;

class AffectionInitializationUnitTest {

    @Test
    void createProfessorSavesProfessorAndAffectionForAuthenticatedUser() {
        ProfessorRepository professorRepository = mock(ProfessorRepository.class);
        AffectionRepository affectionRepository = mock(AffectionRepository.class);
        ProfessorService professorService = new ProfessorService(professorRepository, affectionRepository);

        String username = "worker-2";
        UUID expectedUserId = UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
        UUID persistedProfessorId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        SecurityContextHolder.getContext().setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(username, null, List.of())
        );

        Professor persistedProfessor = mock(Professor.class);
        given(persistedProfessor.getId()).willReturn(persistedProfessorId);
        given(persistedProfessor.getProfessorName()).willReturn("홍길동");
        given(persistedProfessor.getGender()).willReturn(Gender.MALE);
        given(persistedProfessor.getPersonalityType()).willReturn(PersonalityType.GENTLE);
        given(persistedProfessor.getSourcePhotoUrl()).willReturn("https://cdn.example.com/source/prof_3.jpg");
        given(persistedProfessor.getCharacterAssetStatus()).willReturn(CharacterAssetStatus.PENDING);
        given(persistedProfessor.getRepresentativeAssetUrl()).willReturn(null);
        given(persistedProfessor.isDefaultCharacterAssets()).willReturn(false);
        given(professorRepository.save(any(Professor.class))).willReturn(persistedProfessor);
        given(affectionRepository.save(any(Affection.class))).willAnswer(invocation -> invocation.getArgument(0));

        try {
            ProfessorCreateResponse response = professorService.createProfessor(
                new ProfessorCreateRequest(
                    "홍길동",
                    "male",
                    "gentle",
                    "https://cdn.example.com/source/prof_3.jpg"
                )
            );

            assertThat(response.professor().characterAssetStatus()).isEqualTo(CharacterAssetStatus.PENDING);
            assertThat(response.professor().isDefaultCharacterAssets()).isFalse();

            verify(professorRepository).save(
                org.mockito.ArgumentMatchers.argThat(professor ->
                    expectedUserId.equals(professor.getUserId())
                        && professor.getCharacterAssetStatus() == CharacterAssetStatus.PENDING
                        && !professor.isDefaultCharacterAssets()
                )
            );
            verify(affectionRepository).save(
                org.mockito.ArgumentMatchers.argThat(affection ->
                    expectedUserId.equals(affection.getUserId())
                        && persistedProfessorId.equals(affection.getProfessorId())
                        && affection.getAffectionScore() == 0
                )
            );
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void affectionRejectsNegativeScore() {
        assertThatThrownBy(() -> Affection.create(UUID.randomUUID(), UUID.randomUUID(), -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("affectionScore는 0 이상 100 이하여야 합니다.");
    }
}
