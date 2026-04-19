package com.animalleague.april.integration.professor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.integration.support.PostgresIntegrationTest;
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
    private ProfessorRepository professorRepository;

    @Autowired
    private AffectionRepository affectionRepository;

    @Test
    void createProfessorInitializesAffectionAtZero() throws Exception {
        UUID userId = userIdFor("alice");

        mockMvc.perform(
                post("/api/professors")
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
                    .contentType("application/json")
                    .content("""
                        {
                          "professorName": "홍길동",
                          "gender": "male",
                          "personalityType": "gentle",
                          "sourcePhotoUrl": null
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.professor.professorName").value("홍길동"))
            .andExpect(jsonPath("$.professor.characterAssetStatus").value("ready"))
            .andExpect(jsonPath("$.professor.isDefaultCharacterAssets").value(true));

        Professor professor = professorRepository.findAllByUserIdOrderByCreatedAtDesc(userId).getFirst();
        assertThat(professor.getProfessorName()).isEqualTo("홍길동");
        assertThat(professor.getCharacterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(professor.isDefaultCharacterAssets()).isTrue();

        Affection affection = affectionRepository.findByProfessorIdAndUserId(professor.getId(), userId)
            .orElseThrow();
        assertThat(affection.getAffectionScore()).isZero();
    }

    @Test
    void createProfessorWithSourcePhotoStartsInPendingState() throws Exception {
        UUID userId = userIdFor("alice");

        mockMvc.perform(
                post("/api/professors")
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
                    .contentType("application/json")
                    .content("""
                        {
                          "professorName": "김교수",
                          "gender": "female",
                          "personalityType": "shy",
                          "sourcePhotoUrl": "https://cdn.example.com/source/prof_2.jpg"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.professor.professorName").value("김교수"))
            .andExpect(jsonPath("$.professor.characterAssetStatus").value("pending"))
            .andExpect(jsonPath("$.professor.isDefaultCharacterAssets").value(false));

        Professor professor = professorRepository.findAllByUserIdOrderByCreatedAtDesc(userId).getFirst();
        assertThat(professor.getSourcePhotoUrl()).isEqualTo("https://cdn.example.com/source/prof_2.jpg");
        assertThat(professor.getCharacterAssetStatus()).isEqualTo(CharacterAssetStatus.PENDING);
        assertThat(professor.isDefaultCharacterAssets()).isFalse();
    }

    @Test
    void professorListAndDetailAreScopedToAuthenticatedUser() throws Exception {
        Professor aliceProfessor = seedProfessor("alice", "알리스교수", Gender.MALE, PersonalityType.GENTLE, null);
        Professor bobProfessor = seedProfessor(
            "bob",
            "밥교수",
            Gender.FEMALE,
            PersonalityType.SHY,
            "https://cdn.example.com/source/bob.jpg"
        );

        mockMvc.perform(
                get("/api/professors")
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.professors", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.professors[0].professorName").value("알리스교수"));

        mockMvc.perform(
                get("/api/professors/{professorId}", aliceProfessor.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.professor.id").value(aliceProfessor.getId().toString()))
            .andExpect(jsonPath("$.affection.professorId").value(aliceProfessor.getId().toString()))
            .andExpect(jsonPath("$.affection.affectionScore").value(0))
            .andExpect(jsonPath("$.characterAssets", Matchers.hasSize(0)));

        mockMvc.perform(
                get("/api/professors/{professorId}", bobProfessor.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void unauthenticatedProfessorRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/professors"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private Professor seedProfessor(
        String username,
        String professorName,
        Gender gender,
        PersonalityType personalityType,
        String sourcePhotoUrl
    ) {
        UUID userId = userIdFor(username);
        Professor savedProfessor = professorRepository.save(
            Professor.create(userId, professorName, gender, personalityType, sourcePhotoUrl)
        );
        professorRepository.flush();
        affectionRepository.save(Affection.create(userId, savedProfessor.getId(), 0));
        return savedProfessor;
    }

    private UUID userIdFor(String username) {
        return UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
    }
}
