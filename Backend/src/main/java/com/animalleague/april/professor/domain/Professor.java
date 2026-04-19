package com.animalleague.april.professor.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.animalleague.april.common.domain.CharacterAssetStatus;
import com.animalleague.april.common.domain.Gender;
import com.animalleague.april.common.domain.PersonalityType;

@Entity
@Table(name = "professors")
@EntityListeners(AuditingEntityListener.class)
public class Professor {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "professor_name", nullable = false, length = 100)
    private String professorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "personality_type", nullable = false, length = 32)
    private PersonalityType personalityType;

    @Column(name = "source_photo_url")
    private String sourcePhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_asset_status", nullable = false, length = 16)
    private CharacterAssetStatus characterAssetStatus;

    @Column(name = "representative_asset_url")
    private String representativeAssetUrl;

    @Column(name = "is_default_character_assets", nullable = false)
    private boolean isDefaultCharacterAssets;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Professor() {
    }

    private Professor(
        UUID userId,
        String professorName,
        Gender gender,
        PersonalityType personalityType,
        String sourcePhotoUrl,
        CharacterAssetStatus characterAssetStatus,
        String representativeAssetUrl,
        boolean isDefaultCharacterAssets
    ) {
        this.userId = userId;
        this.professorName = professorName;
        this.gender = gender;
        this.personalityType = personalityType;
        this.sourcePhotoUrl = sourcePhotoUrl;
        this.characterAssetStatus = characterAssetStatus;
        this.representativeAssetUrl = representativeAssetUrl;
        this.isDefaultCharacterAssets = isDefaultCharacterAssets;
    }

    public static Professor create(
        UUID userId,
        String professorName,
        Gender gender,
        PersonalityType personalityType,
        String sourcePhotoUrl
    ) {
        String normalizedSourcePhotoUrl = normalizeSourcePhotoUrl(sourcePhotoUrl);
        boolean hasSourcePhoto = normalizedSourcePhotoUrl != null;
        return new Professor(
            userId,
            professorName,
            gender,
            personalityType,
            normalizedSourcePhotoUrl,
            hasSourcePhoto ? CharacterAssetStatus.PENDING : CharacterAssetStatus.READY,
            null,
            !hasSourcePhoto
        );
    }

    private static String normalizeSourcePhotoUrl(String sourcePhotoUrl) {
        if (sourcePhotoUrl == null) {
            return null;
        }

        String trimmed = sourcePhotoUrl.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @PrePersist
    void assignIdIfAbsent() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public Gender getGender() {
        return gender;
    }

    public PersonalityType getPersonalityType() {
        return personalityType;
    }

    public String getSourcePhotoUrl() {
        return sourcePhotoUrl;
    }

    public CharacterAssetStatus getCharacterAssetStatus() {
        return characterAssetStatus;
    }

    public String getRepresentativeAssetUrl() {
        return representativeAssetUrl;
    }

    public boolean isDefaultCharacterAssets() {
        return isDefaultCharacterAssets;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
