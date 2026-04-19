package com.animalleague.april.professor.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "professor_character_assets",
    indexes = {
        @Index(name = "idx_professor_character_assets_professor_id", columnList = "professor_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_professor_character_assets_professor_variant",
            columnNames = {"professor_id", "variant_key"}
        )
    }
)
@EntityListeners(AuditingEntityListener.class)
public class ProfessorCharacterAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "professor_id", nullable = false)
    private UUID professorId;

    @Column(name = "variant_key", nullable = false, length = 100)
    private String variantKey;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_default_asset", nullable = false)
    private boolean defaultAsset;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProfessorCharacterAsset() {
    }

    private ProfessorCharacterAsset(
        UUID professorId,
        String variantKey,
        String imageUrl,
        boolean defaultAsset
    ) {
        this.professorId = professorId;
        this.variantKey = variantKey;
        this.imageUrl = imageUrl;
        this.defaultAsset = defaultAsset;
    }

    public static ProfessorCharacterAsset generated(
        UUID professorId,
        String variantKey,
        String imageUrl
    ) {
        return new ProfessorCharacterAsset(professorId, variantKey, imageUrl, false);
    }

    public static ProfessorCharacterAsset defaultAsset(
        UUID professorId,
        String variantKey,
        String imageUrl
    ) {
        return new ProfessorCharacterAsset(professorId, variantKey, imageUrl, true);
    }

    public UUID getId() {
        return id;
    }

    public UUID getProfessorId() {
        return professorId;
    }

    public String getVariantKey() {
        return variantKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isDefaultAsset() {
        return defaultAsset;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
