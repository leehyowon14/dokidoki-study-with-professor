create table professors (
    id uuid primary key,
    user_id uuid not null,
    professor_name varchar(100) not null,
    gender varchar(16) not null,
    personality_type varchar(32) not null,
    source_photo_url text null,
    character_asset_status varchar(16) not null,
    representative_asset_url text null,
    is_default_character_assets boolean not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table affections (
    id uuid primary key,
    user_id uuid not null,
    professor_id uuid not null references professors (id) on delete cascade,
    affection_score integer not null check (affection_score between 0 and 100),
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_affections_user_professor unique (user_id, professor_id)
);
