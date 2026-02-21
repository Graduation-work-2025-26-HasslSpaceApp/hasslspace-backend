package ru.hse.hasslspace.userservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.userservice.dto.FriendsListDto
import ru.hse.hasslspace.userservice.model.User

@Component
class UserToFriendListDroConverter {

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