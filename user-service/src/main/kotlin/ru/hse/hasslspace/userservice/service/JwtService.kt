package ru.hse.hasslspace.userservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.hse.hasslspace.userservice.config.JwtProperties
import ru.hse.hasslspace.userservice.model.User
import java.util.*
import javax.crypto.SecretKey
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
@ExperimentalEncodingApi
class JwtService(
    private val jwtProperties: JwtProperties,
) {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(Base64.decode(jwtProperties.secret))

    init {
        logger.info("Secret key: ${Base64.encode(secretKey.encoded)}")
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

    fun generateToken(userDetails: User): String {
        val claims = mutableMapOf<String, Any>()
        claims["role"] = userDetails.authorities.firstOrNull()?.authority ?: "DEFAULT"
        claims["userId"] = userDetails.id.toString()
        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
            .issuer("spring-security")
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return (!isTokenExpired(token))
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtService::class.java)
    }
}