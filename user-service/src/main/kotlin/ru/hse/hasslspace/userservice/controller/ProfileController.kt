package ru.hse.hasslspace.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.hse.hasslspace.userservice.dto.ProfileDto
import ru.hse.hasslspace.userservice.dto.UpdateProfileDto
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.service.ProfileService

@RestController
@RequestMapping(USER_SERVICE_BASE_PATH_URL)
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping(GET_PROFILE_URL)
    fun getProfile(@AuthenticationPrincipal user: User?): ProfileDto =
        profileService.getProfile(user!!)


    @PatchMapping(UPDATE_PROFILE_URL)
    fun updateProfile(
        @AuthenticationPrincipal user: User?,
        @RequestBody updateProfileDto: UpdateProfileDto
    ): ResponseEntity<String> =
        profileService.updateProfile(user!!, updateProfileDto)

    @PatchMapping(UPDATE_STATUS_URL)
    fun updateStatus(
        @AuthenticationPrincipal user: User?,
        @RequestParam status: String
    ): ResponseEntity<String> =
        profileService.updateStatus(user!!, status)
}