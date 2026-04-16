package ru.hse.hasslspace.chatservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.chatservice.model.Channel.Companion.TABLE_NAME
import java.util.*

@Table(TABLE_NAME)
data class Channel(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(SERVER_ID_COLUMN_NAME)
    val serverId: UUID,

    @Column(NAME_COLUMN_NAME)
    var name: String,

    @Column(TYPE_COLUMN_NAME)
    var type: ChannelType,

    @Column(POSITION_COLUMN_NAME)
    var position: Int? = null,

    @Column(MAX_MEMBERS_COLUMN_NAME)
    var maxMembers: Int? = null,

    @Column(IS_PRIVATE_COLUMN_NAME)
    var isPrivate: Boolean = false
) {

    enum class ChannelType {
        TEXT,
        VOICE
    }

    companion object {
        const val TABLE_NAME = "channel"

        const val ID_COLUMN_NAME = "id"
        const val SERVER_ID_COLUMN_NAME = "server_id"
        const val NAME_COLUMN_NAME = "name"
        const val TYPE_COLUMN_NAME = "type"
        const val POSITION_COLUMN_NAME = "position"
        const val MAX_MEMBERS_COLUMN_NAME = "max_members"
        const val IS_PRIVATE_COLUMN_NAME = "is_private"
    }
}