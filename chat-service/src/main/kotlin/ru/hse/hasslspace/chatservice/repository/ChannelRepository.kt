package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.Channel
import java.util.*

@Repository
interface ChannelRepository : CrudRepository<Channel, UUID> {

    @Query(
        """
            select server_id
            from public.channel
            where id = :channelId
        """
    )
    fun findServerIdByChannelId(channelId: UUID): UUID?
}
