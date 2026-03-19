package ru.hse.hasslspace.userservice.service.notifier

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.hasslspace.userservice.dto.EmailRequest


@Service
interface NotifierService {

    fun send(request: EmailRequest): ResponseEntity<String>
}