package com.animalleague.april.integration.professor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionOperations;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.integration.support.PostgresIntegrationTest;
import com.animalleague.april.professor.application.DefaultCharacterAssetCatalog;
import com.animalleague.april.professor.application.ProfessorCharacterAssetGenerationService;
import com.animalleague.april.professor.infrastructure.NanobananaClient;
import com.animalleague.april.professor.infrastructure.ProfessorCharacterAssetRepository;
import com.animalleague.april.professor.infrastructure.ProfessorImageStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ProfessorCharacterAssetGenerationIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private ProfessorCharacterAssetRepository professorCharacterAssetRepository;

    @Autowired
    private DefaultCharacterAssetCatalog defaultCharacterAssetCatalog;

    @Autowired
    private TransactionOperations transactionOperations;

    private RecordingNanobananaClient nanobananaClient;
    private DeterministicProfessorImageStorage professorImageStorage;
    private ProfessorCharacterAssetGenerationService service;

    @BeforeEach
    void setUpService() {
        nanobananaClient = new RecordingNanobananaClient();
        professorImageStorage = new DeterministicProfessorImageStorage();
        service = new ProfessorCharacterAssetGenerationService(
            professorCharacterAssetRepository,
            defaultCharacterAssetCatalog,
            nanobananaClient,
            professorImageStorage,
            transactionOperations
        );
    }

    @Test
    void sourcePhotoStartsPendingThenTransitionsToReadyWithRepresentativeAsset() {
        UUID professorId = UUID.randomUUID();

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult pendingResult =
            service.initializeAssets(
                professorId,
                PersonalityType.GENTLE,
                "https://cdn.example.com/source/professor.png"
            );

        assertThat(pendingResult.characterAssetStatus()).isEqualTo(CharacterAssetStatus.PENDING);
        assertThat(pendingResult.representativeAssetUrl()).isNull();
        assertThat(pendingResult.defaultCharacterAssets()).isFalse();
        assertThat(professorCharacterAssetRepository.findAllByProfessorIdOrderByVariantKey(professorId)).isEmpty();
        assertThat(nanobananaClient.requests).singleElement().satisfies(request -> {
            assertThat(request.professorId()).isEqualTo(professorId);
            assertThat(request.variantKeys()).containsExactly("idle_neutral", "idle_smile", "study_focus");
        });

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult readyResult =
            service.completeGeneration(
                professorId,
                PersonalityType.GENTLE,
                List.of(
                    new ProfessorCharacterAssetGenerationService.GeneratedCharacterAssetPayload(
                        "idle_smile",
                        "smile".getBytes(StandardCharsets.UTF_8),
                        "image/png"
                    ),
                    new ProfessorCharacterAssetGenerationService.GeneratedCharacterAssetPayload(
                        "idle_neutral",
                        "neutral".getBytes(StandardCharsets.UTF_8),
                        "image/png"
                    )
                )
            );

        assertThat(readyResult.characterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(readyResult.defaultCharacterAssets()).isFalse();
        assertThat(readyResult.representativeAssetUrl())
            .isEqualTo("https://cdn.test/generated/" + professorId + "/idle_neutral.png");
        assertThat(readyResult.characterAssets())
            .extracting(ProfessorCharacterAssetGenerationService.CharacterAssetView::variantKey)
            .containsExactly("idle_neutral", "idle_smile");
        assertThat(professorCharacterAssetRepository.findAllByProfessorIdOrderByVariantKey(professorId))
            .extracting(asset -> asset.getImageUrl())
            .containsExactly(
                "https://cdn.test/generated/" + professorId + "/idle_neutral.png",
                "https://cdn.test/generated/" + professorId + "/idle_smile.png"
            );
    }

    @Test
    void generationFailureStoresDefaultFallbackAssetsAndRepresentative() {
        UUID professorId = UUID.randomUUID();

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult result =
            service.fallbackToDefaultAssets(professorId, PersonalityType.SHY);

        assertThat(result.characterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(result.defaultCharacterAssets()).isTrue();
        assertThat(result.representativeAssetUrl())
            .isEqualTo("https://cdn.example.com/assets/default/shy/idle_neutral.png");
        assertThat(result.characterAssets())
            .extracting(ProfessorCharacterAssetGenerationService.CharacterAssetView::variantKey)
            .containsExactly("idle_neutral", "idle_smile", "study_focus");
        assertThat(professorCharacterAssetRepository.findAllByProfessorIdOrderByVariantKey(professorId))
            .hasSize(3)
            .allMatch(asset -> asset.isDefaultAsset());
    }

    @Test
    void sourcePhotoRequestFailureFallsBackToDefaultAssets() {
        UUID professorId = UUID.randomUUID();
        nanobananaClient.failNextRequest();

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult result =
            service.initializeAssets(
                professorId,
                PersonalityType.GENTLE,
                "https://cdn.example.com/source/professor.png"
            );

        assertThat(result.characterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(result.defaultCharacterAssets()).isTrue();
        assertThat(result.representativeAssetUrl())
            .isEqualTo("https://cdn.example.com/assets/default/gentle/idle_neutral.png");
        assertThat(professorCharacterAssetRepository.findAllByProfessorIdOrderByVariantKey(professorId))
            .hasSize(3)
            .allMatch(asset -> asset.isDefaultAsset());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void failedAssetReplacementRollsBackDeleteAndKeepsExistingAssets() {
        UUID professorId = UUID.randomUUID();
        service.fallbackToDefaultAssets(professorId, PersonalityType.GENTLE);

        assertThatThrownBy(() -> service.completeGeneration(
            professorId,
            PersonalityType.GENTLE,
            List.of(
                new ProfessorCharacterAssetGenerationService.GeneratedCharacterAssetPayload(
                    "idle_smile",
                    "first".getBytes(StandardCharsets.UTF_8),
                    "image/png"
                ),
                new ProfessorCharacterAssetGenerationService.GeneratedCharacterAssetPayload(
                    "idle_smile",
                    "second".getBytes(StandardCharsets.UTF_8),
                    "image/png"
                )
            )
        )).isInstanceOf(DataAccessException.class);

        assertThat(professorCharacterAssetRepository.findAllByProfessorIdOrderByVariantKey(professorId))
            .extracting(asset -> asset.getVariantKey() + ":" + asset.getImageUrl())
            .containsExactly(
                "idle_neutral:https://cdn.example.com/assets/default/gentle/idle_neutral.png",
                "idle_smile:https://cdn.example.com/assets/default/gentle/idle_smile.png",
                "study_focus:https://cdn.example.com/assets/default/gentle/study_focus.png"
            );
    }

    private static final class RecordingNanobananaClient implements NanobananaClient {

        private final List<CharacterAssetGenerationRequest> requests = new ArrayList<>();
        private boolean failNextRequest;

        @Override
        public GenerationTicket requestCharacterAssetGeneration(CharacterAssetGenerationRequest request) {
            if (failNextRequest) {
                failNextRequest = false;
                throw new IllegalStateException("nanobanana unavailable");
            }
            requests.add(request);
            return new GenerationTicket("ticket-" + requests.size());
        }

        private void failNextRequest() {
            this.failNextRequest = true;
        }
    }

    private static final class DeterministicProfessorImageStorage implements ProfessorImageStorage {

        @Override
        public String storeCharacterAsset(
            UUID professorId,
            String variantKey,
            byte[] imageBytes,
            String contentType
        ) {
            return "https://cdn.test/generated/" + professorId + "/" + variantKey + ".png";
        }
    }
}
