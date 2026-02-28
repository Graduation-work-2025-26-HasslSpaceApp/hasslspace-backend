package ru.hse.hasslspace.serverservice.model

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.serverservice.model.MemberRole.Companion.TABLE_NAME
import java.util.*

@Table(TABLE_NAME)
data class MemberRole(
    @Column(SERVER_ID_COLUMN_NAME)
    val serverId: UUID,

    @Column(USER_ID_COLUMN_NAME)
    val userId: UUID,

    @Column(ROLE_ID_COLUMN_NAME)
    val roleId: UUID
) {

    companion object {
        const val TABLE_NAME = "member_role"

        const val SERVER_ID_COLUMN_NAME = "server_id"
        const val USER_ID_COLUMN_NAME = "user_id"
        const val ROLE_ID_COLUMN_NAME = "role_id"
    }
}