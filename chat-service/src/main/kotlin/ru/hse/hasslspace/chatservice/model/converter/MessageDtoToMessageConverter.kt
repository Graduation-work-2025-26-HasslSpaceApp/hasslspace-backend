package ru.hse.hasslspace.chatservice.model.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.chatservice.dto.MessageDto
import ru.hse.hasslspace.chatservice.model.Message
import java.util.*

@Component
class MessageDtoToMessageConverter {

    fun convert(userId: UUID, chatId: UUID, dto: MessageDto): Message {
        return Message(
            chatId = chatId,
            userId = userId,
            content = dto.content,
            fileUrl = dto.fileUrl,
            createdAt = dto.createdAt,
            editedAt = dto.editedAt,
        )
    }
}