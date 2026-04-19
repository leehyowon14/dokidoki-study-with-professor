CREATE TABLE professor_character_assets (
    id UUID PRIMARY KEY,
    professor_id UUID NOT NULL,
    variant_key VARCHAR(100) NOT NULL,
    image_url TEXT NOT NULL,
    is_default_asset BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_professor_character_assets_professor_variant UNIQUE (professor_id, variant_key)
);

CREATE INDEX idx_professor_character_assets_professor_id
    ON professor_character_assets (professor_id);
