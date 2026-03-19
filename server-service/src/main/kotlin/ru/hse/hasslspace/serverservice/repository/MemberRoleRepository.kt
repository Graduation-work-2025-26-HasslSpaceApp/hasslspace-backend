package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.MemberRole
import ru.hse.hasslspace.serverservice.model.MemberRole.MemberRoleId
import java.util.*

@Repository
interface MemberRoleRepository : CrudRepository<MemberRole, MemberRoleId> {
    //todo: надо подумать над ключом, мб такой вариант работать не будет
    // на редите нашел это https://spring.io/blog/2025/07/22/spring-data-jdbc-composite-id

    @Query(
        """
            insert into member_role (server_id, user_id, role_id)
            values (:#{#memberRole.id.serverId}, :#{#memberRole.id.userId}, :#{#memberRole.id.roleId})
            returning *
        """
    )
    fun save(memberRole: MemberRole): MemberRole

    @Query(
        """
        select role_id
        from member_role
        where server_id = :serverId and user_id = :userId
    """
    )
    fun findRoleIdsByServerIdAndUserId(serverId: UUID, userId: UUID): List<UUID>

    @Modifying
    @Query(
        """
            delete from member_role
            where server_id = :serverId and user_id = :userId
        """
    )
    fun deleteByServerIdAndUserId(serverId: UUID, userId: UUID): Int

    @Query(
        """
        select *
        from member_role
        where role_id = :roleId
    """
    )
    fun findAllByRoleId(roleId: UUID): List<MemberRole>

    @Modifying
    @Query(
        """
            delete from member_role
            where role_id = :roleId
        """
    )
    fun deleteByRoleId(roleId: UUID): Int

    @Query(
        """
        select *
        from member_role
        where server_id = :serverId and user_id = :userId and role_id = :roleId
    """
    )
    fun findByServerIdAndUserIdAndRoleId(serverId: UUID, userId: UUID, roleId: UUID): MemberRole?
}
