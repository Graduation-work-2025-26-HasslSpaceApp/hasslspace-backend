package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.ServerRole
import java.util.*

@Repository
interface ServerRoleRepository : CrudRepository<ServerRole, UUID> {

    @Query(
        """
            select *
            from server_role
            where id in (:roleIds)
        """
    )
    fun findAllRolesById(roleIds: List<UUID>): List<ServerRole>
}
