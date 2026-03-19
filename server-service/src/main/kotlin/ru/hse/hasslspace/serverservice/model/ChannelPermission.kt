package ru.hse.hasslspace.serverservice.model

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.serverservice.model.ChannelPermission.Companion.TABLE_NAME
import java.util.*

@Table(TABLE_NAME)
data class ChannelPermission(
    @Column(CHANNEL_ID_COLUMN_NAME)
    val channelId: UUID,

    @Column(ROLE_ID_COLUMN_NAME)
    val roleId: UUID,

    @Column(CAN_READ_COLUMN_NAME)
    var canRead: Boolean? = null,

    @Column(CAN_WRITE_COLUMN_NAME)
    var canWrite: Boolean? = null,

    @Column(CAN_MANAGE_COLUMN_NAME)
    var canManage: Boolean? = null
) {

    companion object {
        const val TABLE_NAME = "channel_permission"

        const val CHANNEL_ID_COLUMN_NAME = "channel_id"
        const val ROLE_ID_COLUMN_NAME = "role_id"
        const val CAN_READ_COLUMN_NAME = "can_read"
        const val CAN_WRITE_COLUMN_NAME = "can_write"
        const val CAN_MANAGE_COLUMN_NAME = "can_manage"
    }
}
