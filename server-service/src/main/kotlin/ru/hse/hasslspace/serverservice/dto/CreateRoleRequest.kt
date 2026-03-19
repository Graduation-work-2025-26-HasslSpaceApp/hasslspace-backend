package ru.hse.hasslspace.serverservice.dto

data class CreateRoleRequest(
    val name: String,
    val color: String?,
    val position: Int?
)