package ru.hse.hasslspace.serverservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.RoleInfoDto
import ru.hse.hasslspace.serverservice.model.ServerMember
import ru.hse.hasslspace.serverservice.model.ServerRole
import ru.hse.hasslspace.serverservice.model.User

@Component
class RoleToRoleInfoDtoConverter {

    fun convert(
        role: ServerRole,
        members: List<Pair<ServerMember, User>>
    ): RoleInfoDto =
        RoleInfoDto(
            id = role.id.toString(),
            name = role.name,
            color = role.color,
            position = role.position,
            members = members.map { (member, user) ->
                RoleInfoDto.RoleMemberDto(
                    id = member.id.userId.toString(),
                    name = member.name ?: user.username,
                    username = user.username,
                    status = user.status.name,
                    photoUrl = user.photoUrl
                )
            }
        )

}
