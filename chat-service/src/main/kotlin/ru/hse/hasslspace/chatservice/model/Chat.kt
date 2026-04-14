package ru.hse.hasslspace.chatservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.chatservice.model.Chat.Companion.TABLE_NAME
import java.util.*

@Table(TABLE_NAME)
data class Chat(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(TYPE_COLUMN_NAME)
    var type: ChatType,

    @Column(CHANNEL_ID_COLUMN_NAME)
    var channelId: UUID? = null
) {

    enum class ChatType {
        PRIVATE,
        CHANNEL
    }

    companion object {
        const val TABLE_NAME = "chat"

        const val ID_COLUMN_NAME = "id"
        const val TYPE_COLUMN_NAME = "type"
        const val CHANNEL_ID_COLUMN_NAME = "channel_id"
    }
}