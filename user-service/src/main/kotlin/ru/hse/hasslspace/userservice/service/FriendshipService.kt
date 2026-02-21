package ru.hse.hasslspace.userservice.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hse.hasslspace.userservice.dto.FriendsListDto
import ru.hse.hasslspace.userservice.dto.UserProfileDto
import ru.hse.hasslspace.userservice.dto.converter.UserToFriendListDroConverter
import ru.hse.hasslspace.userservice.dto.converter.UserToUserProfileDtoConverter
import ru.hse.hasslspace.userservice.model.Friendship
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.repository.FriendshipRepository
import ru.hse.hasslspace.userservice.repository.UserRepository
import java.time.LocalDateTime
import java.util.*

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository,
    private val userToUserProfileDtoConverter: UserToUserProfileDtoConverter,
    private val userToFriendListDroConverter: UserToFriendListDroConverter,
) {

    @Transactional
    fun getUserProfile(user: User, userId: UUID): UserProfileDto {
        val profileUser = userRepository.findUserByUserId(userId)

        val statusType = friendshipRepository.findFriendshipBetweenUsers(user.id!!, userId)?.let { friendship ->
            when {
                (friendship.requesterId == user.id || friendship.addresseeId == user.id) && friendship.status == Friendship.FriendshipStatus.ACCEPTED
                    -> UserProfileDto.StatusType.FRIEND

                friendship.requesterId == user.id && friendship.status == Friendship.FriendshipStatus.PENDING
                    -> UserProfileDto.StatusType.OUTGOING_REQUEST

                friendship.addresseeId == user.id && friendship.status == Friendship.FriendshipStatus.PENDING
                    -> UserProfileDto.StatusType.INCOMING_REQUEST

                (friendship.requesterId == user.id || friendship.addresseeId == user.id) && friendship.status == Friendship.FriendshipStatus.BLOCKED
                    -> UserProfileDto.StatusType.BLOCKED

                else -> UserProfileDto.StatusType.NONE
            }
        } ?: UserProfileDto.StatusType.NONE

        logger.info("Get profile for user ${user.id} and profile user $userId completed")
        return userToUserProfileDtoConverter.convert(profileUser, statusType)
    }

    @Transactional
    fun getFriendsList(user: User): List<FriendsListDto> {
        val friendships = friendshipRepository.findFriendshipsByUserId(user.id!!)

        return friendships.map { friendship ->
            val friendId = if (friendship.requesterId == user.id) {
                friendship.addresseeId
            } else {
                friendship.requesterId
            }

            val friend = userRepository.findUserByUserId(friendId)

            val type = when {
                (friendship.requesterId == user.id || friendship.addresseeId == user.id) && friendship.status == Friendship.FriendshipStatus.ACCEPTED
                    -> FriendsListDto.Type.FRIEND

                friendship.requesterId == user.id && friendship.status == Friendship.FriendshipStatus.PENDING
                    -> FriendsListDto.Type.OUTGOING_REQUEST

                friendship.addresseeId == user.id && friendship.status == Friendship.FriendshipStatus.PENDING
                    -> FriendsListDto.Type.INCOMING_REQUEST

                else -> throw IllegalStateException("Unexpected friendship status")
            }

            userToFriendListDroConverter.convert(friend, type)
        }
    }

    @Transactional
    fun sendFriendRequest(user: User, username: String): ResponseEntity<String> {
        return try {
            val targetUser = userRepository.findUserByUsername(username).also { user ->
                if (user == null) {
                    logger.error("User with username $username not found")
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с таким username не найден")
                }
            }

            friendshipRepository.save(
                Friendship(
                    requesterId = user.id!!,
                    addresseeId = targetUser!!.id!!,
                    createdAt = LocalDateTime.now()
                )
            )

            logger.info("Friend request from ${user.id} to $username successfully sent")
            ResponseEntity.status(HttpStatus.OK).body("Запрос успешно отправлен")
        } catch (e: Exception) {
            logger.error("Error while send friend request from ${user.id} to $username")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка отправки запроса дружбы")
        }
    }

    @Transactional
    fun updateFriendResponse(user: User, friendshipId: UUID, status: String): ResponseEntity<String> {
        return try {
            val friendship = friendshipRepository.findFriendshipById(friendshipId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Запрос дружбы не найден")

            if (friendship.addresseeId != user.id) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь не является получателем запроса дружбы")
            }

            friendshipRepository.save(
                friendship.apply { this.status = Friendship.FriendshipStatus.valueOf(status) }
            )

            logger.info("Friend response for user ${user.id} and friendship $friendshipId successfully updated to $status")
            ResponseEntity.status(HttpStatus.OK).body("Ответ на запрос дружбы успешно обновлен")
        } catch (e: Exception) {
            logger.error("Error while updating friend response for user ${user.id} and friendship $friendshipId")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обновлении ответа на запрос дружбы")
        }
    }

    @Transactional
    fun blockUser(user: User, userId: UUID): ResponseEntity<String> {
        return try {
            val targetUser = userRepository.findUserByUserId(userId)

            friendshipRepository.save(
                Friendship(
                    requesterId = user.id!!,
                    addresseeId = targetUser.id!!,
                    status = Friendship.FriendshipStatus.BLOCKED,
                    createdAt = LocalDateTime.now()
                )
            )

            logger.info("User $userId successfully blocked by ${user.id}")
            ResponseEntity.status(HttpStatus.OK).body("Запрос успешно отправлен")
        } catch (e: Exception) {
            logger.error("Error while block user from ${user.id} to $userId")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка отправки запроса дружбы")
        }
    }

    @Transactional
    fun deleteFriendship(user: User, userId: UUID): ResponseEntity<String> {
        return try {
            val friendship = friendshipRepository.findFriendshipBetweenUsers(user.id!!, userId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не является вашим другом")

            friendshipRepository.delete(friendship)

            logger.info("Deleting friendship between ${user.id} and $userId")
            ResponseEntity.status(HttpStatus.OK).body("Пользователь успешно удален из друзей")
        } catch (e: Exception) {
            logger.error("Error while deleting friendship between ${user.id!!} and $userId")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении пользователя из друзей")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FriendshipService::class.java)
    }
}