package ru.hse.hasslspace.serverservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.ServersListDto
import ru.hse.hasslspace.serverservice.model.Server

@Component
class ServerToServersListDtoConverter {

    fun convert(server: Server): ServersListDto {
        return ServersListDto(
            serverId = server.id!!,
            serverName = server.name,
            photoUrl = server.iconUrl
        )
    }
}