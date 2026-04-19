package com.animalleague.april.unit.professor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.professor.application.DefaultCharacterAssetCatalog;
import com.animalleague.april.professor.application.ProfessorCharacterAssetGenerationService;
import com.animalleague.april.professor.domain.ProfessorCharacterAsset;
import com.animalleague.april.professor.infrastructure.NanobananaClient;
import com.animalleague.april.professor.infrastructure.ProfessorCharacterAssetRepository;
import com.animalleague.april.professor.infrastructure.ProfessorImageStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ProfessorCharacterAssetGenerationServiceUnitTest {

    private final ProfessorCharacterAssetRepository professorCharacterAssetRepository =
        mock(ProfessorCharacterAssetRepository.class);
    private final DefaultCharacterAssetCatalog defaultCharacterAssetCatalog =
        mock(DefaultCharacterAssetCatalog.class);
    private final NanobananaClient nanobananaClient = mock(NanobananaClient.class);
    private final ProfessorImageStorage professorImageStorage = mock(ProfessorImageStorage.class);
    private final TransactionOperations transactionOperations = new TransactionOperations() {
        @Override
        public <T> T execute(TransactionCallback<T> action) {
            return action.doInTransaction(new SimpleTransactionStatus());
        }
    };

    private ProfessorCharacterAssetGenerationService service;

    @BeforeEach
    void setUp() {
        service = new ProfessorCharacterAssetGenerationService(
            professorCharacterAssetRepository,
            defaultCharacterAssetCatalog,
            nanobananaClient,
            professorImageStorage,
            transactionOperations
        );
    }

    @Test
    void sourcePhotoRequestsNanobananaAndReturnsPending() {
        UUID professorId = UUID.randomUUID();
        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet gentleAssetSet = gentleAssetSet();
        given(defaultCharacterAssetCatalog.getAssetSet(PersonalityType.GENTLE)).willReturn(gentleAssetSet);
        given(nanobananaClient.requestCharacterAssetGeneration(any()))
            .willReturn(new NanobananaClient.GenerationTicket("ticket-1"));

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult result =
            service.initializeAssets(
                professorId,
                PersonalityType.GENTLE,
                "https://cdn.example.com/source/professor.png"
            );

        ArgumentCaptor<NanobananaClient.CharacterAssetGenerationRequest> requestCaptor =
            ArgumentCaptor.forClass(NanobananaClient.CharacterAssetGenerationRequest.class);
        verify(nanobananaClient).requestCharacterAssetGeneration(requestCaptor.capture());
        verify(professorCharacterAssetRepository).deleteAllByProfessorId(professorId);
        verify(professorCharacterAssetRepository, never()).saveAll(any());

        assertThat(requestCaptor.getValue().professorId()).isEqualTo(professorId);
        assertThat(requestCaptor.getValue().personalityType()).isEqualTo(PersonalityType.GENTLE);
        assertThat(requestCaptor.getValue().variantKeys())
            .containsExactly("idle_neutral", "idle_smile", "study_focus");
        assertThat(result.characterAssetStatus()).isEqualTo(CharacterAssetStatus.PENDING);
        assertThat(result.representativeAssetUrl()).isNull();
        assertThat(result.defaultCharacterAssets()).isFalse();
        assertThat(result.characterAssets()).isEmpty();
    }

    @Test
    void sourcePhotoGenerationRequestFailureFallsBackToDefaultAssets() {
        UUID professorId = UUID.randomUUID();
        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet gentleAssetSet = gentleAssetSet();
        AtomicReference<List<ProfessorCharacterAsset>> savedAssets = new AtomicReference<>();
        given(defaultCharacterAssetCatalog.getAssetSet(PersonalityType.GENTLE)).willReturn(gentleAssetSet);
        given(nanobananaClient.requestCharacterAssetGeneration(any()))
            .willThrow(new IllegalStateException("nanobanana timeout"));
        given(professorCharacterAssetRepository.saveAll(any())).willAnswer(invocation -> {
            List<ProfessorCharacterAsset> assets = invocation.getArgument(0);
            savedAssets.set(assets);
            return assets;
        });

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult result =
            service.initializeAssets(
                professorId,
                PersonalityType.GENTLE,
                "https://cdn.example.com/source/professor.png"
            );

        assertThat(savedAssets.get())
            .extracting(ProfessorCharacterAsset::isDefaultAsset)
            .containsOnly(true);
        assertThat(result.characterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(result.defaultCharacterAssets()).isTrue();
        assertThat(result.representativeAssetUrl())
            .isEqualTo("https://cdn.example.com/assets/default/gentle/idle_neutral.png");
    }

    @Test
    void completeGenerationStoresGeneratedAssetsAndSelectsRepresentativeVariant() {
        UUID professorId = UUID.randomUUID();
        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet gentleAssetSet = gentleAssetSet();
        AtomicReference<List<ProfessorCharacterAsset>> savedAssets = new AtomicReference<>();
        given(defaultCharacterAssetCatalog.getAssetSet(PersonalityType.GENTLE)).willReturn(gentleAssetSet);
        given(professorImageStorage.storeCharacterAsset(eq(professorId), eq("idle_smile"), any(), eq("image/png")))
            .willReturn("https://cdn.example.com/generated/professor/idle_smile.png");
        given(professorImageStorage.storeCharacterAsset(eq(professorId), eq("idle_neutral"), any(), eq("image/png")))
            .willReturn("https://cdn.example.com/generated/professor/idle_neutral.png");
        given(professorCharacterAssetRepository.saveAll(any())).willAnswer(invocation -> {
            List<ProfessorCharacterAsset> assets = invocation.getArgument(0);
            savedAssets.set(assets);
            return assets;
        });

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult result =
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

        assertThat(savedAssets.get())
            .extracting(ProfessorCharacterAsset::getVariantKey)
            .containsExactly("idle_neutral", "idle_smile");
        assertThat(result.characterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(result.representativeAssetUrl())
            .isEqualTo("https://cdn.example.com/generated/professor/idle_neutral.png");
        assertThat(result.defaultCharacterAssets()).isFalse();
        assertThat(result.characterAssets())
            .extracting(ProfessorCharacterAssetGenerationService.CharacterAssetView::variantKey)
            .containsExactly("idle_neutral", "idle_smile");
    }

    @Test
    void generationFailureFallsBackToDefaultAssets() {
        UUID professorId = UUID.randomUUID();
        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet gentleAssetSet = gentleAssetSet();
        AtomicReference<List<ProfessorCharacterAsset>> savedAssets = new AtomicReference<>();
        given(defaultCharacterAssetCatalog.getAssetSet(PersonalityType.GENTLE)).willReturn(gentleAssetSet);
        given(professorCharacterAssetRepository.saveAll(any())).willAnswer(invocation -> {
            List<ProfessorCharacterAsset> assets = invocation.getArgument(0);
            savedAssets.set(assets);
            return assets;
        });

        ProfessorCharacterAssetGenerationService.ProfessorCharacterAssetGenerationResult result =
            service.fallbackToDefaultAssets(professorId, PersonalityType.GENTLE);

        assertThat(savedAssets.get())
            .extracting(ProfessorCharacterAsset::isDefaultAsset)
            .containsOnly(true);
        assertThat(result.characterAssetStatus()).isEqualTo(CharacterAssetStatus.READY);
        assertThat(result.representativeAssetUrl())
            .isEqualTo("https://cdn.example.com/assets/default/gentle/idle_neutral.png");
        assertThat(result.defaultCharacterAssets()).isTrue();
        assertThat(result.characterAssets())
            .extracting(ProfessorCharacterAssetGenerationService.CharacterAssetView::variantKey)
            .containsExactly("idle_neutral", "idle_smile", "study_focus");
    }

    private DefaultCharacterAssetCatalog.DefaultCharacterAssetSet gentleAssetSet() {
        return new DefaultCharacterAssetCatalog.DefaultCharacterAssetSet(
            PersonalityType.GENTLE,
            "idle_neutral",
            List.of(
                new DefaultCharacterAssetCatalog.DefaultCharacterAsset(
                    "idle_neutral",
                    "https://cdn.example.com/assets/default/gentle/idle_neutral.png"
                ),
                new DefaultCharacterAssetCatalog.DefaultCharacterAsset(
                    "idle_smile",
                    "https://cdn.example.com/assets/default/gentle/idle_smile.png"
                ),
                new DefaultCharacterAssetCatalog.DefaultCharacterAsset(
                    "study_focus",
                    "https://cdn.example.com/assets/default/gentle/study_focus.png"
                )
            )
        );
    }
}
