package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.Server
import java.util.UUID

@Repository
interface ServerRepository : CrudRepository<Server, UUID> {

    @Query(
        """
            select *
            from server
            where id = :id
        """
    )
    fun findServerById(id: UUID): Server?

    @Query(
        """
            select *
            from server
            where id in (:id)
        """
    )
    fun findAllServersById(id: List<UUID>): List<Server>
}
