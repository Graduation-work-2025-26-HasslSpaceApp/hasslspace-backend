package ru.hse.hasslspace.voiceservice.service

import io.livekit.server.*
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.hasslspace.voiceservice.config.LiveKitConfig
import ru.hse.hasslspace.voiceservice.dto.TokenRequest
import java.util.*

@Service
class VoiceChatService(
    private val liveKitConfig: LiveKitConfig
) {

    fun generateToken(userId: UUID, request: TokenRequest): ResponseEntity<String> {
        return try {
            //TODO: на основе типа комнаты сделать проверку прав доступа потом (а-ля проверять, что участник сервера или приватного чата)

            val token = AccessToken(liveKitConfig.apiKey, liveKitConfig.apiSecret)
            token.identity = userId.toString()
            token.name = request.name

            token.addGrants(
                RoomJoin(true),
                RoomName(request.roomName),
                CanPublish(true),
                CanSubscribe(true)
            )

            logger.info("Generated token for user ${request.name} (ID: $userId) in room ${request.roomName}")

            ResponseEntity.ok(token.toJwt())
        } catch (e: Exception) {
            logger.error("Error generating token for user ${request.name} (ID: $userId) in room ${request.roomName}", e)
            ResponseEntity.status(500).body("Error generating token: ${e.message}")
        }
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(VoiceChatService::class.java)
    }
}