package ru.hse.hasslspace.voiceservice.dto

data class TokenRequest(
    val name: String,
    val roomName: String,
    val roomType: RoomType? = null
) {

    enum class RoomType {
        SERVER,
        PRIVATE_ROOM
    }
}
