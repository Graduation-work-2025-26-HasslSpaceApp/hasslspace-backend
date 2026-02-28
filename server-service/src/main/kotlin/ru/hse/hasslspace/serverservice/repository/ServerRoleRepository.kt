package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.ServerRole
import java.util.*

@Repository
interface ServerRoleRepository : CrudRepository<ServerRole, UUID> {
}
