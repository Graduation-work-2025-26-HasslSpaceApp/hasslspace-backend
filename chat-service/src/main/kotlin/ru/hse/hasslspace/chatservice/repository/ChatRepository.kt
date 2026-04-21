package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.Chat
import java.util.UUID

@Repository
interface ChatRepository : CrudRepository<Chat, UUID> {

    @Query(
        """
            select exists(
                select 1
                from public.chat
                where channel_id = :channelId
            )
        """
    )
    fun existsByChannelId(channelId: UUID): Boolean

    @Query(
        """
            select *
            from public.chat
            where channel_id = :channelId
        """
    )
    fun findByChannelId(channelId: UUID): Chat?
}
