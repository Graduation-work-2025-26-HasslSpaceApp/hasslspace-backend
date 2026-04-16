package ru.hse.hasslspace.chatservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.hse.hasslspace.chatservice.service.JwtService
import java.util.UUID

@RestController
@RequestMapping(CHAT_SERVICE_BASE_PATH_URL)
class ConnectionController(
    private val jwtService: JwtService
) {

    @GetMapping(TOKEN_URL)
    fun getToken(
        @RequestHeader(USER_ID_HEADER) userId: UUID
    ): ResponseEntity<String> =
        jwtService.generateToken(userId)
}