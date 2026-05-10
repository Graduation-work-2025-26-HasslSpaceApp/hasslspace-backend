package ru.hse.hasslspace.serverservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.serverservice.dto.FriendsListDto
import ru.hse.hasslspace.serverservice.dto.ServerMemberDto
import ru.hse.hasslspace.serverservice.dto.converter.ServerToServerInfoExpandedDtoConverter
import ru.hse.hasslspace.serverservice.dto.converter.UserToFriendListDtoConverter
import ru.hse.hasslspace.serverservice.model.Friendship
import ru.hse.hasslspace.serverservice.model.MemberRole
import ru.hse.hasslspace.serverservice.model.ServerMember
import ru.hse.hasslspace.serverservice.model.ServerMember.ServerMemberId
import ru.hse.hasslspace.serverservice.repository.*
import java.time.LocalDateTime
import java.util.*

@Service
class ServerMemberService(
    private val serverInviteRepository: ServerInviteRepository,
    private val serverRepository: ServerRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val serverRoleRepository: ServerRoleRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
    private val serverToServerInfoExpandedDtoConverter: ServerToServerInfoExpandedDtoConverter,
    private val userToFriendListDtoConverter: UserToFriendListDtoConverter
) {

    @Transactional
    fun joinServerByInvite(userId: UUID, code: String): ResponseEntity<String> {
        return try {
            val invite = serverInviteRepository.findById(code)
                .orElse(null) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Приглашение не найдено")

            if (invite.expiresAt != null && invite.expiresAt.isBefore(LocalDateTime.now())) {
                serverInviteRepository.delete(invite)
                return ResponseEntity.status(HttpStatus.GONE).body("Срок действия приглашения истек")
            }

            if (serverMemberRepository.findByServerIdAndUserId(invite.serverId, userId) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Вы уже участник этого сервера")
            }

            serverMemberRepository.save(
                ServerMember(
                    ServerMemberId(
                        serverId = invite.serverId,
                        userId = userId,
                    ),
                    joinedAt = LocalDateTime.now(),
                    name = userRepository.findUserByUserId(userId).name
                )
            )

            serverRoleRepository.findDefaultRoleByServerId(invite.serverId).also { role ->
                memberRoleRepository.save(
                    MemberRole(
                        MemberRole.MemberRoleId(
                            serverId = invite.serverId,
                            userId = userId,
                            roleId = role!!.id!!
                        )
                    )
                )
            }

            logger.info("User $userId joined server ${invite.serverId} using invite $code")

            ResponseEntity.ok("Вы успешно присоединились к серверу")
        } catch (e: Exception) {
            logger.error("Error while joining server by invite", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при присоединении к серверу")
        }
    }

    @Transactional
    fun leaveServer(userId: UUID, serverId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            if (server.ownerId == userId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Владелец не может покинуть сервер. Передайте права владельца или удалите сервер.")
            }

            val member = serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Вы не участник этого сервера")

            memberRoleRepository.deleteByServerIdAndUserId(serverId, userId)

            serverMemberRepository.delete(member)

            logger.info("User $userId left server $serverId")

            ResponseEntity.ok("Вы успешно покинули сервер")
        } catch (e: Exception) {
            logger.error("Error while leaving server", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при выходе из сервера")
        }
    }

    @Transactional
    fun getServerMembers(
        currentUserId: UUID,
        serverId: UUID
    ): ResponseEntity<List<ServerMemberDto>> {
        return try {
            serverMemberRepository.findByServerIdAndUserId(serverId, currentUserId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()


            val memberDtos = serverMemberRepository.findAllByServerId(serverId).map { member ->
                val roleIds = memberRoleRepository.findRoleIdsByServerIdAndUserId(serverId, member.id.userId)
                val roles = serverRoleRepository.findAllRolesById(roleIds.toList())
                val user = userRepository.findUserByUserId(member.id.userId)

                serverToServerInfoExpandedDtoConverter.toMemberDto(
                    member = member,
                    user = user,
                    roles = roles
                )
            }

            ResponseEntity.ok(memberDtos)
        } catch (e: Exception) {
            logger.error("Error while getting server members", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun kickMember(
        currentUserId: UUID,
        serverId: UUID,
        targetUserId: UUID
    ): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            if (server.ownerId != currentUserId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Только владелец сервера может выгонять участников")
            }

            if (server.ownerId == targetUserId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Нельзя выгнать владельца сервера")
            }

            val member = serverMemberRepository.findByServerIdAndUserId(serverId, targetUserId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Участник не найден")

            memberRoleRepository.deleteByServerIdAndUserId(serverId, targetUserId)

            serverMemberRepository.delete(member)

            logger.info("User $targetUserId was kicked from server $serverId by $currentUserId")

            ResponseEntity.ok("Участник успешно удален с сервера")
        } catch (e: Exception) {
            logger.error("Error while kicking member", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при удалении участника")
        }
    }

    @Transactional
    fun getFriendsNotInServer(userId: UUID, serverId: UUID): ResponseEntity<List<FriendsListDto>> {
        return try {
            serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val friendships = friendshipRepository.findFriendshipsByUserId(userId)

            val memberIds = serverMemberRepository.findAllByServerId(serverId)
                .map { it.id.userId }
                .toSet()

            val friendsNotInServer = friendships
                .filter { it.status == Friendship.FriendshipStatus.ACCEPTED }
                .map { friendship ->
                    if (friendship.requesterId == userId) friendship.addresseeId else friendship.requesterId
                }
                .filter { friendId -> !memberIds.contains(friendId) }
                .map { friendId -> userRepository.findUserByUserId(friendId) }

            val friendsDto = friendsNotInServer.map { user ->
                userToFriendListDtoConverter.convert(user, FriendsListDto.Type.FRIEND)
            }

            logger.info("User $userId requested friends not in server $serverId. Found ${friendsDto.size} friends.")

            ResponseEntity.ok(friendsDto)
        } catch (e: Exception) {
            logger.error("Error while getting friends not in server. User: $userId, Server: $serverId", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun changeServerOwner(currentUserId: UUID, serverId: UUID, newOwnerId: UUID): ResponseEntity<String> {
        return try {
            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сервер не найден")

            if (server.ownerId != currentUserId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Только владелец сервера может передавать права собственности")
            }

            serverMemberRepository.findByServerIdAndUserId(serverId, newOwnerId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Новый владелец должен быть участником сервера")

            val adminRoleId = serverRoleRepository.findAdminRoleIdByServerId(serverId)
            val defaultRoleId = serverRoleRepository.findDefaultRoleByServerId(serverId)!!.id!!

            serverRepository.save(server.also { it.ownerId = newOwnerId })

            memberRoleRepository.delete(
                MemberRole(
                    MemberRole.MemberRoleId(
                        serverId = serverId,
                        userId = currentUserId,
                        roleId = adminRoleId
                    )
                )
            )

            memberRoleRepository.save(
                MemberRole(
                    MemberRole.MemberRoleId(
                        serverId = serverId,
                        userId = newOwnerId,
                        roleId = adminRoleId
                    )
                )
            )

            val existingDefaultRole = memberRoleRepository.findById(
                MemberRole.MemberRoleId(
                    serverId = serverId,
                    userId = currentUserId,
                    roleId = defaultRoleId
                )
            )

            if (!existingDefaultRole.isPresent) {
                memberRoleRepository.save(
                    MemberRole(
                        MemberRole.MemberRoleId(
                            serverId = serverId,
                            userId = currentUserId,
                            roleId = defaultRoleId
                        )
                    )
                )
            }

            logger.info("User $currentUserId transferred ownership of server $serverId to $newOwnerId")

            ResponseEntity.ok("Права собственности успешно переданы")
        } catch (e: Exception) {
            logger.error("Error while changing server owner", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при передаче прав собственности")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ServerMemberService::class.java)
    }
}