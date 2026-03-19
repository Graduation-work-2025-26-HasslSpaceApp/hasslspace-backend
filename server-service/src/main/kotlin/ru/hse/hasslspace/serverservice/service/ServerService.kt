package ru.hse.hasslspace.serverservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.serverservice.dto.ServerInfoExpandedDto
import ru.hse.hasslspace.serverservice.dto.ServersListDto
import ru.hse.hasslspace.serverservice.dto.converter.ServerToServerInfoExpandedDtoConverter
import ru.hse.hasslspace.serverservice.model.Channel
import ru.hse.hasslspace.serverservice.model.MemberRole
import ru.hse.hasslspace.serverservice.model.Server
import ru.hse.hasslspace.serverservice.model.ServerMember
import ru.hse.hasslspace.serverservice.model.ServerRole
import ru.hse.hasslspace.serverservice.repository.ChannelRepository
import ru.hse.hasslspace.serverservice.repository.MemberRoleRepository
import ru.hse.hasslspace.serverservice.repository.ServerMemberRepository
import ru.hse.hasslspace.serverservice.repository.ServerRepository
import ru.hse.hasslspace.serverservice.repository.ServerRoleRepository
import ru.hse.hasslspace.serverservice.repository.UserRepository
import java.time.LocalDateTime
import java.util.*

@Service
class ServerService(
    private val serverRepository: ServerRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val serverRoleRepository: ServerRoleRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val channelRepository: ChannelRepository,
    private val userRepository: UserRepository,
    private val serverToServerInfoExpandedDtoConverter: ServerToServerInfoExpandedDtoConverter
) {

    @Transactional
    fun createServer(ownerId: UUID, serverName: String, username: String): ResponseEntity<String> {
        return try {
            val savedServer = serverRepository.save(
                Server(
                    name = serverName,
                    ownerId = ownerId,
                    createdAt = LocalDateTime.now()
                )
            )

            val adminRole = serverRoleRepository.save(
                ServerRole(
                    serverId = savedServer.id!!,
                    name = "Admin",
                    position = 1,
                    isDefault = false
                )
            )

            val memberRole = serverRoleRepository.save(
                ServerRole(
                    serverId = savedServer.id,
                    name = "Member",
                    position = 2,
                    isDefault = true
                )
            )

            serverMemberRepository.save(
                ServerMember(
                    ServerMember.ServerMemberId(
                        serverId = savedServer.id,
                        userId = ownerId
                    ),
                    joinedAt = LocalDateTime.now(),
                    name = username
                )
            )

            memberRoleRepository.save(
                MemberRole(
                    MemberRole.MemberRoleId(
                        serverId = savedServer.id,
                        userId = ownerId,
                        roleId = adminRole.id!!
                    )
                )
            )
            logger.info("Server with id ${savedServer.id} successfully created by user $ownerId")
            logger.info("Created roles: Admin (${adminRole.id}) and Member (${memberRole.id}) for server ${savedServer.id}")

            ResponseEntity.status(HttpStatus.CREATED).body("Сервер успешно создан")
        } catch (e: Exception) {
            logger.error("Error while creating server", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при создании сервера")
        }
    }

    fun getServer(userId: UUID, serverId: UUID): ResponseEntity<ServerInfoExpandedDto> {
        return try {
            serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            val members = serverMemberRepository.findAllByServerId(serverId)
            val channels = channelRepository.findAllByServerIdOrderByPosition(serverId)

            val memberDtos = members.map { member ->
                val roleIds = memberRoleRepository.findRoleIdsByServerIdAndUserId(serverId, member.id.userId)
                val roles = serverRoleRepository.findAllRolesById(roleIds.toList())
                val user = userRepository.findUserByUserId(member.id.userId)

                serverToServerInfoExpandedDtoConverter.toMemberDto(
                    member = member,
                    user = user,
                    roles = roles
                )
            }

            val (textChannelDtos, voiceChannelDtos) = channels
                .partition { it.type == Channel.ChannelType.TEXT }
                .let { (textChannels, voiceChannels) ->
                    serverToServerInfoExpandedDtoConverter.toTextAndVoiceChannelDtos(
                        textChannels = textChannels,
                        voiceChannels = voiceChannels
                    )
                }

            ResponseEntity.ok(
                serverToServerInfoExpandedDtoConverter.convert(
                    server = server,
                    memberDtos = memberDtos,
                    userId = userId,
                    textChannelDtos = textChannelDtos,
                    voiceChannelDtos = voiceChannelDtos
                )
            )
        } catch (e: Exception) {
            logger.error("Error while getting server expanded info", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // todo: исправить
    fun getAllUserServers(userId: UUID): ResponseEntity<List<ServersListDto>> {
        return try {
            val serverIds = serverMemberRepository.findAllByUserId(userId).map { it.id.serverId }
            val servers = serverRepository.findAllServersById(serverIds)

            logger.info("User with id $userId requested all their servers. Found ${servers.size} servers.")

            ResponseEntity.ok(
                servers.map { server ->
                    ServersListDto(
                        serverId = server.id!!,
                        serverName = server.name,
                        photoUrl = server.iconUrl
                    )
                }
            )
        } catch (e: Exception) {
            logger.error("Error while getting all user servers", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun deleteServer(userId: UUID, serverId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findById(serverId)

            if (server.isEmpty) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")
            }

            if (server.get().ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец может удалить сервер")
            }

            serverRepository.deleteById(serverId)

            logger.info("Server with id $serverId successfully deleted by user $userId")

            ResponseEntity.ok("Сервер успешно удален")
        } catch (e: Exception) {
            logger.error("Error while deleting server", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при удалении сервера")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ServerService::class.java)
    }
}