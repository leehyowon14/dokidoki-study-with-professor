package com.animalleague.april.contract.professor;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.common.infrastructure.GlobalExceptionHandler;
import com.animalleague.april.contract.support.ApiContractTest;
import com.animalleague.april.professor.api.AffectionResponse;
import com.animalleague.april.professor.api.ProfessorController;
import com.animalleague.april.professor.api.ProfessorCreateRequest;
import com.animalleague.april.professor.api.ProfessorCreateResponse;
import com.animalleague.april.professor.api.ProfessorDetailResponse;
import com.animalleague.april.professor.api.ProfessorListResponse;
import com.animalleague.april.professor.api.ProfessorResponse;
import com.animalleague.april.professor.application.ProfessorService;

@WebMvcTest(controllers = ProfessorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProfessorContractTest extends ApiContractTest {

    @MockBean
    private ProfessorService professorService;

    @Test
    void createProfessorReturnsProfessorEnvelope() throws Exception {
        ProfessorResponse professor = new ProfessorResponse(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "홍길동",
            Gender.MALE,
            PersonalityType.GENTLE,
            null,
            CharacterAssetStatus.READY,
            null,
            true
        );
        given(professorService.createProfessor(any(ProfessorCreateRequest.class)))
            .willReturn(new ProfessorCreateResponse(professor));

        mockMvc.perform(
                post("/api/professors")
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
            .andExpect(jsonPath("$.professor.id").value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.professor.professorName").value("홍길동"))
            .andExpect(jsonPath("$.professor.gender").value("male"))
            .andExpect(jsonPath("$.professor.personalityType").value("gentle"))
            .andExpect(jsonPath("$.professor.characterAssetStatus").value("ready"))
            .andExpect(jsonPath("$.professor.isDefaultCharacterAssets").value(true));
    }

    @Test
    void createProfessorWithSourcePhotoReturnsPendingProfessorEnvelope() throws Exception {
        ProfessorResponse professor = new ProfessorResponse(
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "김교수",
            Gender.FEMALE,
            PersonalityType.SHY,
            "https://cdn.example.com/source/prof_2.jpg",
            CharacterAssetStatus.PENDING,
            null,
            false
        );
        given(professorService.createProfessor(any(ProfessorCreateRequest.class)))
            .willReturn(new ProfessorCreateResponse(professor));

        mockMvc.perform(
                post("/api/professors")
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
            .andExpect(jsonPath("$.professor.id").value("22222222-2222-2222-2222-222222222222"))
            .andExpect(jsonPath("$.professor.gender").value("female"))
            .andExpect(jsonPath("$.professor.characterAssetStatus").value("pending"))
            .andExpect(jsonPath("$.professor.isDefaultCharacterAssets").value(false));
    }

    @Test
    void listProfessorsReturnsProfessorArray() throws Exception {
        ProfessorResponse professor = new ProfessorResponse(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "홍길동",
            Gender.MALE,
            PersonalityType.GENTLE,
            null,
            CharacterAssetStatus.READY,
            null,
            true
        );
        given(professorService.listProfessors())
            .willReturn(new ProfessorListResponse(List.of(professor)));

        mockMvc.perform(get("/api/professors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.professors[0].id").value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.professors[0].gender").value("male"))
            .andExpect(jsonPath("$.professors[0].characterAssetStatus").value("ready"));
    }

    @Test
    void getProfessorDetailReturnsAffectionAndCharacterAssets() throws Exception {
        ProfessorResponse professor = new ProfessorResponse(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "홍길동",
            Gender.MALE,
            PersonalityType.GENTLE,
            null,
            CharacterAssetStatus.READY,
            null,
            true
        );
        given(professorService.getProfessorDetail(any(UUID.class)))
                    .willReturn(
                new ProfessorDetailResponse(
                    professor,
                    new AffectionResponse(UUID.fromString("11111111-1111-1111-1111-111111111111"), 0),
                    List.of()
                )
            );

        mockMvc.perform(get("/api/professors/11111111-1111-1111-1111-111111111111"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.professor.id").value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.affection.professorId").value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.affection.affectionScore").value(0))
            .andExpect(jsonPath("$.characterAssets").value(org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void invalidGenderReturns400() throws Exception {
        mockMvc.perform(
                post("/api/professors")
                    .contentType("application/json")
                    .content("""
                        {
                          "professorName": "홍길동",
                          "gender": "other",
                          "personalityType": "gentle",
                          "sourcePhotoUrl": null
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.violations[0].field").value("gender"));
    }
}
