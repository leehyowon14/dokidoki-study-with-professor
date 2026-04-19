package com.animalleague.april.professor.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.animalleague.april.common.domain.PersonalityType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DefaultCharacterAssetCatalog {

    private static final String RESOURCE_PATH = "classpath:seed/characters/default-character-assets.json";

    private final Map<PersonalityType, DefaultCharacterAssetSet> assetSets;

    public DefaultCharacterAssetCatalog(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.assetSets = loadCatalog(objectMapper, resourceLoader);
    }

    public DefaultCharacterAssetSet getAssetSet(PersonalityType personalityType) {
        DefaultCharacterAssetSet assetSet = assetSets.get(personalityType);
        if (assetSet == null) {
            throw new IllegalArgumentException("기본 캐릭터 에셋이 정의되지 않은 personalityType 입니다: " + personalityType.value());
        }
        return assetSet;
    }

    private Map<PersonalityType, DefaultCharacterAssetSet> loadCatalog(
        ObjectMapper objectMapper,
        ResourceLoader resourceLoader
    ) {
        try (InputStream inputStream = resourceLoader.getResource(RESOURCE_PATH).getInputStream()) {
            CatalogDocument catalog = objectMapper.readValue(inputStream, CatalogDocument.class);
            return catalog.personalities().stream()
                .map(entry -> new DefaultCharacterAssetSet(
                    PersonalityType.fromValue(entry.personalityType()),
                    entry.representativeVariantKey(),
                    entry.assets().stream()
                        .map(asset -> new DefaultCharacterAsset(asset.variantKey(), asset.imageUrl()))
                        .toList()
                ))
                .collect(Collectors.toUnmodifiableMap(DefaultCharacterAssetSet::personalityType, Function.identity()));
        } catch (IOException exception) {
            throw new UncheckedIOException("기본 캐릭터 에셋 카탈로그를 불러오지 못했습니다.", exception);
        }
    }

    public record DefaultCharacterAssetSet(
        PersonalityType personalityType,
        String representativeVariantKey,
        List<DefaultCharacterAsset> assets
    ) {

        public DefaultCharacterAssetSet {
            assets = List.copyOf(assets);
        }

        public List<String> variantKeys() {
            return assets.stream()
                .map(DefaultCharacterAsset::variantKey)
                .toList();
        }

        public Map<String, Integer> variantOrder() {
            List<String> variantKeys = variantKeys();
            return variantKeys.stream()
                .collect(Collectors.toMap(Function.identity(), variantKeys::indexOf));
        }
    }

    public record DefaultCharacterAsset(String variantKey, String imageUrl) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CatalogDocument(List<CatalogEntry> personalities) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CatalogEntry(
        String personalityType,
        String representativeVariantKey,
        List<AssetEntry> assets
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AssetEntry(String variantKey, String imageUrl) {
    }
}
