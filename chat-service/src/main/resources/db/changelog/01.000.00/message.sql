-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:3.2 logicalFilePath:message.sql
create table message
(
    id         uuid default gen_random_uuid() primary key,
    chat_id    uuid    not null references chat (id),
    user_id    uuid    not null references "user" (id),
    content    text,
    file_url   varchar,
    created_at timestamp,
    edited_at  timestamp
);
-- rollback drop table message;