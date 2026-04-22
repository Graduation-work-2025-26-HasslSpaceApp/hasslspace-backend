package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.ChannelPermission
import java.util.*

@Repository
interface ChannelPermissionRepository :
    CrudRepository<ChannelPermission, ChannelPermission.ChannelPermissionId> {
    //todo: надо подумать над ключом, мб такой вариант работать не будет
    // на редите нашел это https://spring.io/blog/2025/07/22/spring-data-jdbc-composite-id

    @Query(
        """
            insert into channel_permission (channel_id, role_id, can_read, can_write, can_manage)
            values (:#{#channelPermission.id.channelId}, :#{#channelPermission.id.roleId}, :#{#channelPermission.canRead}, :#{#channelPermission.canWrite}, :#{#channelPermission.canManage})
            returning * 
        """
    )
    fun save(channelPermission: ChannelPermission): ChannelPermission

    @Query(
        """
            select *
            from channel_permission
            where channel_id = :channelId and role_id = :roleId
        """
    )
    fun findByChannelIdAndRoleId(channelId: UUID, roleId: UUID): ChannelPermission?

    @Query(
        """
            select *
            from channel_permission
            where channel_id = :channelId
        """
    )
    fun findByChannelId(channelId: UUID): List<ChannelPermission>

    @Modifying
    @Query(
        """
            delete from channel_permission
            where channel_id = :channelId
        """
    )
    fun deleteByChannelId(channelId: UUID): Int
}