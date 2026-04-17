package ru.hse.hasslspace.chatservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.chatservice.dto.ChatDto
import ru.hse.hasslspace.chatservice.dto.MessageDto
import ru.hse.hasslspace.chatservice.dto.converter.ChatToChatDtoConverter
import ru.hse.hasslspace.chatservice.model.Chat
import ru.hse.hasslspace.chatservice.model.PrivateChatMember
import ru.hse.hasslspace.chatservice.model.converter.MessageDtoToMessageConverter
import ru.hse.hasslspace.chatservice.repository.ChannelRepository
import ru.hse.hasslspace.chatservice.repository.ChatRepository
import ru.hse.hasslspace.chatservice.repository.MessageRepository
import ru.hse.hasslspace.chatservice.repository.PrivateChatMemberRepository
import ru.hse.hasslspace.chatservice.repository.ServerMemberRepository
import ru.hse.hasslspace.chatservice.repository.UserRepository
import java.util.*

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val privateChatMemberRepository: PrivateChatMemberRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val chatToChatDtoConverter: ChatToChatDtoConverter,
    private val channelRepository: ChannelRepository,
    private val serverMemberRepository: ServerMemberRepository,
    private val messageDtoToMessageConverter: MessageDtoToMessageConverter,
    private val centrifugoService: CentrifugoService,
) {

    @Transactional
    fun createPrivateChat(userId: UUID, targetUserId: UUID): ResponseEntity<String> {
        return try {
            val userChats = privateChatMemberRepository.findAllByUserId(userId)

            val existingChat = userChats.firstOrNull { chatMember ->
                val members = privateChatMemberRepository.findAllByChatId(chatMember.id.chatId)
                members.size == 2 &&
                        members.any { it.id.userId == targetUserId } &&
                        members.any { it.id.userId == userId }
            }

            if (existingChat != null) {
                return ResponseEntity.ok(existingChat.id.chatId.toString())
            }

            val chat = chatRepository.save(
                Chat(
                    type = Chat.ChatType.PRIVATE,
                    channelId = null
                )
            )

            privateChatMemberRepository.save(
                PrivateChatMember(
                    PrivateChatMember.PrivateChatMemberId(
                        chatId = chat.id!!, userId = userId
                    )
                )
            )

            privateChatMemberRepository.save(
                PrivateChatMember(
                    PrivateChatMember.PrivateChatMemberId(
                        chatId = chat.id, userId = targetUserId
                    )
                )
            )

            logger.info("Created private chat with id ${chat.id} between users $userId and $targetUserId")

            return ResponseEntity.status(HttpStatus.CREATED).body(chat.id.toString())
        } catch (e: Exception) {
            logger.error("Error while creating private chat", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при создании приватного чата")
        }
    }

    @Transactional
    fun getPrivateChat(userId: UUID, chatId: UUID): ResponseEntity<ChatDto> {
        return try {
            val chat = chatRepository.findById(chatId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

            if (chat.type != Chat.ChatType.PRIVATE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
            }

            val members = privateChatMemberRepository.findAllByChatId(chatId)
            if (members.none { it.id.userId == userId }) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
            }

            val targetUserId = members.first { it.id.userId != userId }.id.userId

            val users = userRepository.findAllUsersById(listOf(userId, targetUserId))

            val chatDto = chatToChatDtoConverter.convert(chat, users)

            logger.info("Retrieved private chat with id ${chat.id} for user $userId")

            ResponseEntity.ok(chatDto)
        } catch (e: Exception) {
            logger.error("Error while retrieving private chat", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @Transactional
    fun getPrivateChats(userId: UUID): ResponseEntity<List<ChatDto>> {
        return try {
            val chatMembers = privateChatMemberRepository.findAllByUserId(userId)

            val chats = chatRepository.findAllById(chatMembers.map { it.id.chatId })

            val chatDtos = chats.map { chat ->
                val members = privateChatMemberRepository.findAllByChatId(chat.id!!)

                val targetUserId = members.first { it.id.userId != userId }.id.userId

                val users = userRepository.findAllUsersById(listOf(userId, targetUserId))

                chatToChatDtoConverter.convert(chat, users)
            }

            logger.info("Retrieved private chats for user $userId")
            ResponseEntity.ok().body(chatDtos)
        } catch (e: Exception) {
            logger.error("Error while retrieving private chats for user $userId", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @Transactional
    fun sendMessage(userId: UUID, chatId: UUID, message: MessageDto): ResponseEntity<String> {
        return try {
            val chat = chatRepository.findById(chatId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Чат не найден")

            val hasAccess = when (chat.type) {
                Chat.ChatType.PRIVATE -> privateChatMemberRepository.existsByChatIdAndUserId(chatId, userId)
                else -> {
                    val serverId = channelRepository.findServerIdByChannelId(chat.channelId!!)
                        ?: return ResponseEntity.badRequest().body("Неверный канал")
                    serverMemberRepository.existsByServerIdAndUserId(serverId, userId)
                }
            }

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Пользователь не имеет доступа к чату")
            }

            val message = messageRepository.save(messageDtoToMessageConverter.convert(userId, chatId, message))

            centrifugoService.publish(chatId, message)

            logger.info("User $userId sent message to chat $chatId")

            ResponseEntity.ok("Сообщение отправлено")
        } catch (e: Exception) {
            logger.error("Error while sending message to chat", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при отправке сообщения")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ChatService::class.java)
    }
}