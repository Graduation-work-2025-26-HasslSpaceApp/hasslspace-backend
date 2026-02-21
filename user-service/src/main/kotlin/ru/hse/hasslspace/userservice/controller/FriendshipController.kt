package ru.hse.hasslspace.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.hse.hasslspace.userservice.dto.FriendsListDto
import ru.hse.hasslspace.userservice.dto.UserProfileDto
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.service.FriendshipService
import java.util.UUID

@RestController
@RequestMapping(USER_SERVICE_BASE_PATH_URL)
class FriendshipController(private val friendshipService: FriendshipService) {

    @GetMapping(GET_USER_PROFILE_URL)
    fun getUserProfile(
        @AuthenticationPrincipal user: User,
        @RequestParam userId: UUID
    ): UserProfileDto =
        friendshipService.getUserProfile(user, userId)

    @GetMapping(GET_FRIENDS_URL)
    fun getFriendsList(@AuthenticationPrincipal user: User): List<FriendsListDto> =
        friendshipService.getFriendsList(user)

    @PostMapping(FRIEND_REQUESTS_URL)
    fun sendFriendRequest(
        @AuthenticationPrincipal user: User,
        @RequestParam username: String,
    ): ResponseEntity<String> =
        friendshipService.sendFriendRequest(user, username)

    @PatchMapping(FRIEND_RESPONSE_URL)
    fun updateFriendResponse(
        @AuthenticationPrincipal user: User,
        @RequestParam friendshipId: UUID,
        @RequestParam status: String
    ): ResponseEntity<String> =
        friendshipService.updateFriendResponse(user, friendshipId, status)

    @PostMapping(BLOCK_USER_URL)
    fun blockUser(
        @AuthenticationPrincipal user: User,
        @RequestParam userId: UUID
    ): ResponseEntity<String> =
        friendshipService.blockUser(user, userId)

    @DeleteMapping(DELETE_USER_URL)
    fun deleteFriendship(
        @AuthenticationPrincipal user: User,
        @RequestParam userId: UUID
    ): ResponseEntity<String> =
        friendshipService.deleteFriendship(user, userId)
}
