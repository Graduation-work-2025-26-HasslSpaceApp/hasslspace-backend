package ru.hse.hasslspace.userservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.userservice.model.Friendship
import java.util.UUID

@Repository
interface FriendshipRepository : CrudRepository<Friendship, UUID> {

}
