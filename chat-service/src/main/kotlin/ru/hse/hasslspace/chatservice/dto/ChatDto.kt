package ru.hse.hasslspace.chatservice.dto

import java.util.UUID

data class ChatDto(
    val id: UUID,
    val name: String? = null,
    val chatMembers: List<ChatMemberDto>
) {

    data class ChatMemberDto(
        val id: UUID,
        val name: String,
        val username: String,
        val status: String,
        val photoUrl: String?
    )
}