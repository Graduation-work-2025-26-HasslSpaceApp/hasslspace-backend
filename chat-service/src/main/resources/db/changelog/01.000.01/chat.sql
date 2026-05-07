-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:3.4 logicalFilePath:chat.sql
alter table chat
    drop constraint chat_channel_id_fkey,
    add constraint chat_channel_id_fkey
        foreign key (channel_id) references channel(id)
            on delete cascade;
-- rollback alter table chat drop constraint chat_channel_id_fkey, add constraint chat_channel_id_fkey foreign key (channel_id) references channel(id);