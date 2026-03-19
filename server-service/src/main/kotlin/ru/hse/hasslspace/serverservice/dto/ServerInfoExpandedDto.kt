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
}
