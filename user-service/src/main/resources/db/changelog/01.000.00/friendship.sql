-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:3 logicalFilePath:01.000.00/friendship.sql
create table friendship
(
    id           uuid default gen_random_uuid()
        primary key,
    requester_id uuid    not null references "user" (id),
    addressee_id uuid    not null references "user" (id),
    status       varchar not null,
    created_at   timestamp,

    unique (requester_id, addressee_id)
);
-- rollback drop table "friendship";
