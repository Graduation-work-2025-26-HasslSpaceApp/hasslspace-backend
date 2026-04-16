package ru.hse.hasslspace.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.chatservice.model.ServerMember
import java.util.UUID

@Repository
interface ServerMemberRepository : CrudRepository<ServerMember, ServerMember.ServerMemberId> {

    @Query(
        """
            select exists(
                select 1
                from public.server_member
                where server_id = :serverId and user_id = :userId
            )
        """
    )
    fun existsByServerIdAndUserId(serverId: UUID, userId: UUID): Boolean
}
