package ru.hse.hasslspace.serverservice.dto

data class ChannelDto(
    val id: String,
    val name: String,
    val type: String,
    val position: Int?,
    val maxMembers: Int?,
    val isPrivate: Boolean
)
