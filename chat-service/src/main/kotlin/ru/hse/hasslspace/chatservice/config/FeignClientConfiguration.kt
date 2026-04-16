package ru.hse.hasslspace.chatservice.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["ru.hse.hasslspace.chatservice.client.rest.api"])
class FeignClientConfiguration
