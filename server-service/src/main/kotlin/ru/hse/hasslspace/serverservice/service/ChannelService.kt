package ru.hse.hasslspace.serverservice.service

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import ru.hse.hasslspace.serverservice.dto.*
import ru.hse.hasslspace.serverservice.dto.converter.ChannelToChannelDtoConverter
import ru.hse.hasslspace.serverservice.dto.converter.RoleToRoleInfoDtoConverter
import ru.hse.hasslspace.serverservice.model.Channel
import ru.hse.hasslspace.serverservice.model.ChannelPermission
import ru.hse.hasslspace.serverservice.model.converter.UpdateChannelDtoToChannelConverter
import ru.hse.hasslspace.serverservice.repository.*
import java.util.*

@Service
class ChannelService(
    private val serverRepository: ServerRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val channelRepository: ChannelRepository,
    private val serverRoleRepository: ServerRoleRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val userRepository: UserRepository,
    private val channelPermissionRepository: ChannelPermissionRepository,
    private val channelToChannelDtoConverter: ChannelToChannelDtoConverter,
    private val updateChannelDtoToChannelConverter: UpdateChannelDtoToChannelConverter,
    private val roleToRoleInfoDtoConverter: RoleToRoleInfoDtoConverter,
) {

    @Transactional
    fun createChannel(
        userId: UUID,
        serverId: UUID,
        request: CreateChannelRequest
    ): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            // TODO: исправить, пока только владелец может создавать каналы
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Только владелец сервера может создавать каналы")
            }

            val savedChannel = channelRepository.save(
                Channel(
                    serverId = serverId,
                    name = request.name,
                    type = Channel.ChannelType.valueOf(request.type),
                    position = request.position,
                    maxMembers = request.maxMembers,
                    isPrivate = request.isPrivate
                )
            )

            if (savedChannel.isPrivate) {
                val roleIds = memberRoleRepository.findRoleIdsByServerIdAndUserId(serverId, userId)

                val adminRoleId = roleIds
                    .mapNotNull { serverRoleRepository.findById(it).orElse(null) }
                    .firstOrNull { it.name == "Admin" }
                    ?.id
                    ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Роль Admin не найдена")

                channelPermissionRepository.save(
                    ChannelPermission(
                        ChannelPermission.ChannelPermissionId(
                            channelId = savedChannel.id,
                            roleId = adminRoleId
                        ),
                        canRead = true,
                        canWrite = true,
                        canManage = true
                    )
                )
            }

            logger.info("Channel '${request.name}' created in server $serverId by user $userId")

            ResponseEntity.status(HttpStatus.CREATED).body("Канал успешно создан")
        } catch (e: Exception) {
            logger.error("Error while creating channel", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при создании канала")
        }
    }

    @Transactional
    fun getChannelInfo(userId: UUID, serverId: UUID, channelId: UUID): ResponseEntity<ChannelDto> {
        return try {
            serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            serverMemberRepository.findByServerIdAndUserId(channel.serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            // TODO: добавить проверку прав доступа для приватного канала

            ResponseEntity.ok(channelToChannelDtoConverter.convert(channel))
        } catch (e: Exception) {
            logger.error("Error while getting channel info", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun deleteChannel(userId: UUID, serverId: UUID, channelId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            //TODO: исправить, пока только владелец может удалять каналы
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может удалять каналы")
            }

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Канал не найден")

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Канал не принадлежит этому серверу")
            }

            // TODO: пока не создаем никаких доступов
            // TODO: вроде каскадно удаляется хз
            //channelPermissionRepository.deleteByChannelId(channelId)

            channelRepository.deleteById(channelId)

            logger.info("Channel $channelId deleted from server $serverId by user $userId")

            ResponseEntity.ok("Канал успешно удален")
        } catch (e: Exception) {
            logger.error("Error while deleting channel", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении канала")
        }
    }

    @Transactional
    fun updateChannel(
        userId: UUID,
        serverId: UUID,
        channelId: UUID,
        request: UpdateChannelDto
    ): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            //TODO: исправить, пока только владелец может обновлять каналы
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может изменять каналы")
            }

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Канал не найден")

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Канал не принадлежит этому серверу")
            }

            channelRepository.save(updateChannelDtoToChannelConverter.convert(channel, request))

            if (request.isPrivate != null && request.isPrivate == false) {
                channelPermissionRepository.deleteByChannelId(channelId)
            } else if (request.isPrivate != null && request.isPrivate == true) {
                val roleIds = memberRoleRepository.findRoleIdsByServerIdAndUserId(serverId, userId)

                val adminRoleId = roleIds
                    .mapNotNull { serverRoleRepository.findById(it).orElse(null) }
                    .firstOrNull { it.name == "Admin" }
                    ?.id
                    ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Роль Admin не найдена")

                channelPermissionRepository.save(
                    ChannelPermission(
                        ChannelPermission.ChannelPermissionId(
                            channelId = channelId,
                            roleId = adminRoleId
                        ),
                        canRead = true,
                        canWrite = true,
                        canManage = true
                    )
                )
            }

            logger.info("Channel $channelId is updated in server $serverId by user $userId")

            ResponseEntity.ok("Канал успешно обновлен")
        } catch (e: Exception) {
            logger.error("Error while updating channel", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении канала")
        }
    }

    @Transactional
    fun assignRolePermission(userId: UUID, serverId: UUID, channelId: UUID, roleId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Только владелец сервера может назначать права доступа к каналам")
            }

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Канал не найден")

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Канал не принадлежит этому серверу")
            } else if (!channel.isPrivate) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Права ролей можно назначать только для приватных каналов")
            }

            val role = serverRoleRepository.findById(roleId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль не найдена")

            if (role.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Роль не принадлежит этому серверу")
            }

            val existPermission =  channelPermissionRepository.findByChannelIdAndRoleId(channel.id!!, role.id!!)

            if (existPermission != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Права для этой роли уже назначены для этого канала")
            }

            channelPermissionRepository.save(
                ChannelPermission(
                    ChannelPermission.ChannelPermissionId(
                        channelId = channel.id,
                        roleId = role.id
                    ),
                    canRead = true,
                    canWrite = true,
                    canManage = true
                )
            )

            logger.info("Role $roleId is assigned to channel $channelId in server $serverId by user $userId")
            ResponseEntity.status(HttpStatus.CREATED).body("Права роли успешно назначены для канала")
        } catch (e: Exception) {
            logger.error("Error while assigning role permission", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при назначении прав роли")
        }

    }

    @Transactional
    fun getChannelPermissions(userId: UUID, serverId: UUID, channelId: UUID): ResponseEntity<List<RoleInfoDto>> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            serverMemberRepository.findByServerIdAndUserId(server.id!!, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
            }

            val permissions = channelPermissionRepository.findByChannelId(channel.id!!)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            val roles = serverRoleRepository.findAllRolesById(permissions.map { it.id.roleId } as List<UUID>)

            val rolesDto = roles.map { role ->
                val memberRoles = memberRoleRepository.findAllByRoleId(role.id!!)

                val membersWithUsers = memberRoles.mapNotNull { memberRole ->
                    val member = serverMemberRepository.findByServerIdAndUserId(
                        serverId,
                        memberRole.id.userId
                    )
                    val user = userRepository.findUserByUserId(memberRole.id.userId)

                    if (member != null) {
                        member to user
                    } else null
                }

                roleToRoleInfoDtoConverter.convert(role, membersWithUsers)
            }

            ResponseEntity.ok(rolesDto.toList())
        } catch (e: Exception) {
            logger.error("Error while getting channel permissions", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun removeRolePermission(userId: UUID, serverId: UUID, channelId: UUID, roleId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Только владелец сервера может изменять доступ к каналам")
            }

            val channel = channelRepository.findById(channelId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Канал не найден")

            if (channel.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Канал не принадлежит этому серверу")
            }

            val role = serverRoleRepository.findById(roleId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль не найдена")

            if (role.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Роль не принадлежит этому серверу")
            }

            val permission = channelPermissionRepository.findByChannelIdAndRoleId(channel.id!!, role.id!!)
                ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Права для этой роли не назначены для этого канала")

            channelPermissionRepository.delete(permission)

            logger.info("Role $roleId is removed from channel $channelId in server $serverId by user $userId")

            ResponseEntity.ok("Права роли успешно удалены для канала")
        } catch (e: Exception) {
            logger.error("Error while removing role permission", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении прав роли")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ChannelService::class.java)
    }
}