package ru.hse.hasslspace.serverservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.serverservice.dto.ServerInviteDto
import ru.hse.hasslspace.serverservice.dto.converter.ServerInviteToServerInviteDtoConverter
import ru.hse.hasslspace.serverservice.model.ServerInvite
import ru.hse.hasslspace.serverservice.repository.ServerInviteRepository
import ru.hse.hasslspace.serverservice.repository.ServerMemberRepository
import ru.hse.hasslspace.serverservice.repository.ServerRepository
import java.time.LocalDateTime
import java.util.*

@Service
class ServerInviteService(
    private val serverInviteRepository: ServerInviteRepository,
    private val serverRepository: ServerRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val serverInviteToServerInviteDtoConverter: ServerInviteToServerInviteDtoConverter
) {

    @Transactional
    fun createInvite(userId: UUID, serverId: UUID): ResponseEntity<ServerInviteDto> {
        return try {
            serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            val inviteCode = generateInviteCode()

            val savedInvite = serverInviteRepository.save(
                ServerInvite(
                    code = inviteCode,
                    serverId = serverId,
                    creatorId = userId,
                    expiresAt = LocalDateTime.now().plusDays(7)
                )
            )

            logger.info("Invite created for server $serverId by user $userId, code: $inviteCode")

            ResponseEntity.status(HttpStatus.CREATED).body(
                serverInviteToServerInviteDtoConverter.convert(
                    serverInvite = savedInvite,
                    serverName = server.name,
                    creatorName = serverMemberRepository.findByServerIdAndUserId(serverId, userId)!!.name,
                )
            )
        } catch (e: Exception) {
            logger.error("Error while creating invite", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    fun getActiveInvites(userId: UUID, serverId: UUID): ResponseEntity<List<ServerInviteDto>> {
        return try {
            serverMemberRepository.findByServerIdAndUserId(serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val server = serverRepository.findServerById(serverId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            val activeInvites = serverInviteRepository.findAllActiveByServerId(serverId)

            ResponseEntity.ok(activeInvites.map { invite ->
                serverInviteToServerInviteDtoConverter.convert(
                    serverInvite = invite,
                    serverName = server.name,
                    creatorName = serverMemberRepository.findByServerIdAndUserId(serverId, invite.creatorId)?.name
                )
            })
        } catch (e: Exception) {
            logger.error("Error while getting active invites", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @Transactional
    fun deleteInvite(userId: UUID, inviteCode: String): ResponseEntity<String> {
        return try {
            val invite = serverInviteRepository.findByCode(inviteCode)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            serverMemberRepository.findByServerIdAndUserId(invite.serverId, userId)
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            serverInviteRepository.delete(invite)

            logger.info("Invite with code $inviteCode deleted by user $userId")

            ResponseEntity.status(HttpStatus.OK).body("Приглашение с кодом $inviteCode успешно удалено")
        } catch (e: Exception) {
            logger.error("Error while deleting invite", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    private fun generateInviteCode(): String {
        return UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
    }


    companion object {
        private val logger = LoggerFactory.getLogger(ServerInviteService::class.java)
    }
}