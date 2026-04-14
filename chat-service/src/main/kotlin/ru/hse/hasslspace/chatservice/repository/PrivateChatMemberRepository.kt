package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.PrivateChatMember
import java.util.UUID

@Repository
interface PrivateChatMemberRepository : CrudRepository<PrivateChatMember, PrivateChatMember.PrivateChatMemberId> {

    @Query(
        """
            insert into private_chat_member (chat_id, user_id)
            values (:#{#member.id.chatId}, :#{#member.id.userId})
            returning *
        """
    )
    fun save(member: PrivateChatMember): PrivateChatMember

    @Query(
        """
            select *
            from private_chat_member
            where user_id = :userId
        """
    )
    fun findAllByUserId(userId: UUID): List<PrivateChatMember>

    @Query(
        """
            select *
            from private_chat_member
            where chat_id = :chatId
        """
    )
    fun findAllByChatId(chatId: UUID): List<PrivateChatMember>
}