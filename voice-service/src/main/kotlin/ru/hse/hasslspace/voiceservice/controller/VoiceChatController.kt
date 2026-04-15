package ru.hse.hasslspace.voiceservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.voiceservice.dto.TokenRequest
import ru.hse.hasslspace.voiceservice.service.VoiceChatService
import java.util.*

@RestController
@RequestMapping(VOICE_SERVICE_BASE_PATH_URL)
class VoiceChatController(private val voiceChatService: VoiceChatService) {

    @PostMapping(GET_TOKEN_URL)
    fun getToken(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestBody request: TokenRequest
    ): ResponseEntity<String> =
        voiceChatService.generateToken(userId, request)
}
