package ru.hse.hasslspace.userservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.userservice.model.User
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    fun findUserByUsername(username: String) : UserDetails
}
