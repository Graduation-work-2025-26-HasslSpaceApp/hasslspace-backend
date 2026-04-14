package ru.hse.hasslspace.chatservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.chatservice.dto.ChatDto
import ru.hse.hasslspace.chatservice.dto.ChatDto.ChatMemberDto
import ru.hse.hasslspace.chatservice.model.Chat
import ru.hse.hasslspace.chatservice.model.User

@Component
class ChatToChatDtoConverter {

    fun convert(chat: Chat, users: List<User>): ChatDto {
        return ChatDto(
            id = chat.id!!,
            chatMembers = users.map { user ->
                ChatMemberDto(
                    id = user.id!!,
                    name = user.name,
                    username = user.username,
                    status = user.status.toString(),
                    photoUrl = user.photoUrl
                )
            }
        )
    }
}
