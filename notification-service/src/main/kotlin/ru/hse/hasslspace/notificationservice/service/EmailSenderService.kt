package ru.hse.hasslspace.notificationservice.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import ru.hse.hasslspace.notificationservice.dto.EmailRequest
import java.util.*

@Service
class EmailSenderService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    @Value("\${spring.mail.username}")
    private val from: String
) {

    fun sendEmail(request: EmailRequest): ResponseEntity<String> =
        try {
            val context = Context(Locale("ru"))
            context.setVariable("code", request.text)

            val htmlContent = templateEngine.process("email-confirmation", context)

            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

            helper.setFrom(from)
            helper.setTo(request.targetEmail)
            helper.setSubject(request.subject)
            helper.setText(htmlContent, true)

            mailSender.send(mimeMessage)

            logger.info("HTML email sent to ${request.targetEmail}")
            ResponseEntity.ok("Email sent successfully")
        } catch (e: Exception) {
            logger.error("Failed to send email: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send email: ${e.message}")
        }

    companion object {
        private val logger = LoggerFactory.getLogger(EmailSenderService::class.java)
    }
}