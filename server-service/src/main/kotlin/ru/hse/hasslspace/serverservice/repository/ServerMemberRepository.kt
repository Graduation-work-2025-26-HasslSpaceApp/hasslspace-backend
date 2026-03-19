package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.Server
import ru.hse.hasslspace.serverservice.model.ServerMember
import java.util.UUID

@Repository
interface ServerMemberRepository : CrudRepository<ServerMember, ServerMember.ServerMemberId> {
    //todo: надо подумать над ключом, мб такой вариант работать не будет
    // на редите нашел это https://spring.io/blog/2025/07/22/spring-data-jdbc-composite-id

    @Query(
        """
            insert into server_member (server_id, user_id, joined_at, name)
            values (:#{#serverMember.id.serverId}, :#{#serverMember.id.userId}, :#{#serverMember.joinedAt}, :#{#serverMember.name})
            returning *
        """
    )
    fun save(serverMember: ServerMember): ServerMember

    @Query(
        """
            select *
            from server_member
            where server_id = :serverId and user_id = :userId
        """
    )
    fun findByServerIdAndUserId(serverId: UUID, userId: UUID): ServerMember?

    @Query(
        """
                select *
                from server_member
                where server_id = :serverId
            """
    )
    fun findAllByServerId(serverId: UUID): List<ServerMember>

    @Query(
        """
                select *
                from server_member
                where user_id = :userId
        """
    )
    fun findAllByUserId(userId: UUID): List<ServerMember>
}
