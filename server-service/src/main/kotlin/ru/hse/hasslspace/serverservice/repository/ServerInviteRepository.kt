package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.ServerInvite
import java.util.UUID

@Repository
interface ServerInviteRepository : CrudRepository<ServerInvite, String> {

    @Query(
        """
            insert into server_invite (code, server_id, creator_id, expires_at)
            values (:#{#invite.code}, :#{#invite.serverId}, :#{#invite.creatorId}, :#{#invite.expiresAt})
            returning *
        """
    )
    fun save(invite: ServerInvite): ServerInvite

    @Query(
        """
            select *
            from server_invite
            where server_id = :serverId
              and (expires_at is null or expires_at > now())
        """
    )
    fun findAllActiveByServerId(serverId: UUID): List<ServerInvite>
}