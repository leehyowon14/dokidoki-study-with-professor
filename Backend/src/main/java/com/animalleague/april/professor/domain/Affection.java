package com.animalleague.april.professor.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "affections",
    uniqueConstraints = @UniqueConstraint(name = "uk_affections_user_professor", columnNames = {"user_id", "professor_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class Affection {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "professor_id", nullable = false)
    private UUID professorId;

    @Column(name = "affection_score", nullable = false)
    private int affectionScore;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Affection() {
    }

    private Affection(UUID userId, UUID professorId, int affectionScore) {
        this.userId = userId;
        this.professorId = professorId;
        this.affectionScore = affectionScore;
    }

    public static Affection create(UUID userId, UUID professorId, int affectionScore) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        if (professorId == null) {
            throw new IllegalArgumentException("professorId는 필수입니다.");
        }

        if (affectionScore < 0 || affectionScore > 100) {
            throw new IllegalArgumentException("affectionScore는 0 이상 100 이하여야 합니다.");
        }

        return new Affection(userId, professorId, affectionScore);
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

    public UUID getProfessorId() {
        return professorId;
    }

    public int getAffectionScore() {
        return affectionScore;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
