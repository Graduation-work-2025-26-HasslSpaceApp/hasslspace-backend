package ru.hse.hasslspace.chatservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.chatservice.dto.MessageDto
import ru.hse.hasslspace.chatservice.service.ChatService
import java.util.*

@RestController
@RequestMapping(CHAT_SERVICE_BASE_PATH_URL)
class ChatController(
    private val chatService: ChatService
) {
    @PostMapping(CHATS_URL)
    fun createPrivateChat(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam targetUserId: UUID
    ): ResponseEntity<String> {
        return chatService.createPrivateChat(userId, targetUserId)
    }

    @GetMapping(CHATS_URL)
    fun getPrivateChats(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam(required = false) chatId: UUID?
    ): ResponseEntity<*> {
        return if (chatId != null) {
            chatService.getPrivateChat(userId, chatId)
        } else {
            chatService.getPrivateChats(userId)
        }
    }

    @PostMapping(CHATS_MESSAGE_URL)
    fun sendMessage(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam chatId: UUID,
        @RequestBody message: MessageDto
    ): ResponseEntity<String> {
        return chatService.sendMessage(userId, chatId, message)
    }

}
