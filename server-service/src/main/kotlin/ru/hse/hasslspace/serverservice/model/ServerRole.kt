package ru.hse.hasslspace.serverservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.hse.hasslspace.serverservice.model.ServerRole.Companion.TABLE_NAME
import java.util.*

@Table(TABLE_NAME)
data class ServerRole(
    @Id
    @Column(ID_COLUMN_NAME)
    val id: UUID? = null,

    @Column(SERVER_ID_COLUMN_NAME)
    val serverId: UUID,

    @Column(NAME_COLUMN_NAME)
    var name: String,

    @Column(POSITION_COLUMN_NAME)
    var position: Int? = null,

    @Column(COLOR_COLUMN_NAME)
    var color: String? = null,

    @Column(IS_DEFAULT_COLUMN_NAME)
    var isDefault: Boolean = false
) {

    companion object {
        const val TABLE_NAME = "server_role"

        const val ID_COLUMN_NAME = "id"
        const val SERVER_ID_COLUMN_NAME = "server_id"
        const val NAME_COLUMN_NAME = "name"
        const val POSITION_COLUMN_NAME = "position"
        const val COLOR_COLUMN_NAME = "color"
        const val IS_DEFAULT_COLUMN_NAME = "is_default"
    }
}