package ru.hse.hasslspace.userservice.dto.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.userservice.dto.ProfileDto
import ru.hse.hasslspace.userservice.model.User
import java.util.UUID

@Component
class UserToProfileDtoConverter {

    fun convert(user: User): ProfileDto {
        return ProfileDto(
            id = user.id!!,
            username = user.username,
            name = user.name,
            email = user.email,
            photoUrl = user.photoUrl,
            description = user.userInfo
        )
    }
}
