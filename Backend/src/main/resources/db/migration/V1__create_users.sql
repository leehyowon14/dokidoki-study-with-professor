create table users (
    id uuid primary key,
    login_id varchar(50) not null,
    name varchar(100) not null,
    password_hash varchar(255) not null,
    exam_end_date date not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint uk_users_login_id unique (login_id)
);
