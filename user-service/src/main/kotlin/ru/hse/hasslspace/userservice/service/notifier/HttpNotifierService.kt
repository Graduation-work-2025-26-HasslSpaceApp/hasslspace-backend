package ru.hse.hasslspace.userservice.service.notifier

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.hasslspace.userservice.client.rest.api.NotificationServiceApi
import ru.hse.hasslspace.userservice.dto.EmailRequest


@Service
@ConditionalOnProperty(
    name = ["notification-service.transport-type"],
    havingValue = "HTTP",
    matchIfMissing = true
)
class HttpNotifierService(
    private val notificationServiceApi: NotificationServiceApi
) : NotifierService {

    override fun send(request: EmailRequest): ResponseEntity<String> {
        return try {
            logger.info("Sending email request to Notification Service by HTTP")
            notificationServiceApi.sendEmail(request)
        } catch (e: Exception) {
            logger.error("Failed to send email request to Notification Service by HTTP", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email request")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HttpNotifierService::class.java)
    }
}
