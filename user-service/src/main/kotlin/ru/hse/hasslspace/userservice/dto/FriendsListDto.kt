package ru.hse.hasslspace.userservice.dto

import java.util.UUID

data class FriendsListDto(
    val id: UUID,
    val username: String,
    val name: String,
    val photoUrl: String?,
    val type: Type,
    val status: String
) {

    enum class Type {
        FRIEND,
        INCOMING_REQUEST,
        OUTGOING_REQUEST
    }
}
