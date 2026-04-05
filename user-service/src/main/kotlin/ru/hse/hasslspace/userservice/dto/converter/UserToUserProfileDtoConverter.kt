package ru.hse.hasslspace.userservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.userservice.dto.UserProfileDto
import ru.hse.hasslspace.userservice.dto.UserProfileDto.StatusType
import ru.hse.hasslspace.userservice.model.User

@Component
class UserToUserProfileDtoConverter {

    fun convert(user: User, statusType: StatusType): UserProfileDto {
        return UserProfileDto(
            id = user.id!!,
            username = user.username,
            name = user.name,
            photoUrl = user.photoUrl,
            description = user.userInfo,
            friendStatus = statusType,
            status = user.status.toString()
        )
    }
}
