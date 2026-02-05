package ru.hse.hasslspace.userservice.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["ru.hse.hasslspace.userservice.client.rest.api"])
class FeignClientConfiguration
