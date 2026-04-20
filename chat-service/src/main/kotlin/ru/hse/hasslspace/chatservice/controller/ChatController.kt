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
    fun createChat(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam targetUserId: UUID? = null,
        @RequestParam channelId: UUID? = null
    ): ResponseEntity<String> {
        return when {
            targetUserId != null && channelId != null -> {
                ResponseEntity.badRequest().body("Нельзя указывать одновременно targetUserId и channelId")
            }
            targetUserId != null -> {
                chatService.createPrivateChat(userId, targetUserId)
            }
            channelId != null -> {
                chatService.createChannelChat(userId, channelId)
            }
            else -> {
                ResponseEntity.badRequest().body("Необходимо указать targetUserId или channelId")
            }
        }
    }

    @GetMapping(CHATS_URL)
    fun getChats(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam chatId: UUID? = null,
        @RequestParam channelId: UUID? = null
    ): ResponseEntity<Any> {
        return when {
            chatId != null && channelId != null -> {
                ResponseEntity.badRequest().body("Нельзя указывать одновременно chatId и channelId")
            }

            chatId != null -> {
                chatService.getPrivateChat(userId, chatId)
            }

            channelId != null -> {
                chatService.getChannelChat(userId, channelId)
            }

            else -> {
                chatService.getPrivateChats(userId)
            }
        } as ResponseEntity<Any>
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
