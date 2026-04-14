-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:3.1 logicalFilePath:chat.sql
create table chat
(
    id         uuid default gen_random_uuid() primary key,
    type       varchar not null,
    channel_id uuid references channel (id)
);
-- rollback drop table chat;