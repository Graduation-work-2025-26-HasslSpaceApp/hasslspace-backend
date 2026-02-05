package ru.hse.hasslspace.userservice.client.rest.api

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.hse.hasslspace.userservice.dto.EmailRequest


@FeignClient(name = "notification-service", path = "\${feign.notification-service-api.base-path}")
interface NotificationServiceApi {

     @PostMapping("\${feign.notification-service-api.endpoints.send-email}")
     fun sendEmail(
         @RequestBody emailRequest: EmailRequest,
     ): ResponseEntity<String>
}
