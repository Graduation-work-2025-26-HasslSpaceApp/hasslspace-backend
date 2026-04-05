package ru.hse.hasslspace.serverservice.dto

data class CreateServerRequest(
    val name: String,
    val iconUrl: String? = null,
)
