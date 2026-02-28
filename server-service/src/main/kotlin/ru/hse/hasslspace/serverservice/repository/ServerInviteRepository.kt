package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.ServerInvite

@Repository
interface ServerInviteRepository : CrudRepository<ServerInvite, String> {
}