package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.Friendship
import java.util.UUID

@Repository
interface FriendshipRepository : CrudRepository<Friendship, UUID> {

    @Query("""
        select *
        from friendship
        where requester_id = :userId or addressee_id = :userId
    """)
    fun findFriendshipsByUserId(userId: UUID): List<Friendship>
}