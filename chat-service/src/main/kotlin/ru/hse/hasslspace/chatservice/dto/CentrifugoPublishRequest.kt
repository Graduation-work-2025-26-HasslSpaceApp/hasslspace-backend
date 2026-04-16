package ru.hse.hasslspace.chatservice.dto

import ru.hse.hasslspace.chatservice.model.Message

data class CentrifugoPublishRequest(
    val method: String,
    val params: CentrifugoPublishParams
) {

    data class CentrifugoPublishParams(
        val channel: String,
        val data: Message
    )
}