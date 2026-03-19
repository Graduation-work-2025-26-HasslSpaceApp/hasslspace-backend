package ru.hse.hasslspace.userservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.userservice.dto.ProfileDto
import ru.hse.hasslspace.userservice.dto.UpdateProfileDto
import ru.hse.hasslspace.userservice.dto.converter.UserToProfileDtoConverter
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.model.converter.UpdateProfileDtoToUserConverter
import ru.hse.hasslspace.userservice.repository.UserRepository

@Service
class ProfileService(
    private val userToProfileDtoConverter: UserToProfileDtoConverter,
    private val userRepository: UserRepository,
    private val updateProfileDtoToUserConverter: UpdateProfileDtoToUserConverter,

    ) {

    @Transactional
    fun getProfile(user: User): ProfileDto {
        return userToProfileDtoConverter.convert(user)
            .also {
                logger.info("Get profile for user ${user.id} completed")
            }

    }

    @Transactional
    fun updateProfile(user: User, updateProfileDto: UpdateProfileDto): ResponseEntity<String> {
        return try {
            userRepository.save(updateProfileDtoToUserConverter.convert(user, updateProfileDto))

            logger.info("Update profile for user ${user.id} completed")
            ResponseEntity.status(HttpStatus.OK).body("Профиль успешно обновлен")
        } catch (e: Exception) {
            logger.error("Error while updating profile ${user.id!!}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении профиля")
        }
    }

    @Transactional
    fun updateStatus(user: User, status: String): ResponseEntity<String> {
        return try {
            userRepository.save(user.apply {
                this.status = User.StatusType.valueOf(status.uppercase())
            })

            logger.info("Update status for user ${user.id}: $status")
            ResponseEntity.status(HttpStatus.OK).body("Статус успешно обновлен")
        } catch (e: Exception) {
            logger.error("Error while updating status ${user.id}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении статуса")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ProfileService::class.java)
    }
}