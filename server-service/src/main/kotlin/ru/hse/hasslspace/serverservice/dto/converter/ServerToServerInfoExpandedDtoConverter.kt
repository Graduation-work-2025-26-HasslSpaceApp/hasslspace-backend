package ru.hse.hasslspace.serverservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.ServerInfoExpandedDto
import ru.hse.hasslspace.serverservice.dto.ServerMemberDto
import ru.hse.hasslspace.serverservice.model.Channel
import ru.hse.hasslspace.serverservice.model.Server
import ru.hse.hasslspace.serverservice.model.ServerMember
import ru.hse.hasslspace.serverservice.model.ServerRole
import ru.hse.hasslspace.serverservice.model.User
import java.util.*

@Component
class ServerToServerInfoExpandedDtoConverter {

    fun convert(
        server: Server,
        memberDtos: List<ServerMemberDto>,
        userId: UUID,
        textChannelDtos: List<ServerInfoExpandedDto.TextChannelDto>,
        voiceChannelDtos: List<ServerInfoExpandedDto.VoiceChannelDto>
    ): ServerInfoExpandedDto =
        ServerInfoExpandedDto(
            id = server.id.toString(),
            name = server.name,
            photoUrl = server.iconUrl,
            members = memberDtos,
            isOwner = server.ownerId == userId,
            textChannels = textChannelDtos,
            voiceChannels = voiceChannelDtos
        )

    fun toMemberDto(
        member: ServerMember,
        user: User,
        roles: List<ServerRole>
    ): ServerMemberDto {
        val memberName = member.name ?: user.username

        return ServerMemberDto(
            id = member.id.userId.toString(),
            name = memberName,
            username = user.username,
            status = user.status.name,
            photoUrl = user.photoUrl,
            roles = toRoleDtos(roles)
        )
    }

    private fun toRoleDtos(roles: List<ServerRole>): List<ServerMemberDto.ServerRoleDto> {
        return roles.map { role ->
            ServerMemberDto.ServerRoleDto(
                id = role.id.toString(),
                name = role.name,
                color = role.color ?: DEFAULT_ROLE_COLOR
            )
        }
    }

    fun toTextAndVoiceChannelDtos(
        textChannels: List<Channel>,
        voiceChannels: List<Channel>
    ): Pair<List<ServerInfoExpandedDto.TextChannelDto>, List<ServerInfoExpandedDto.VoiceChannelDto>> {
        return Pair(
            textChannels.map { channel ->
                ServerInfoExpandedDto.TextChannelDto(
                    id = channel.id.toString(),
                    name = channel.name
                )
            },
            voiceChannels.map { channel ->
                ServerInfoExpandedDto.VoiceChannelDto(
                    id = channel.id.toString(),
                    name = channel.name
                )
            }
        )
    }

    companion object {
        private const val DEFAULT_ROLE_COLOR = "#99AAB5"
    }
}