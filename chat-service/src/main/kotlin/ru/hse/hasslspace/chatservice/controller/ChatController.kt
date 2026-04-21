package ru.hse.hasslspace.chatservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.hse.hasslspace.chatservice.dto.MessageDto
import ru.hse.hasslspace.chatservice.model.Message
import ru.hse.hasslspace.chatservice.service.ChatService
import java.time.LocalDateTime
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

    @GetMapping(CHARS_MESSAGE_HISTORY_URL)
    fun getMessageHistory(
        @RequestHeader(USER_ID_HEADER) userId: UUID,
        @RequestParam chatId: UUID,
        @RequestParam(required = false) fromMessageId: UUID? = null,
        @RequestParam(required = false) fromDate: LocalDateTime? = null,
        @RequestParam(required = false) toDate: LocalDateTime? = null,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<List<Message>> {
        return chatService.getMessageHistory(
            userId = userId,
            chatId = chatId,
            fromMessageId = fromMessageId,
            fromDate = fromDate,
            toDate = toDate,
            limit = limit.coerceIn(1, 200)
        )
    }

}
