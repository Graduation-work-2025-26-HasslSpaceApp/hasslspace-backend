package ru.hse.hasslspace.serverservice.service

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import ru.hse.hasslspace.serverservice.dto.CreateRoleRequest
import ru.hse.hasslspace.serverservice.dto.RoleInfoDto
import ru.hse.hasslspace.serverservice.dto.UpdateServerRoleDto
import ru.hse.hasslspace.serverservice.dto.converter.RoleToRoleInfoDtoConverter
import ru.hse.hasslspace.serverservice.model.MemberRole
import ru.hse.hasslspace.serverservice.model.ServerRole
import ru.hse.hasslspace.serverservice.model.converter.UpdateServerRoleDtoToServerRoleConverter
import ru.hse.hasslspace.serverservice.repository.MemberRoleRepository
import ru.hse.hasslspace.serverservice.repository.ServerMemberRepository
import ru.hse.hasslspace.serverservice.repository.ServerRepository
import ru.hse.hasslspace.serverservice.repository.ServerRoleRepository
import ru.hse.hasslspace.serverservice.repository.UserRepository
import java.util.*

@Service
class ServerRoleService(
    private val serverRepository: ServerRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val serverRoleRepository: ServerRoleRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val userRepository: UserRepository,
    private val roleToRoleInfoDtoConverter: RoleToRoleInfoDtoConverter,
    private val updateServerRoleDtoToServerRoleConverter: UpdateServerRoleDtoToServerRoleConverter
) {

    @Transactional
    fun createRole(userId: UUID, serverId: UUID, request: CreateRoleRequest): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            // TODO: исправть, пока только владелец может создавать роли
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может создавать роли")
            }

            // TODO: разобраться с позициями ролей
            serverRoleRepository.save(
                ServerRole(
                    serverId = serverId,
                    name = request.name,
                    color = request.color,
                    position = request.position,
                    isDefault = false
                )
            )

            logger.info("Role '${request.name}' created for server $serverId by user $userId")

            ResponseEntity.status(HttpStatus.CREATED).body("Роль успешно создана")
        } catch (e: Exception) {
            logger.error("Error while creating role", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при создании роли")
        }
    }

    @Transactional
    fun getRoles(userId: UUID, serverId: UUID): ResponseEntity<List<RoleInfoDto>> {
        return try {
            serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val roles = serverRoleRepository.findAllByServerId(serverId)

            val roleDtos = roles.map { role ->
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

            ResponseEntity.ok(roleDtos.toList())
        } catch (e: Exception) {
            logger.error("Error while getting roles for server $serverId", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun deleteRole(userId: UUID, serverId: UUID, roleId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            // TODO: исправить, пока только владелец может удалять роли
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может удалять роли")
            }

            val role = serverRoleRepository.findById(roleId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль не найдена")

            if (role.isDefault) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нельзя удалить стандартную роль")
            }

            memberRoleRepository.deleteByRoleId(roleId)

            serverRoleRepository.deleteById(roleId)

            logger.info("Role $roleId deleted from server $serverId by user $userId")

            ResponseEntity.ok("Роль успешно удалена")
        } catch (e: Exception) {
            logger.error("Error while deleting role", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении роли")
        }
    }

    @Transactional
    fun assignRole(userId: UUID, serverId: UUID, targetUserId: UUID, roleId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            //TODO: исправить, пока только владелец может выдавать роли
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может выдавать роли")
            }

            serverMemberRepository.findByServerIdAndUserId(serverId, targetUserId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Участник не найден на сервере")

            val role = serverRoleRepository.findById(roleId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль не найдена")

            if (role.serverId != serverId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Роль не принадлежит этому серверу")
            }

            val existingMemberRole = memberRoleRepository.findByServerIdAndUserIdAndRoleId(
                serverId, targetUserId, roleId
            )
            if (existingMemberRole != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("У участника уже есть эта роль")
            }

            memberRoleRepository.save(
                MemberRole(
                    id = MemberRole.MemberRoleId(
                        serverId = serverId,
                        userId = targetUserId,
                        roleId = roleId
                    )
                )
            )

            logger.info("Role $roleId assigned to user $targetUserId on server $serverId by $userId")

            ResponseEntity.ok("Роль успешно выдана")
        } catch (e: Exception) {
            logger.error("Error while assigning role", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при выдаче роли")
        }
    }

    @Transactional
    fun removeRole(userId: UUID, serverId: UUID, targetUserId: UUID, roleId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            // TODO: исправить, пока только владелец может забирать роли
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может забирать роли")
            }

            val memberRole = memberRoleRepository.findByServerIdAndUserIdAndRoleId(
                serverId, targetUserId, roleId
            ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("У участника нет такой роли")

            val role = serverRoleRepository.findById(roleId).orElse(null)
            if (role?.isDefault == true) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нельзя забрать стандартную роль")
            }


            memberRoleRepository.delete(memberRole)

            logger.info("Role $roleId removed from user $targetUserId on server $serverId by $userId")

            ResponseEntity.ok("Роль успешно забрана")
        } catch (e: Exception) {
            logger.error("Error while removing role", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при забирании роли")
        }
    }

    @Transactional
    fun updateRole(userId: UUID, serverId: UUID, roleId: UUID, request: UpdateServerRoleDto): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            // TODO: исправить, пока только владелец может изменять роли
            if (server.ownerId != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только владелец сервера может забирать роли")
            }

            val role = serverRoleRepository.findById(roleId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Роль не найдена")

            serverRoleRepository.save(updateServerRoleDtoToServerRoleConverter.convert(role, request))

            logger.info("Updating role $roleId on server $serverId by user $userId")

            ResponseEntity.ok("Роль успешно обновлена")
        } catch (e: Exception) {
            logger.error("Error while updating role", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении роли")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ServerRoleService::class.java)
    }
}