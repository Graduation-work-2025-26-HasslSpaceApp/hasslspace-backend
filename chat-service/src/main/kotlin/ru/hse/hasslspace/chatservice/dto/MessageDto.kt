package ru.hse.hasslspace.chatservice.dto

import java.time.LocalDateTime

data class MessageDto(
    var content: String? = null,
    var fileUrl: String? = null,
    val createdAt: LocalDateTime,
    var editedAt: LocalDateTime? = null
)
