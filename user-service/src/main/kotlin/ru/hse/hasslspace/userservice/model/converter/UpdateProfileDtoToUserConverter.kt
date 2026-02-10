package ru.hse.hasslspace.userservice.model.converter

import org.springframework.stereotype.Component
import ru.hse.hasslspace.userservice.dto.UpdateProfileDto
import ru.hse.hasslspace.userservice.model.User

@Component
class UpdateProfileDtoToUserConverter {

    fun convert(user: User, updateProfileDto: UpdateProfileDto): User {
        return user.copy(
            username = user.username ?: user.username,
            name = updateProfileDto.name ?: user.name,
            photoUrl = updateProfileDto.photoUrl ?: user.photoUrl,
            userInfo = updateProfileDto.description ?: user.userInfo,
        )
    }
}