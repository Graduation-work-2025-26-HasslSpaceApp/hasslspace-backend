package ru.hse.hasslspace.serverservice.dto

data class RoleInfoDto(
    val id: String,
    val name: String,
    val color: String?,
    val position: Int?,
    val members: List<RoleMemberDto>,
) {
    data class RoleMemberDto(
        val id: String,
        val name: String,
        val username: String,
        val status: String,
        val photoUrl: String?,
    )
}
