-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.5 logicalFilePath:01.000.00/channel.sql
create table channel
(
    id          uuid default gen_random_uuid()
        primary key,
    server_id   uuid not null references server (id) on delete cascade,
    name        varchar not null,
    type        varchar not null, -- TEXT | VOICE
    position    int,
    max_members int,
    is_private  boolean not null default false
);
-- rollback drop table channel;