package ru.hse.hasslspace.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.hse.hasslspace.userservice.dto.LoginUserDto
import ru.hse.hasslspace.userservice.dto.RegisterUserDto
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.service.AuthService
import kotlin.io.encoding.ExperimentalEncodingApi

@RestController
@RequestMapping(USER_SERVICE_BASE_PATH_URL)
@ExperimentalEncodingApi
class AuthController(
    private val authService: AuthService
) {

    @PostMapping(REGISTER_USER_URL)
    fun registerUser(@RequestBody registerDto: RegisterUserDto): ResponseEntity<String> =
        authService.registerUser(registerDto)

    @GetMapping(LOGIN_USER_URL)
    fun loginUser(@RequestParam email: String,
                  @RequestParam password: String): ResponseEntity<String> =
        authService.loginUser(email, password)

    @PostMapping(SEND_VERIFICATION_CODE_URL)
    fun sendVerificationCode(
        @AuthenticationPrincipal user: User?,
        @RequestParam(required = false) email: String?
    ): ResponseEntity<String> =
        authService.sendVerificationCode(user!!.id!!, email ?: user.email, user.name )

    @PostMapping(VERIFY_CODE_URL)
    fun verifyCode(
        @AuthenticationPrincipal user: User?,
        @RequestParam code: String
    ): ResponseEntity<String> =
        authService.verifyCode(user!!, code)

    @GetMapping(IS_VERIFIED_URL)
    fun isVerified(
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<Boolean> =
        authService.isVerified(user!!)
}