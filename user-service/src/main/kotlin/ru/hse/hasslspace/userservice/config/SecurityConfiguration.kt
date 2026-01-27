package ru.hse.hasslspace.userservice.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.hse.hasslspace.userservice.filter.JwtRequestFilter
import ru.hse.hasslspace.userservice.service.NewUserDetailsService

import kotlin.io.encoding.ExperimentalEncodingApi

@Configuration
@ExperimentalEncodingApi
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfiguration(
    private val newUserDetailsService: NewUserDetailsService,
    private val jwtRequestFilter: JwtRequestFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        return DaoAuthenticationProvider(newUserDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { c ->
                c
                    .anyRequest().permitAll()//authenticated()
            }
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
            .userDetailsService(newUserDetailsService)

        return http.build()
    }
}
