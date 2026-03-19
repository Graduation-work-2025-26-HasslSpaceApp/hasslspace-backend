package ru.hse.hasslspace.notificationservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.notificationservice.dto.EmailRequest
import ru.hse.hasslspace.notificationservice.service.EmailSenderService

@RestController
@RequestMapping(MAIL_SENDER_BASE_PATH_URL)
class MailController(
    private val emailSenderService: EmailSenderService
) {

    @PostMapping(SEND_MAIL_URL)
    fun sendMail(
        @RequestBody request: EmailRequest
    ): ResponseEntity<String> =
        emailSenderService.sendEmail(request)
}
