package ru.hse.hasslspace.serverservice.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hse.hasslspace.serverservice.model.ChannelPermission
import java.util.*

@Repository
interface ChannelPermissionRepository : CrudRepository<ChannelPermission, Map<String, UUID>> {
    //todo: надо подумать над ключом, мб такой вариант работать не будет
    // на редите нашел это https://spring.io/blog/2025/07/22/spring-data-jdbc-composite-id

    @Modifying
    @Query(
        """
            delete from channel_permission
            where channel_id = :channelId
        """
    )
    fun deleteByChannelId(channelId: UUID): Int
}