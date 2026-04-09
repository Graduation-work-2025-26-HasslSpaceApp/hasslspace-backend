package ru.hse.hasslspace.serverservice.model.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.UpdateServerRoleDto
import ru.hse.hasslspace.serverservice.model.ServerRole

@Component
class UpdateServerRoleDtoToServerRoleConverter {

    fun convert(serverRole: ServerRole, roleDto: UpdateServerRoleDto): ServerRole {
        return ServerRole(
            id = serverRole.id,
            serverId = serverRole.serverId,
            name = roleDto.name ?: serverRole.name,
            position = roleDto.position ?: serverRole.position,
            color = roleDto.color ?: serverRole.color,
            isDefault = serverRole.isDefault
        )
    }
}