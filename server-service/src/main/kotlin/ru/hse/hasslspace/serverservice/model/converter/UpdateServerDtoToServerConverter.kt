package ru.hse.hasslspace.serverservice.model.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.UpdateServerDto
import ru.hse.hasslspace.serverservice.model.Server

@Component
class UpdateServerDtoToServerConverter {

    fun convert(server: Server, request: UpdateServerDto): Server =
        Server(
            id = server.id,
            name = request.name ?: server.name,
            ownerId = server.ownerId,
            iconUrl = request.iconUrl ?: server.iconUrl,
            createdAt = server.createdAt
        )
}