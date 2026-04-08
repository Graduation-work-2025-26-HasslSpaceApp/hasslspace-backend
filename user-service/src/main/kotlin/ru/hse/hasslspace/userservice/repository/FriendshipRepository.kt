package ru.hse.hasslspace.userservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.userservice.model.Friendship
import java.util.UUID

@Repository
interface FriendshipRepository : CrudRepository<Friendship, UUID> {

    @Query(
        """
            select *
            from friendship
            where (requester_id = :user1 and addressee_id = :user2) 
            or (requester_id = :user2 and addressee_id = :user1)
        """
    )
    fun findFriendshipBetweenUsers(user1: UUID, user2: UUID): Friendship?

    @Query("""
        select *
        from friendship
        where requester_id = :userId or addressee_id = :userId
    """)
    fun findFriendshipsByUserId(userId: UUID): List<Friendship>

    @Query(
        """
            select *
            from friendship
            where id = :friendshipId
        """
    )
    fun findFriendshipById(friendshipId: UUID): Friendship?
}
