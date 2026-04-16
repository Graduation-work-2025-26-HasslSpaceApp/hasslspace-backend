package ru.hse.hasslspace.chatservice.client.rest.api

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import ru.hse.hasslspace.chatservice.dto.CentrifugoPublishRequest

@FeignClient(name = "centrifugo-service")
interface CentrifugoApi {

    @PostMapping("\${feign.centrifugo-api.endpoints.api}")
    fun publish(
        @RequestBody request: CentrifugoPublishRequest,
        @RequestHeader(API_KEY_HEADER) apiKey: String
    ): ResponseEntity<String>

    companion object {
        const val API_KEY_HEADER = "X-API-KEY"
    }
}
