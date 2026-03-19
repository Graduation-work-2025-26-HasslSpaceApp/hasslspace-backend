package ru.hse.hasslspace.userservice.dto

import java.util.UUID

data class UserProfileDto(
    val id: UUID,
    val username: String,
    val name: String,
    val photoUrl: String?,
    val description: String?,
    val friendStatus: StatusType
) {

    enum class StatusType {
        FRIEND,
        INCOMING_REQUEST,
        OUTGOING_REQUEST,
        BLOCKED,
        NONE
    }
}
