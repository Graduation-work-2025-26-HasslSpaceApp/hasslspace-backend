package ru.hse.hasslspace.serverservice.dto

import java.util.UUID

data class ServersListDto(
    val serverId: UUID,
    val serverName: String,
    val photoUrl: String?
)
