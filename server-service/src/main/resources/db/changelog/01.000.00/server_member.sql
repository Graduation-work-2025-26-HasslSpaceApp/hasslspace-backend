-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.2 logicalFilePath:01.000.00/server_member.sql
create table server_member
(
    server_id  uuid not null references server (id) on delete cascade,
    user_id    uuid not null references "user" (id) on delete cascade,
    joined_at  timestamp,
    name       varchar,
    primary key (server_id, user_id)
);
-- rollback drop table server_member;