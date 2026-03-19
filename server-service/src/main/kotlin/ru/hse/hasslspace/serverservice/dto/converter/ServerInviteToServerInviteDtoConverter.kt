package ru.hse.hasslspace.serverservice.dto.converter

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.ServerInviteDto
import ru.hse.hasslspace.serverservice.model.ServerInvite

@Component
class ServerInviteToServerInviteDtoConverter(
    @Value("\${invite_base_url}")
    private val inviteBaseUrl: String
) {

    fun convert(
        serverInvite: ServerInvite,
        serverName: String? = null,
        creatorName: String? = null
    ): ServerInviteDto {
        return ServerInviteDto(
            code = serverInvite.code,
            serverId = serverInvite.serverId,
            creatorId = serverInvite.creatorId,
            expiresAt = serverInvite.expiresAt!!,
            serverName = serverName,
            creatorName = creatorName,
            inviteUrl = inviteBaseUrl + serverInvite.code
        )
    }
}