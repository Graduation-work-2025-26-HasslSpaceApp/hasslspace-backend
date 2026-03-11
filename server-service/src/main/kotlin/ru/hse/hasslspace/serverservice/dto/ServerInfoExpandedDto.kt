package ru.hse.hasslspace.serverservice.dto

data class ServerInfoExpandedDto(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val members: List<ServerMemberDto>,
    val isOwner: Boolean,
    val textChannels: List<TextChannelDto>,
    val voiceChannels: List<VoiceChannelDto>,
) {
    data class TextChannelDto(
        val id: String,
        val name: String,
    )
    data class VoiceChannelDto(
        val id: String,
        val name: String,
    )
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
}
