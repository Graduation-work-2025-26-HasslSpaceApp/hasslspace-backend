package ru.hse.hasslspace.chatservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.chatservice.model.PrivateChatMember.Companion.TABLE_NAME
import java.util.UUID

@Table(TABLE_NAME)
data class PrivateChatMember(
    @Id
    val id: PrivateChatMemberId
) {
    data class PrivateChatMemberId(
        @Column(CHAT_ID_COLUMN_NAME)
        val chatId: UUID,

        @Column(USER_ID_COLUMN_NAME)
        val userId: UUID
    )

    companion object {
        const val TABLE_NAME = "private_chat_member"

        const val CHAT_ID_COLUMN_NAME = "chat_id"
        const val USER_ID_COLUMN_NAME = "user_id"
    }
}