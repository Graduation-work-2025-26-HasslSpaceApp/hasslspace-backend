-- liquibase formatted sql
-- changeset aevsyukov_1@edu.hse.ru:2.6 logicalFilePath:01.000.00/channel_permission.sql
create table channel_permission
(
    channel_id  uuid not null references channel (id) on delete cascade,
    role_id     uuid not null references server_role (id) on delete cascade,
    can_read    boolean,
    can_write   boolean,
    can_manage  boolean,
    primary key (channel_id, role_id)
);
-- rollback drop table channel_permission;
