package ru.hse.hasslspace.userservice.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.hse.hasslspace.userservice.model.User
import ru.hse.hasslspace.userservice.repository.UserRepository

@Service
class NewUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findUserByUsername(username)
            ?: throw UsernameNotFoundException("User with username $username not found")
    }

    fun loadUserByEmail(email: String): User {
        return userRepository.findUserByEmail(email)
    }
}