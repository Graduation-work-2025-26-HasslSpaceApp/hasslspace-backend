-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2 logicalFilePath:01.000.00/verification.sql
create table verification
(
    id         uuid default gen_random_uuid()
        primary key,
    user_id    uuid    not null references "user" (id) on delete cascade,
    code       varchar not null,
    created_at timestamp
);
-- rollback drop table "verification";
