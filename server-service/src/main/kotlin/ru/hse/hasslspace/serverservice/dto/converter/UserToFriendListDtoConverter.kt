package ru.hse.hasslspace.serverservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.serverservice.dto.FriendsListDto
import ru.hse.hasslspace.serverservice.model.User

@Component
class UserToFriendListDtoConverter {

    fun convert(user: User, type: FriendsListDto.Type): FriendsListDto {
        return FriendsListDto(
            id = user.id!!,
            username = user.username,
            name = user.name,
            photoUrl = user.photoUrl,
            type = type
        )
    }
}