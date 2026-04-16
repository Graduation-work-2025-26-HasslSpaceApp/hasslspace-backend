package ru.hse.hasslspace.chatservice.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.hse.hasslspace.chatservice.client.rest.api.CentrifugoApi
import ru.hse.hasslspace.chatservice.dto.CentrifugoPublishRequest
import ru.hse.hasslspace.chatservice.model.Message
import java.util.UUID

@Service
class CentrifugoService(
    private val centrifugoApi: CentrifugoApi
) {
    @Value("\${feign.centrifugo-api.api_key}")
    private lateinit var apiKey: String
    @Value("\${feign.centrifugo-api.method}")
    private lateinit var method: String

    fun publish(chatId: UUID, message: Message) {
        val request = CentrifugoPublishRequest(
            method = method,
            params = CentrifugoPublishRequest.CentrifugoPublishParams(
                channel = CHANNEL_NAMESPACE+ chatId.toString(),
                data = message
            )
        )

        val response = centrifugoApi.publish(request, apiKey)

        if (response.statusCode.is2xxSuccessful) {
            logger.info("Published message to channel $chatId")
        } else {
            logger.error("Failed to publish message to channel $chatId: ${response.statusCode} - ${response.body}")
            throw RuntimeException("Centrifugo publish failed: ${response.statusCode}")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CentrifugoService::class.java)
        const val CHANNEL_NAMESPACE = "chat:"
    }
}
