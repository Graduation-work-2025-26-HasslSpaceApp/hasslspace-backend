-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.1 logicalFilePath:01.000.00/server.sql
create table server
(
    id         uuid default gen_random_uuid()
        primary key,
    name       varchar not null,
    owner_id   uuid    not null references "user" (id) on delete cascade,
    icon_url   varchar,
    created_at timestamp
);
-- rollback drop table server;