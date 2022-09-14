create table feedback
(
    id                     uuid not null primary key,
    user_reference_id      varchar(255) not null,
    text                   text,
    created_at             timestamp not null
);