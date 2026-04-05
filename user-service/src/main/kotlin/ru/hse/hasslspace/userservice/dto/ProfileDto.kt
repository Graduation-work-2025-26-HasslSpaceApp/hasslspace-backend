package ru.hse.hasslspace.userservice.dto

import java.util.UUID

data class ProfileDto(
    val id: UUID,
    val username: String,
    val name: String,
    val email: String,
    val status: String,
    val photoUrl: String?,
    val description: String?
)
