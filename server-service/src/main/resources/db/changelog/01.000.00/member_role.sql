-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.4 logicalFilePath:01.000.00/member_role.sql
create table member_role
(
    server_id uuid not null,
    user_id   uuid not null,
    role_id   uuid not null references server_role (id) on delete cascade,
    foreign key (server_id, user_id) references server_member (server_id, user_id) on delete cascade,
    primary key (server_id, user_id, role_id)
);
-- rollback drop table member_role;