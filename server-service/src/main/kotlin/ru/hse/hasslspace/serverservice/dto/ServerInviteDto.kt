package ru.hse.hasslspace.serverservice.dto

import java.time.LocalDateTime
import java.util.UUID

data class ServerInviteDto(
    val code: String,
    val serverId: UUID,
    val creatorId: UUID,
    val expiresAt: LocalDateTime,
    val serverName: String? = null,
    val creatorName: String? = null,
    val inviteUrl : String? = null
)
