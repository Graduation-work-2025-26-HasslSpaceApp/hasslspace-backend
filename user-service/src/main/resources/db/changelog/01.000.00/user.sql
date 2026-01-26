-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:1 logicalFilePath:01.000.00/user.sql
create table "user"
(
    id          uuid               default gen_random_uuid()
        primary key,
    username    varchar   not null,
    name        varchar   not null,
    email       varchar   not null,
    password    varchar   not null,
    photo_url   varchar,
    user_info   varchar,
    status      varchar,
    roles       varchar[] not null default '{DEFAULT}',
    is_verified boolean   not null default false
);
-- rollback drop table "user";
