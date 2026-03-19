-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.3 logicalFilePath:01.000.00/server_invite.sql
create table server_invite
(
    code       varchar not null
        primary key,
    server_id  uuid    not null references server (id) on delete cascade,
    creator_id uuid    not null references "user" (id) on delete cascade,
    expires_at timestamp
);
-- rollback drop table server_invite;