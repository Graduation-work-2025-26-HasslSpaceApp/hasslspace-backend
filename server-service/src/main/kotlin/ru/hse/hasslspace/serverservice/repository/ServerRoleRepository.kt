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

    @Query(
        """
            select *
            from server_role
            where server_id = :serverId
              and is_default = true
            limit 1
        """
    )
    fun findDefaultRoleByServerId(serverId: UUID): ServerRole?

    @Query(
        """
            select *
            from server_role
            where server_id = :serverId
        """
    )
    fun findAllByServerId(serverId: UUID): List<ServerRole>

    @Query(
        """
            select id
            from server_role
            where server_id = :serverId
              and position = 1
            limit 1
        """
    )
    fun findAdminRoleIdByServerId(serverId: UUID): UUID
}
