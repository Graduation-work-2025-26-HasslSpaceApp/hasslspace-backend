package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.Channel
import java.util.*

@Repository
interface ChannelRepository : CrudRepository<Channel, UUID> {

    @Query(
        """
            select *
            from channel
            where server_id = :serverId
            order by position
        """
    )
    fun findAllByServerIdOrderByPosition(serverId: UUID): List<Channel>
}