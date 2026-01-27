package ru.hse.hasslspace.userservice.repository

import org.springframework.data.repository.CrudRepository
import ru.hse.hasslspace.userservice.model.Friendship
import java.util.UUID

interface FriendshipRepository : CrudRepository<Friendship, UUID> {

}
