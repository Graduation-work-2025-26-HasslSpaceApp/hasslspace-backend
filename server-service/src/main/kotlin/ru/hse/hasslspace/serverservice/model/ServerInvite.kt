package ru.hse.hasslspace.serverservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.serverservice.model.ServerInvite.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*

@Table(TABLE_NAME)
data class ServerInvite(
    @Id
    @Column(CODE_COLUMN_NAME)
    val code: String,

    @Column(SERVER_ID_COLUMN_NAME)
    val serverId: UUID,

    @Column(CREATOR_ID_COLUMN_NAME)
    val creatorId: UUID,

    @Column(EXPIRES_AT_COLUMN_NAME)
    val expiresAt: LocalDateTime?
) {

    companion object {
        const val TABLE_NAME = "server_invite"

        const val CODE_COLUMN_NAME = "code"
        const val SERVER_ID_COLUMN_NAME = "server_id"
        const val CREATOR_ID_COLUMN_NAME = "creator_id"
        const val EXPIRES_AT_COLUMN_NAME = "expires_at"
    }
}