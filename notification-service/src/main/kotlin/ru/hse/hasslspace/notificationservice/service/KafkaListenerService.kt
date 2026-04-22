package ru.hse.hasslspace.notificationservice.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.notificationservice.dto.EmailRequest

@Service
class KafkaListenerService(
    private val emailSenderService: EmailSenderService
) {

    @Transactional
    @KafkaListener(
        topics = ["\${spring.kafka.topic.verify-user-email}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        properties = [
            "spring.json.value.default.type=ru.hse.hasslspace.notificationservice.dto.EmailRequest",
            "spring.json.use.type.headers=false"
        ]
    )
    fun sendMail(request: EmailRequest) {
        logger.info("Received email request from Kafka topic for user: ${request.targetEmail}")
        emailSenderService.sendEmail(request)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaListenerService::class.java)
    }
}
