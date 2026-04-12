package ru.hse.hasslspace.serverservice.dto

data class UpdateChannelDto(
    var name: String?,
    var type: String?,
    var position: Int?,
    var maxMembers: Int?,
    var isPrivate: Boolean?
)