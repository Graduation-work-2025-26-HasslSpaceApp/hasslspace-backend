package ru.hse.hasslspace.serverservice.dto

import java.util.UUID

data class FriendsListDto(
    val id: UUID,
    val username: String,
    val name: String,
    val photoUrl: String?,
    val status: String,
    val type: Type
) {

    enum class Type {
        FRIEND,
        INCOMING_REQUEST,
        OUTGOING_REQUEST
    }
}
