package com.animalleague.april.integration.professor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.integration.support.PostgresIntegrationTest;
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

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AffectionRepository affectionRepository;

    @Test
    void createProfessorInitializesAffectionAtZero() {
        UUID userId = userIdFor("alice");

        runAs("alice", () -> professorService.createProfessor(
            new ProfessorCreateRequest("홍길동", "male", "gentle", null)
        ));

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

        runAs("alice", () -> professorService.createProfessor(
            new ProfessorCreateRequest(
                "김교수",
                "female",
                "shy",
                "https://cdn.example.com/source/prof_2.jpg"
            )
        ));

        Professor professor = professorRepository.findAllByUserIdOrderByCreatedAtDesc(userId).getFirst();
        assertThat(professor.getSourcePhotoUrl()).isEqualTo("https://cdn.example.com/source/prof_2.jpg");
        assertThat(professor.getCharacterAssetStatus()).isEqualTo(CharacterAssetStatus.PENDING);
        assertThat(professor.isDefaultCharacterAssets()).isFalse();
    }

    @Test
    void professorListAndDetailAreScopedToAuthenticatedUser() {
        UUID aliceProfessorId = runAs("alice", () -> professorService.createProfessor(
            new ProfessorCreateRequest("알리스교수", "male", "gentle", null)
        ).professor().id());
        UUID bobProfessorId = runAs("bob", () -> professorService.createProfessor(
            new ProfessorCreateRequest(
                "밥교수",
                "female",
                "shy",
                "https://cdn.example.com/source/bob.jpg"
            )
        ).professor().id());

        ProfessorListResponse aliceList = runAs("alice", professorService::listProfessors);
        assertThat(aliceList.professors()).hasSize(1);
        assertThat(aliceList.professors().getFirst().professorName()).isEqualTo("알리스교수");

        ProfessorDetailResponse aliceDetail = runAs("alice", () -> professorService.getProfessorDetail(aliceProfessorId));
        assertThat(aliceDetail.professor().id()).isEqualTo(aliceProfessorId);
        assertThat(aliceDetail.affection().professorId()).isEqualTo(aliceProfessorId);
        assertThat(aliceDetail.affection().affectionScore()).isZero();
        assertThat(aliceDetail.characterAssets()).isEmpty();

        assertThatThrownBy(() -> runAs("alice", () -> professorService.getProfessorDetail(bobProfessorId)))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void unauthenticatedProfessorRequestReturns401() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/professors"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private UUID userIdFor(String username) {
        return UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
    }

    private <T> T runAs(String username, ThrowingSupplier<T> action) {
        SecurityContextHolder.getContext().setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(username, null, List.of())
        );

        try {
            return action.get();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private interface ThrowingSupplier<T> extends Supplier<T> {
    }
}
