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
import org.springframework.test.web.servlet.MvcResult;

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

        MvcResult createResult = mockMvc.perform(
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
            .andReturn();

        System.err.printf(
            "DIAG createProfessorInitializesAffectionAtZero status=%s body=%s%n",
            createResult.getResponse().getStatus(),
            createResult.getResponse().getContentAsString()
        );

        assertThat(createResult.getResponse().getStatus())
            .withFailMessage(
                "createProfessorInitializesAffectionAtZero status=%s body=%s",
                createResult.getResponse().getStatus(),
                createResult.getResponse().getContentAsString()
            )
            .isEqualTo(201);

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

        MvcResult createResult = mockMvc.perform(
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
            .andReturn();

        System.err.printf(
            "DIAG createProfessorWithSourcePhotoStartsInPendingState status=%s body=%s%n",
            createResult.getResponse().getStatus(),
            createResult.getResponse().getContentAsString()
        );

        assertThat(createResult.getResponse().getStatus())
            .withFailMessage(
                "createProfessorWithSourcePhotoStartsInPendingState status=%s body=%s",
                createResult.getResponse().getStatus(),
                createResult.getResponse().getContentAsString()
            )
            .isEqualTo(201);

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

        MvcResult listResult = mockMvc.perform(
                get("/api/professors")
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
            )
            .andReturn();

        System.err.printf(
            "DIAG professorList status=%s body=%s%n",
            listResult.getResponse().getStatus(),
            listResult.getResponse().getContentAsString()
        );

        assertThat(listResult.getResponse().getStatus())
            .withFailMessage(
                "professorList status=%s body=%s",
                listResult.getResponse().getStatus(),
                listResult.getResponse().getContentAsString()
            )
            .isEqualTo(200);
        assertThat(listResult.getResponse().getContentAsString()).contains("알리스교수");

        MvcResult detailResult = mockMvc.perform(
                get("/api/professors/{professorId}", aliceProfessor.getId())
                    .with(SecurityMockMvcRequestPostProcessors.user("alice"))
            )
            .andReturn();

        System.err.printf(
            "DIAG professorDetail status=%s body=%s%n",
            detailResult.getResponse().getStatus(),
            detailResult.getResponse().getContentAsString()
        );

        assertThat(detailResult.getResponse().getStatus())
            .withFailMessage(
                "professorDetail status=%s body=%s",
                detailResult.getResponse().getStatus(),
                detailResult.getResponse().getContentAsString()
            )
            .isEqualTo(200);
        assertThat(detailResult.getResponse().getContentAsString())
            .contains(aliceProfessor.getId().toString())
            .contains("\"affectionScore\":0");

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
        Professor professor = Professor.create(userId, professorName, gender, personalityType, sourcePhotoUrl);
        try {
            Professor savedProfessor = professorRepository.save(professor);
            professorRepository.flush();
            affectionRepository.save(Affection.create(userId, savedProfessor.getId(), 0));
            return savedProfessor;
        } catch (RuntimeException exception) {
            System.err.printf(
                "DIAG seedProfessor failed userId=%s professorName=%s gender=%s personalityType=%s sourcePhotoUrl=%s%n",
                userId,
                professorName,
                gender,
                personalityType,
                sourcePhotoUrl
            );
            exception.printStackTrace(System.err);
            throw new AssertionError(
                "seedProfessor failed userId=%s, professorName=%s, gender=%s, personalityType=%s, sourcePhotoUrl=%s"
                    .formatted(userId, professorName, gender, personalityType, sourcePhotoUrl),
                exception
            );
        }
    }

    private UUID userIdFor(String username) {
        return UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
    }
}
