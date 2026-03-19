package ru.hse.hasslspace.userservice.dto

data class EmailRequest(
    val subject: String,
    val targetEmail: String,
    val text: String,
    val name: String? = null
)
