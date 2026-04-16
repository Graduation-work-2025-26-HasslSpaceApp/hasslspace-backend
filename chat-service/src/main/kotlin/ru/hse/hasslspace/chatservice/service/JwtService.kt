package ru.hse.hasslspace.chatservice.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.hasslspace.chatservice.config.JwtProperties
import java.util.Date
import java.util.UUID

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    fun generateToken(userId: UUID): ResponseEntity<String> {
        return try {
            val token = Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration!!))
                .claim("userId", userId.toString())
                .issuer("chat-service")
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray()))
                .compact()

            logger.info("Generated JWT token for user with id $userId")

            ResponseEntity.ok(token)
        } catch (e: Exception) {
            logger.error("Error generating JWT token", e)
            ResponseEntity.badRequest().body("Ошибка при генерации токена")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtService::class.java)
    }
}
