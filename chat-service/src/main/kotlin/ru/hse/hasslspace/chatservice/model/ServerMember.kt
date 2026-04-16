package ru.hse.hasslspace.chatservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.chatservice.model.ServerMember.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*

@Table(TABLE_NAME)
data class ServerMember(
    @Id
    val id: ServerMemberId,

    @Column(JOINED_AT_COLUMN_NAME)
    val joinedAt: LocalDateTime?,

    @Column(NAME_COLUMN_NAME)
    var name: String? = null
) {

    data class ServerMemberId(
        @Column(SERVER_ID_COLUMN_NAME)
        val serverId: UUID,

        @Column(USER_ID_COLUMN_NAME)
        val userId: UUID
    )

    companion object {
        const val TABLE_NAME = "server_member"

        const val SERVER_ID_COLUMN_NAME = "server_id"
        const val USER_ID_COLUMN_NAME = "user_id"
        const val JOINED_AT_COLUMN_NAME = "joined_at"
        const val NAME_COLUMN_NAME = "name"
    }
}
