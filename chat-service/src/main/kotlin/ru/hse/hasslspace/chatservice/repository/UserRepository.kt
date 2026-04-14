package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.User
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    @Query(
        """
            select *
            from public."user"
            where id in (:userIds)
        """
    )
    fun findAllUsersById(userIds: List<UUID>) : List<User>
}