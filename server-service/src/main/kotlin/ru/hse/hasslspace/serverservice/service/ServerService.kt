package ru.hse.hasslspace.serverservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.serverservice.dto.CreateServerRequest
import ru.hse.hasslspace.serverservice.dto.ServerInfoExpandedDto
import ru.hse.hasslspace.serverservice.dto.ServersListDto
import ru.hse.hasslspace.serverservice.dto.UpdateServerDto
import ru.hse.hasslspace.serverservice.dto.converter.ServerToServerInfoExpandedDtoConverter
import ru.hse.hasslspace.serverservice.dto.converter.ServerToServersListDtoConverter
import ru.hse.hasslspace.serverservice.model.*
import ru.hse.hasslspace.serverservice.model.converter.UpdateServerDtoToServerConverter
import ru.hse.hasslspace.serverservice.repository.*
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
    private val serverToServerInfoExpandedDtoConverter: ServerToServerInfoExpandedDtoConverter,
    private val updateServerDtoToServerConverter: UpdateServerDtoToServerConverter,
    private val serverToServersListDtoConverter: ServerToServersListDtoConverter,
) {

    @Transactional
    fun createServer(ownerId: UUID, request: CreateServerRequest): ResponseEntity<String> {
        return try {
            val savedServer = serverRepository.save(
                Server(
                    name = request.name,
                    ownerId = ownerId,
                    iconUrl = request.iconUrl,
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
                    name = userRepository.findUserByUserId(ownerId).name,
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

    @Transactional
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

    @Transactional
    // todo: исправить
    fun getAllUserServers(userId: UUID): ResponseEntity<List<ServersListDto>> {
        return try {
            val serverIds = serverMemberRepository.findAllByUserId(userId).map { it.id.serverId }
            if (serverIds.isEmpty()) {
                logger.info("User with id $userId has no servers.")
                return ResponseEntity.ok(emptyList())
            }

            val servers = serverRepository.findAllServersById(serverIds)

            logger.info("User with id $userId requested all their servers. Found ${servers.size} servers.")

            ResponseEntity.ok(
                servers.map { server -> serverToServersListDtoConverter.convert(server) }
            )
        } catch (e: Exception) {
            logger.error("Error while getting all user servers", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun getSharedServers(userId: UUID, friendId: UUID): ResponseEntity<List<ServersListDto>> {
        return try {
            val userServerIds = serverMemberRepository.findAllByUserId(userId).map { it.id.serverId }
            if (userServerIds.isEmpty()) {
                logger.info("User with id $userId has no shared servers with user $friendId.")
                return ResponseEntity.ok(emptyList())
            }

            val friendServerIds = serverMemberRepository.findAllByUserId(friendId).map { it.id.serverId }
            if (friendServerIds.isEmpty()) {
                logger.info("User with id $friendId has no shared servers with user $userId.")
                return ResponseEntity.ok(emptyList())
            }

            val sharedServerIds = userServerIds.intersect(friendServerIds.toSet())
            if (sharedServerIds.isEmpty()) {
                logger.info("Users with ids $userId and $friendId have no shared servers.")
                return ResponseEntity.ok(emptyList())
            }

            val sharedServers = serverRepository.findAllServersById(sharedServerIds.toList())

            logger.info("Users with ids $userId and $friendId have ${sharedServers.size} shared servers.")

            ResponseEntity.ok(
                sharedServers.map { server -> serverToServersListDtoConverter.convert(server) }
            )

        } catch (e: Exception) {
            logger.error("Error while getting all user shared servers", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun updateServer(userId: UUID, serverId: UUID, request: UpdateServerDto): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Сервер не найден")

            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец может обновить сервер")
            }

            serverRepository.save(updateServerDtoToServerConverter.convert(server, request))

            logger.info("Server with id $serverId successfully updated by user $userId")

            ResponseEntity.ok("Сервер успешно обновлен")
        } catch (e: Exception) {
            logger.error("Error while updating server", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при обновлении сервера")
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