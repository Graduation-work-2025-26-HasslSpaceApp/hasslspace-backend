package ru.hse.hasslspace.voiceservice.config

import io.livekit.server.AccessToken
import io.livekit.server.CanPublish
import io.livekit.server.CanSubscribe
import io.livekit.server.RoomJoin
import io.livekit.server.RoomName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "livekit")
class LiveKitConfig {
    lateinit var apiKey: String
    lateinit var apiSecret: String
    lateinit var host: String
    lateinit var url: String

    // TODO: доделать, перенести наверное в сервис, а не в конфиг
    fun generateToken(roomName: String, identity: String): String {
        val token = AccessToken(apiKey, apiSecret)
        token.identity = identity
        token.name = identity

        token.addGrants(
            RoomJoin(true),
            RoomName(roomName),
            CanPublish(true),
            CanSubscribe(true)
        )

        return token.toJwt()
    }
}
