package ru.hse.hasslspace.userservice.dto

data class RegisterUserDto(
    val email: String,
    val username: String,
    val password: String
)
