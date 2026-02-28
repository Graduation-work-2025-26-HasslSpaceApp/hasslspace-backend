-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.3 logicalFilePath:01.000.00/server_role.sql
create table server_role
(
    id          uuid default gen_random_uuid()
        primary key,
    server_id   uuid not null references server (id) on delete cascade,
    name        varchar not null,
    position    int,
    color       varchar,
    is_default  boolean not null default false
);
-- rollback drop table server_role;