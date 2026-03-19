package ru.hse.hasslspace.serverservice.dto

data class ServerMemberDto(
    val id: String,
    val name: String,
    val username: String,
    val status: String,
    val photoUrl: String?,
    val roles: List<ServerRoleDto>? = null,
) {
    data class ServerRoleDto(
        val id: String,
        val name: String,
        val color: String,
    )
}