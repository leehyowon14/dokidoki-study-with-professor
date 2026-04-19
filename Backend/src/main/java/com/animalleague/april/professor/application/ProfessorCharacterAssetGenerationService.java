package com.animalleague.april.professor.application;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.lang.Nullable;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.PersonalityType;
import com.animalleague.april.professor.domain.ProfessorCharacterAsset;
import com.animalleague.april.professor.infrastructure.NanobananaClient;
import com.animalleague.april.professor.infrastructure.ProfessorCharacterAssetRepository;
import com.animalleague.april.professor.infrastructure.ProfessorImageStorage;

public class ProfessorCharacterAssetGenerationService {

    private final ProfessorCharacterAssetRepository professorCharacterAssetRepository;
    private final DefaultCharacterAssetCatalog defaultCharacterAssetCatalog;
    private final NanobananaClient nanobananaClient;
    private final ProfessorImageStorage professorImageStorage;

    public ProfessorCharacterAssetGenerationService(
        ProfessorCharacterAssetRepository professorCharacterAssetRepository,
        DefaultCharacterAssetCatalog defaultCharacterAssetCatalog,
        NanobananaClient nanobananaClient,
        ProfessorImageStorage professorImageStorage
    ) {
        this.professorCharacterAssetRepository = professorCharacterAssetRepository;
        this.defaultCharacterAssetCatalog = defaultCharacterAssetCatalog;
        this.nanobananaClient = nanobananaClient;
        this.professorImageStorage = professorImageStorage;
    }

    public ProfessorCharacterAssetGenerationResult initializeAssets(
        UUID professorId,
        PersonalityType personalityType,
        @Nullable String sourcePhotoUrl
    ) {
        professorCharacterAssetRepository.deleteAllByProfessorId(professorId);

        if (sourcePhotoUrl == null || sourcePhotoUrl.isBlank()) {
            return fallbackToDefaultAssets(professorId, personalityType);
        }

        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet defaultAssetSet =
            defaultCharacterAssetCatalog.getAssetSet(personalityType);

        nanobananaClient.requestCharacterAssetGeneration(
            new NanobananaClient.CharacterAssetGenerationRequest(
                professorId,
                personalityType,
                sourcePhotoUrl,
                defaultAssetSet.variantKeys()
            )
        );

        return ProfessorCharacterAssetGenerationResult.pending();
    }

    public ProfessorCharacterAssetGenerationResult completeGeneration(
        UUID professorId,
        PersonalityType personalityType,
        List<GeneratedCharacterAssetPayload> generatedAssets
    ) {
        if (generatedAssets == null || generatedAssets.isEmpty()) {
            return fallbackToDefaultAssets(professorId, personalityType);
        }

        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet defaultAssetSet =
            defaultCharacterAssetCatalog.getAssetSet(personalityType);
        Map<String, Integer> variantOrder = defaultAssetSet.variantOrder();

        List<ProfessorCharacterAsset> assetsToPersist = generatedAssets.stream()
            .sorted(assetComparator(variantOrder))
            .map(asset -> ProfessorCharacterAsset.generated(
                professorId,
                asset.variantKey(),
                professorImageStorage.storeCharacterAsset(
                    professorId,
                    asset.variantKey(),
                    asset.imageBytes(),
                    asset.contentType()
                )
            ))
            .toList();

        professorCharacterAssetRepository.deleteAllByProfessorId(professorId);
        List<ProfessorCharacterAsset> persistedAssets = professorCharacterAssetRepository.saveAll(assetsToPersist);
        return readyFromAssets(defaultAssetSet.representativeVariantKey(), false, persistedAssets);
    }

    public ProfessorCharacterAssetGenerationResult fallbackToDefaultAssets(
        UUID professorId,
        PersonalityType personalityType
    ) {
        DefaultCharacterAssetCatalog.DefaultCharacterAssetSet defaultAssetSet =
            defaultCharacterAssetCatalog.getAssetSet(personalityType);

        List<ProfessorCharacterAsset> fallbackAssets = defaultAssetSet.assets().stream()
            .map(asset -> ProfessorCharacterAsset.defaultAsset(professorId, asset.variantKey(), asset.imageUrl()))
            .toList();

        professorCharacterAssetRepository.deleteAllByProfessorId(professorId);
        List<ProfessorCharacterAsset> persistedAssets = professorCharacterAssetRepository.saveAll(fallbackAssets);
        return readyFromAssets(defaultAssetSet.representativeVariantKey(), true, persistedAssets);
    }

    private Comparator<GeneratedCharacterAssetPayload> assetComparator(Map<String, Integer> variantOrder) {
        return Comparator
            .comparingInt((GeneratedCharacterAssetPayload asset) ->
                variantOrder.getOrDefault(asset.variantKey(), Integer.MAX_VALUE))
            .thenComparing(GeneratedCharacterAssetPayload::variantKey);
    }

    private ProfessorCharacterAssetGenerationResult readyFromAssets(
        String representativeVariantKey,
        boolean defaultCharacterAssets,
        List<ProfessorCharacterAsset> persistedAssets
    ) {
        List<CharacterAssetView> characterAssets = persistedAssets.stream()
            .map(asset -> new CharacterAssetView(
                asset.getVariantKey(),
                asset.getImageUrl(),
                asset.isDefaultAsset()
            ))
            .toList();

        String representativeAssetUrl = characterAssets.stream()
            .filter(asset -> asset.variantKey().equals(representativeVariantKey))
            .map(CharacterAssetView::imageUrl)
            .findFirst()
            .orElseGet(() -> characterAssets.stream()
                .findFirst()
                .map(CharacterAssetView::imageUrl)
                .orElse(null));

        return ProfessorCharacterAssetGenerationResult.ready(
            representativeAssetUrl,
            defaultCharacterAssets,
            characterAssets
        );
    }

    public record GeneratedCharacterAssetPayload(
        String variantKey,
        byte[] imageBytes,
        String contentType
    ) {
    }

    public record CharacterAssetView(
        String variantKey,
        String imageUrl,
        boolean defaultAsset
    ) {
    }

    public record ProfessorCharacterAssetGenerationResult(
        CharacterAssetStatus characterAssetStatus,
        @Nullable String representativeAssetUrl,
        boolean defaultCharacterAssets,
        List<CharacterAssetView> characterAssets
    ) {

        public ProfessorCharacterAssetGenerationResult {
            characterAssets = List.copyOf(characterAssets);
        }

        public static ProfessorCharacterAssetGenerationResult pending() {
            return new ProfessorCharacterAssetGenerationResult(
                CharacterAssetStatus.PENDING,
                null,
                false,
                List.of()
            );
        }

        public static ProfessorCharacterAssetGenerationResult ready(
            @Nullable String representativeAssetUrl,
            boolean defaultCharacterAssets,
            List<CharacterAssetView> characterAssets
        ) {
            return new ProfessorCharacterAssetGenerationResult(
                CharacterAssetStatus.READY,
                representativeAssetUrl,
                defaultCharacterAssets,
                characterAssets
            );
        }
    }
}
