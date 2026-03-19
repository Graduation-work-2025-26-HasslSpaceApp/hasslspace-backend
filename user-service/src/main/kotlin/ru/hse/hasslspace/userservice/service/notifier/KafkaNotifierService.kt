package ru.hse.hasslspace.userservice.service.notifier

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.hasslspace.userservice.dto.EmailRequest


@Service
@ConditionalOnProperty(
    name = ["notification-service.transport-type"],
    havingValue = "KAFKA",
    matchIfMissing = false
)
class KafkaNotifierService(
//    private val kafkaTemplate: KafkaTemplate<String, Any>,
//    private val notificationServiceApi: NotificationServiceApi,
//    @Value("\${spring.kafka.topic.verify-user-email}")
//    private val topic: String
) : NotifierService {

    override fun send(request: EmailRequest): ResponseEntity<String> {
//        return try {
//            logger.info("Sending email request to Kafka topic: $topic")
//            kafkaTemplate.send(topic, request.targetEmail ,request)
//            ResponseEntity.status(HttpStatus.OK).body("Email request sent to Kafka topic")
//        } catch (e: Exception) {
//            logger.error("Failed to send email request to Kafka topic: $topic", e)
//            fallbackToHttp(request)
//        }
        return ResponseEntity.ok("OK")

    }

    fun fallbackToHttp(request: EmailRequest): ResponseEntity<String> {
//        return try {
//            logger.info("Fallback to HTTP notifier service")
//            return notificationServiceApi.sendEmail(request)
//        } catch (e: Exception) {
//            logger.error("Failed to send email request to Notification Service by HTTP", e)
//            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email request")
//        }
        return ResponseEntity.ok("OK")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaNotifierService::class.java)
    }
}