package ru.hse.hasslspace.userservice.dto

data class UpdateProfileDto(
    val username: String?,
    val name: String?,
    val photoUrl: String?,
    val description: String?,
)
