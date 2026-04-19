package com.animalleague.april.integration.professor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.integration.support.PostgresIntegrationTest;
import com.animalleague.april.professor.application.CurrentUserProvider;
import com.animalleague.april.professor.api.ProfessorCreateRequest;
import com.animalleague.april.professor.api.ProfessorDetailResponse;
import com.animalleague.april.professor.api.ProfessorListResponse;
import com.animalleague.april.professor.application.ProfessorService;
import com.animalleague.april.professor.domain.Affection;
import com.animalleague.april.professor.domain.Professor;
import com.animalleague.april.professor.infrastructure.AffectionRepository;
import com.animalleague.april.professor.infrastructure.ProfessorRepository;

@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.flyway.enabled=true")
class ProfessorRegistrationIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfessorService professorService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AffectionRepository affectionRepository;

    @Test
    void createProfessorInitializesAffectionAtZero() {
        UUID userId = userIdFor("alice");
        given(currentUserProvider.currentUserId()).willReturn(userId);

        professorService.createProfessor(
            new ProfessorCreateRequest("홍길동", "male", "gentle", null)
        );

        Professor professor = professorRepository.findAllByUserIdOrderByCreatedAtDesc(userId).getFirst();
        assertThat(professor.getProfessorName()).isEqualTo("홍길동");
        assertThat(professor.getCharacterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(professor.isDefaultCharacterAssets()).isTrue();

        Affection affection = affectionRepository.findByProfessorIdAndUserId(professor.getId(), userId)
            .orElseThrow();
        assertThat(affection.getAffectionScore()).isZero();
    }

    @Test
    void createProfessorWithSourcePhotoStartsInPendingState() {
        UUID userId = userIdFor("alice");
        given(currentUserProvider.currentUserId()).willReturn(userId);

        professorService.createProfessor(
            new ProfessorCreateRequest(
                "김교수",
                "female",
                "shy",
                "https://cdn.example.com/source/prof_2.jpg"
            )
        );

        Professor professor = professorRepository.findAllByUserIdOrderByCreatedAtDesc(userId).getFirst();
        assertThat(professor.getSourcePhotoUrl()).isEqualTo("https://cdn.example.com/source/prof_2.jpg");
        assertThat(professor.getCharacterAssetStatus()).isEqualTo(CharacterAssetStatus.PENDING);
        assertThat(professor.isDefaultCharacterAssets()).isFalse();
    }

    @Test
    void professorListAndDetailAreScopedToAuthenticatedUser() {
        UUID aliceUserId = userIdFor("alice");
        UUID bobUserId = userIdFor("bob");

        given(currentUserProvider.currentUserId()).willReturn(aliceUserId);
        UUID aliceProfessorId = professorService.createProfessor(
            new ProfessorCreateRequest("알리스교수", "male", "gentle", null)
        ).professor().id();

        given(currentUserProvider.currentUserId()).willReturn(bobUserId);
        UUID bobProfessorId = professorService.createProfessor(
            new ProfessorCreateRequest(
                "밥교수",
                "female",
                "shy",
                "https://cdn.example.com/source/bob.jpg"
            )
        ).professor().id();

        given(currentUserProvider.currentUserId()).willReturn(aliceUserId);
        ProfessorListResponse aliceList = professorService.listProfessors();
        assertThat(aliceList.professors()).hasSize(1);
        assertThat(aliceList.professors().getFirst().professorName()).isEqualTo("알리스교수");

        ProfessorDetailResponse aliceDetail = professorService.getProfessorDetail(aliceProfessorId);
        assertThat(aliceDetail.professor().id()).isEqualTo(aliceProfessorId);
        assertThat(aliceDetail.affection().professorId()).isEqualTo(aliceProfessorId);
        assertThat(aliceDetail.affection().affectionScore()).isZero();
        assertThat(aliceDetail.characterAssets()).isEmpty();

        assertThatThrownBy(() -> professorService.getProfessorDetail(bobProfessorId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void unauthenticatedProfessorRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/professors"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private UUID userIdFor(String username) {
        return UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
    }
}
