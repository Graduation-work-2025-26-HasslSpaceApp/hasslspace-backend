package ru.hse.hasslspace.notificationservice.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EmailRequest(
    @JsonProperty("subject")
    val subject: String,

    @JsonProperty("targetEmail")
    val targetEmail: String,

    @JsonProperty("text")
    val text: String,

    @JsonProperty("name")
    val name: String? = null
)
