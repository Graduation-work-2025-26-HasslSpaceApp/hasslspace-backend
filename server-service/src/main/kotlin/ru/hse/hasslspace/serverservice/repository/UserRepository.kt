package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.User
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    @Query(
        """
            select *
            from public."user"
            where username = :username
        """
    )
    fun findUserByUsername(username: String) : User?

    @Query(
        """
            select exists(
                select 1
                from public."user"
                where email = :email
            )
        """
    )
    fun existsByEmail(email: String): Boolean

    @Query(
        """
            select exists(
                select 1
                from public."user"
                where username = :username
            )
        """
    )
    fun existsByUsername(username: String): Boolean

    @Query(
        """
            select *
            from public."user"
            where email = :email
        """
    )
    fun findUserByEmail(email: String) : User

    @Query(
        """
            select *
            from public."user"
            where id = :userId
        """
    )
    fun findUserByUserId(userId: UUID) : User
}