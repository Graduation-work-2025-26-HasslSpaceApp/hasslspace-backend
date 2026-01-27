package ru.hse.hasslspace.userservice.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.hse.hasslspace.userservice.repository.UserRepository

@Service
class NewUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findUserByUsername(username)
    }
}