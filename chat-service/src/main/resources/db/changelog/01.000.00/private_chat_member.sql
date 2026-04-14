-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:3.3 logicalFilePath:private_chat_member.sql
create table private_chat_member
(
    chat_id uuid not null references chat (id),
    user_id uuid not null references "user" (id),

    unique (chat_id, user_id)
);
-- rollback drop table private_chat_member;