package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.Message
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface MessageRepository : CrudRepository<Message, UUID> {

    @Query(
        """
            select *
            from message
            where chat_id = :chatId
            and created_at >= :fromDate
            order by created_at asc
        """
    )
    fun findByChatIdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(
        chatId: UUID,
        fromDate: LocalDateTime,
        limit: Int
    ): List<Message>


    @Query(
        """
            select *
            from message
            where chat_id = :chatId
            and created_at >= :fromDate
            and created_at <= :toDate
            order by created_at asc
        """
    )
    fun findByChatIdAndCreatedAtBetweenOrderByCreatedAtAsc(
        chatId: UUID,
        fromDate: LocalDateTime,
        toDate: LocalDateTime,
        limit: Int
    ): List<Message>


    @Query(
        """
            select *
            from message
            where chat_id = :chatId
            and created_at <= :toDate
            order by created_at asc
        """
    )
    fun findByChatIdAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
        chatId: UUID,
        toDate: LocalDateTime,
        limit: Int
    ) : List<Message>

    @Query(
        """
            select *
            from message
            where chat_id = :chatId
            order by created_at asc
            limit :limit
        """
    )
    fun findLastMessages(
        chatId: UUID,
        limit: Int
    ): List<Message>
}
