package ru.hse.hasslspace.voiceservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "livekit")
class LiveKitConfig {
    lateinit var apiKey: String
    lateinit var apiSecret: String
    lateinit var host: String
    lateinit var url: String
}
