package ru.hse.hasslspace.serverservice.dto

data class CreateChannelRequest(
    val name: String,
    val type: String,
    val position: Int?,
    val maxMembers: Int?,
    val isPrivate: Boolean = false
)