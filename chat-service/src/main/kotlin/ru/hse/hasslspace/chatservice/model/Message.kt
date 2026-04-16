package ru.hse.hasslspace.chatservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.chatservice.model.Message.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.UUID

@Table(TABLE_NAME)
data class Message(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(CHAT_ID_COLUMN_NAME)
    val chatId: UUID,

    @Column(USER_ID_COLUMN_NAME)
    val userId: UUID,

    @Column(CONTENT_COLUMN_NAME)
    var content: String? = null,

    @Column(FILE_URL_COLUMN_NAME)
    var fileUrl: String? = null,

    @Column(CREATED_AT_COLUMN_NAME)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(EDITED_AT_COLUMN_NAME)
    var editedAt: LocalDateTime? = null
) {

    companion object {
        const val TABLE_NAME = "message"

        const val ID_COLUMN_NAME = "id"
        const val CHAT_ID_COLUMN_NAME = "chat_id"
        const val USER_ID_COLUMN_NAME = "user_id"
        const val CONTENT_COLUMN_NAME = "content"
        const val FILE_URL_COLUMN_NAME = "file_url"
        const val CREATED_AT_COLUMN_NAME = "created_at"
        const val EDITED_AT_COLUMN_NAME = "edited_at"
    }
}